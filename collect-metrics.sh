#!/usr/bin/env bash

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

IMPLEMENTATION=$1

if [ -z "$IMPLEMENTATION" ]; then
    echo "Error: Specify implementation"
    echo "Usage: $0 [java|clojure-idiomatic|clojure-optimized]"
    exit 1
fi

PROMETHEUS_URL="http://localhost:9090"

mkdir -p metrics-results

echo "═══════════════════════════════════════════════════"
echo "  METRICS COLLECTION - ${IMPLEMENTATION^^}"
echo "═══════════════════════════════════════════════════"
echo ""

echo "[STEP 1] Collecting initial metrics..."
echo ""

GC_TIME_INITIAL=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=jvm_gc_pause_seconds_sum" | jq -r '.data.result[0].value[1]')
GC_COUNT_INITIAL=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=jvm_gc_pause_seconds_count" | jq -r '.data.result[0].value[1]')
REQUESTS_INITIAL=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=http_server_requests_seconds_count" | jq -r '.data.result | map(.value[1] | tonumber) | add')

echo "  GC Time Initial:        $GC_TIME_INITIAL seconds"
echo "  GC Collections Initial: $GC_COUNT_INITIAL"
echo "  Requests Initial:       $REQUESTS_INITIAL"
echo ""
echo "  ℹ These initial values include health checks and Prometheus scraping"
echo "    They will be subtracted from final metrics"
echo ""

echo "[STEP 2] Ready to run test"
echo ""
echo "Press ENTER after configuring Gatling for $IMPLEMENTATION..."
echo ""
echo "Execute now: ./run-gatling.sh"
echo ""
read -p "Press ENTER after test finishes..."
echo ""

echo "[STEP 3] Waiting for Prometheus synchronization..."
echo ""
echo "  Waiting 15 seconds for final metrics sync..."
sleep 15

echo "[STEP 4] Collecting final metrics..."
echo ""

GC_TIME_FINAL=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=jvm_gc_pause_seconds_sum" | jq -r '.data.result[0].value[1]')
GC_COUNT_FINAL=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=jvm_gc_pause_seconds_count" | jq -r '.data.result[0].value[1]')
REQUESTS_FINAL=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=http_server_requests_seconds_count" | jq -r '.data.result | map(.value[1] | tonumber) | add')

# Heap - Query do ANTIGO (que funcionava)
HEAP_PEAK=$(curl -s -G "${PROMETHEUS_URL}/api/v1/query" \
  --data-urlencode 'query=max(max_over_time(jvm_memory_used_bytes{area="heap"}[10m]))' \
  | jq -r '.data.result[0].value[1]')

# CPU - Query do ANTIGO (que funcionava)
CPU_PEAK=$(curl -s -G "${PROMETHEUS_URL}/api/v1/query" \
  --data-urlencode 'query=max(max_over_time(process_cpu_usage[10m])) * 100' \
  | jq -r '.data.result[0].value[1]')

echo "  GC Time Final:          $GC_TIME_FINAL seconds"
echo "  GC Collections Final:   $GC_COUNT_FINAL"
echo "  Requests Final:         $REQUESTS_FINAL"
echo "  Heap Peak:              $HEAP_PEAK bytes"
echo "  CPU Peak:               $CPU_PEAK %"
echo ""

echo "[CALCULATIONS]"
echo ""

GC_TIME_MS=$(echo "($GC_TIME_FINAL - $GC_TIME_INITIAL) * 1000" | bc)
GC_COUNT=$(echo "$GC_COUNT_FINAL - $GC_COUNT_INITIAL" | bc)
TOTAL_REQUESTS=$(echo "$REQUESTS_FINAL - $REQUESTS_INITIAL" | bc)
HEAP_GB=$(echo "scale=2; $HEAP_PEAK / 1024 / 1024 / 1024" | bc)
HEAP_PERCENT=$(echo "scale=2; ($HEAP_PEAK / 6442450944) * 100" | bc)
CPU_PEAK_FORMATTED=$(printf "%.2f" "$CPU_PEAK")

echo "  GC Time (test):         $GC_TIME_MS ms"
echo "  GC Collections (test):  $GC_COUNT"
echo "  Total Requests:         $TOTAL_REQUESTS"
echo "  Heap Peak:              $HEAP_GB GB"
echo "  Heap Peak (%):          $HEAP_PERCENT %"
echo "  CPU Peak:               $CPU_PEAK_FORMATTED %"
echo ""

# Coleta de erros Gatling
echo "[VALIDATION] Enter Gatling metrics for cross-check:"
echo "(Check Gatling HTML report - Global Information section)"
echo ""

read -p "Total requests sent by Gatling: " GATLING_TOTAL
read -p "Successful requests (OK): " GATLING_SUCCESS
read -p "Failed requests (KO): " GATLING_FAILED

echo ""

if [ "$GATLING_TOTAL" -gt 0 ]; then
    GATLING_ERROR_RATE=$(echo "scale=3; ($GATLING_FAILED / $GATLING_TOTAL) * 100" | bc)
else
    GATLING_ERROR_RATE="0"
fi

DELTA=$(echo "$GATLING_SUCCESS - $TOTAL_REQUESTS" | bc)
if [ "$GATLING_SUCCESS" -gt 0 ]; then
    DELTA_PERCENT=$(echo "scale=3; (($DELTA) / $GATLING_SUCCESS) * 100" | bc | sed 's/-//')
else
    DELTA_PERCENT="0"
fi

echo "  ═══════════════════════════════════════════════════"
echo "  Prometheus vs Gatling Comparison:"
echo "  ═══════════════════════════════════════════════════"
echo "    Prometheus Successful:  $TOTAL_REQUESTS"
echo "    Gatling Total:          $GATLING_TOTAL"
echo "    Gatling Successful:     $GATLING_SUCCESS"
echo "    Gatling Failed:         $GATLING_FAILED"
echo ""
echo "    Prometheus-Gatling Delta: $DELTA requests"
echo "    Delta Percentage:         $DELTA_PERCENT%"
echo "    Gatling Error Rate:       $GATLING_ERROR_RATE%"
echo ""

if (( $(echo "$DELTA_PERCENT < 0.1" | bc -l) )); then
    echo -e "    Sync Status:     ${GREEN}✓ EXCELLENT${NC} (< 0.1%)"
    SYNC_STATUS="EXCELLENT"
elif (( $(echo "$DELTA_PERCENT < 1" | bc -l) )); then
    echo -e "    Sync Status:     ${GREEN}✓ GOOD${NC} (< 1%)"
    SYNC_STATUS="GOOD"
elif (( $(echo "$DELTA_PERCENT < 2" | bc -l) )); then
    echo -e "    Sync Status:     ${YELLOW}⚠ ACCEPTABLE${NC} (1-2%)"
    SYNC_STATUS="ACCEPTABLE"
else
    echo -e "    Sync Status:     ${RED}✗ WARNING${NC} (> 2% - investigate timing)"
    SYNC_STATUS="WARNING"
fi

echo ""
if (( $(echo "$GATLING_ERROR_RATE < 0.5" | bc -l) )); then
    echo -e "    Error Status:    ${GREEN}✓ EXCELLENT${NC} (< 0.5%)"
    ERROR_STATUS="EXCELLENT"
elif (( $(echo "$GATLING_ERROR_RATE < 1" | bc -l) )); then
    echo -e "    Error Status:    ${GREEN}✓ GOOD${NC} (< 1%)"
    ERROR_STATUS="GOOD"
elif (( $(echo "$GATLING_ERROR_RATE < 2" | bc -l) )); then
    echo -e "    Error Status:    ${YELLOW}⚠ ACCEPTABLE${NC} (1-2%)"
    ERROR_STATUS="ACCEPTABLE"
else
    echo -e "    Error Status:    ${RED}✗ HIGH${NC} (> 2% - investigate!)"
    ERROR_STATUS="HIGH"
fi
echo "  ═══════════════════════════════════════════════════"
echo ""

TIMESTAMP=$(date +"%Y%m%d-%H%M%S")
RESULT_FILE="metrics-results/metrics-${IMPLEMENTATION}-${TIMESTAMP}.txt"

# Heap Available (6 GB fixo da config JVM)
HEAP_AVAILABLE_GB="6"

cat > "$RESULT_FILE" << EOF
═══════════════════════════════════════════════════
RESULTS - ${IMPLEMENTATION^^}
Date: $(date +"%Y-%m-%d %H:%M:%S")
═══════════════════════════════════════════════════

RAW METRICS (PROMETHEUS):
────────────────────────────────────────────────
CPU Peak:                 $CPU_PEAK_FORMATTED %
Heap Available:           $HEAP_AVAILABLE_GB GB
Heap Peak:                $HEAP_GB GB - $HEAP_PEAK bytes
GC Time Initial:          $GC_TIME_INITIAL seconds
GC Time Final:            $GC_TIME_FINAL seconds
GC Collections Initial:   $GC_COUNT_INITIAL
GC Collections Final:     $GC_COUNT_FINAL
Requests Initial:         $REQUESTS_INITIAL
Requests Final:           $REQUESTS_FINAL

CALCULATED METRICS:
────────────────────────────────────────────────
CPU Peak:                 $CPU_PEAK_FORMATTED %
Heap Peak:                $HEAP_GB GB
Heap Peak (%):            $HEAP_PERCENT %
GC Time (test):           $GC_TIME_MS ms
GC Collections (test):    $GC_COUNT
Total Requests (Prom):    $TOTAL_REQUESTS

GATLING METRICS:
────────────────────────────────────────────────
Total Requests:           $GATLING_TOTAL
Successful Requests:      $GATLING_SUCCESS
Failed Requests:          $GATLING_FAILED
Error Rate:               $GATLING_ERROR_RATE %

VALIDATION:
────────────────────────────────────────────────
Prometheus-Gatling Delta: $DELTA requests ($DELTA_PERCENT%)
Sync Status:              $SYNC_STATUS
Error Status:             $ERROR_STATUS

INTERPRETATION:
────────────────────────────────────────────────
EOF

if (( $(echo "$HEAP_PERCENT < 30" | bc -l) )); then
    echo "Heap Usage: ✓ EXCELLENT (< 30%)" >> "$RESULT_FILE"
elif (( $(echo "$HEAP_PERCENT < 50" | bc -l) )); then
    echo "Heap Usage: ✓ GOOD (30-50%)" >> "$RESULT_FILE"
elif (( $(echo "$HEAP_PERCENT < 70" | bc -l) )); then
    echo "Heap Usage: ⚠ MODERATE (50-70%)" >> "$RESULT_FILE"
else
    echo "Heap Usage: ✗ HIGH (> 70%)" >> "$RESULT_FILE"
fi

if (( $(echo "$GC_TIME_MS < 500" | bc -l) )); then
    echo "GC Overhead: ✓ EXCELLENT (< 500ms)" >> "$RESULT_FILE"
elif (( $(echo "$GC_TIME_MS < 2000" | bc -l) )); then
    echo "GC Overhead: ✓ ACCEPTABLE (500-2000ms)" >> "$RESULT_FILE"
elif (( $(echo "$GC_TIME_MS < 5000" | bc -l) )); then
    echo "GC Overhead: ⚠ MODERATE (2-5s)" >> "$RESULT_FILE"
else
    echo "GC Overhead: ✗ HIGH (> 5s)" >> "$RESULT_FILE"
fi

if (( $(echo "$CPU_PEAK_FORMATTED < 70" | bc -l) )); then
    echo "CPU Peak: ✓ GOOD (< 70%)" >> "$RESULT_FILE"
elif (( $(echo "$CPU_PEAK_FORMATTED < 90" | bc -l) )); then
    echo "CPU Peak: ⚠ HIGH (70-90%)" >> "$RESULT_FILE"
else
    echo "CPU Peak: ✗ CRITICAL (> 90%)" >> "$RESULT_FILE"
fi

if (( $(echo "$GATLING_ERROR_RATE < 0.5" | bc -l) )); then
    echo "Error Rate: ✓ EXCELLENT (< 0.5%)" >> "$RESULT_FILE"
elif (( $(echo "$GATLING_ERROR_RATE < 1" | bc -l) )); then
    echo "Error Rate: ✓ GOOD (< 1%)" >> "$RESULT_FILE"
elif (( $(echo "$GATLING_ERROR_RATE < 2" | bc -l) )); then
    echo "Error Rate: ⚠ ACCEPTABLE (1-2%)" >> "$RESULT_FILE"
else
    echo "Error Rate: ✗ HIGH (> 2%)" >> "$RESULT_FILE"
fi

echo ""
echo "Results saved in: $RESULT_FILE"
echo ""

CSV_FILE="metrics-results/metrics-comparison.csv"

if [ ! -f "$CSV_FILE" ]; then
    echo "Implementation,Timestamp,Prometheus_Success,Gatling_Total,Gatling_Success,Gatling_Failed,Error_Rate_%,Prom_Gatling_Delta_%,GC_Time_ms,GC_Collections,Heap_Peak_GB,Heap_Peak_%,CPU_Peak_%,Sync_Status,Error_Status" > "$CSV_FILE"
fi

echo "${IMPLEMENTATION},${TIMESTAMP},${TOTAL_REQUESTS},${GATLING_TOTAL},${GATLING_SUCCESS},${GATLING_FAILED},${GATLING_ERROR_RATE},${DELTA_PERCENT},${GC_TIME_MS},${GC_COUNT},${HEAP_GB},${HEAP_PERCENT},${CPU_PEAK_FORMATTED},${SYNC_STATUS},${ERROR_STATUS}" >> "$CSV_FILE"

echo "═══════════════════════════════════════════════════"
echo "  SUMMARY"
echo "═══════════════════════════════════════════════════"
echo ""
echo "  Implementation:                ${IMPLEMENTATION}"
echo "  Total Requests (Prometheus):   $TOTAL_REQUESTS"
echo "  Total Requests (Gatling):      $GATLING_TOTAL"
echo "  Successful Requests:           $GATLING_SUCCESS"
echo "  Failed Requests:               $GATLING_FAILED"
echo "  Error Rate:                    $GATLING_ERROR_RATE%"
echo "  GC Time:                       $GC_TIME_MS ms"
echo "  GC Collections:                $GC_COUNT"
echo "  Heap Peak:                     $HEAP_GB GB"
echo "  Heap Peak (%):                 $HEAP_PERCENT%"
echo "  CPU Peak:                      $CPU_PEAK_FORMATTED%"
echo ""
echo "═══════════════════════════════════════════════════"
echo ""
echo "Data added to CSV: $CSV_FILE"
echo ""
echo "Next steps:"
echo "  1. Wait 5 minutes for stabilization"
echo "  2. Run next implementation:"
echo -e "     ${GREEN}./collect-metrics.sh [java|clojure-idiomatic|clojure-optimized]${NC}"
echo ""
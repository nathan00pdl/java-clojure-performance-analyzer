#!/usr/bin/env bash

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

IMPLEMENTATION=$1

if [ -z "$IMPLEMENTATION" ]; then
    echo "Error: Specify implementation"
    echo "Usage: $0 [java|clojure-idiomatic|clojure-interop-java]"
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


HEAP_PEAK=$(curl -s -G "${PROMETHEUS_URL}/api/v1/query" \
  --data-urlencode 'query=max(max_over_time(jvm_memory_used_bytes{area="heap"}[10m]))' \
  | jq -r '.data.result[0].value[1]')


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
HEAP_GB_RAW=$(echo "scale=2; $HEAP_PEAK / 1024 / 1024 / 1024" | bc)

if [[ $HEAP_GB_RAW =~ ^\. ]]; then
    HEAP_GB="0${HEAP_GB_RAW}"
else
    HEAP_GB="$HEAP_GB_RAW"
fi
HEAP_PERCENT=$(echo "scale=2; ($HEAP_PEAK / 6442450944) * 100" | bc)
CPU_PEAK_FORMATTED=$(printf "%.2f" "$CPU_PEAK")

echo "  GC Time (test):         $GC_TIME_MS ms"
echo "  GC Collections (test):  $GC_COUNT"
echo "  Total Requests:         $TOTAL_REQUESTS"
echo "  Heap Peak:              $HEAP_GB GB"
echo "  Heap Peak (%):          $HEAP_PERCENT %"
echo "  CPU Peak:               $CPU_PEAK_FORMATTED %"
echo ""


echo "[STEP 5] Collecting Gatling metrics..."
echo ""
echo -e "${BLUE}Option 1:${NC} Provide path to stats.json for automatic extraction"
echo "           Example: gatling-results/compoundinterestsimulation-20251210123456/js/stats.json"
echo ""
echo -e "${BLUE}Option 2:${NC} Press ENTER to input all metrics manually"
echo ""
read -p "Path to stats.json (or ENTER for manual): " GATLING_STATS_PATH


AUTO_EXTRACTED=false

if [ -n "$GATLING_STATS_PATH" ] && [ -f "$GATLING_STATS_PATH" ]; then
    echo ""
    echo -e "  ${GREEN}✓ File found, extracting all metrics automatically...${NC}"
    echo ""
    
    GATLING_TOTAL=$(jq -r '.stats.numberOfRequests.total' "$GATLING_STATS_PATH")
    GATLING_SUCCESS=$(jq -r '.stats.numberOfRequests.ok' "$GATLING_STATS_PATH")
    GATLING_FAILED=$(jq -r '.stats.numberOfRequests.ko' "$GATLING_STATS_PATH")
    
    PERCENTILE_50=$(jq -r '.stats.percentiles1.total' "$GATLING_STATS_PATH")
    PERCENTILE_75=$(jq -r '.stats.percentiles2.total' "$GATLING_STATS_PATH")
    PERCENTILE_95=$(jq -r '.stats.percentiles3.total' "$GATLING_STATS_PATH")
    PERCENTILE_99=$(jq -r '.stats.percentiles4.total' "$GATLING_STATS_PATH")
    
    RESPONSE_TIME_MIN=$(jq -r '.stats.minResponseTime.total' "$GATLING_STATS_PATH")
    RESPONSE_TIME_MAX=$(jq -r '.stats.maxResponseTime.total' "$GATLING_STATS_PATH")
    RESPONSE_TIME_MEAN=$(jq -r '.stats.meanResponseTime.total' "$GATLING_STATS_PATH")
    RESPONSE_TIME_STDDEV=$(jq -r '.stats.standardDeviation.total' "$GATLING_STATS_PATH")
    
    REQUESTS_PER_SECOND=$(jq -r '.stats.meanNumberOfRequestsPerSecond.total' "$GATLING_STATS_PATH")
    
    GROUP_UNDER_800MS=$(jq -r '.stats.group1.count' "$GATLING_STATS_PATH")
    GROUP_800_1200MS=$(jq -r '.stats.group2.count' "$GATLING_STATS_PATH")
    GROUP_OVER_1200MS=$(jq -r '.stats.group3.count' "$GATLING_STATS_PATH")
    
    GROUP_UNDER_800MS_PCT=$(jq -r '.stats.group1.percentage' "$GATLING_STATS_PATH")
    GROUP_800_1200MS_PCT=$(jq -r '.stats.group2.percentage' "$GATLING_STATS_PATH")
    GROUP_OVER_1200MS_PCT=$(jq -r '.stats.group3.percentage' "$GATLING_STATS_PATH")
    
    AUTO_EXTRACTED=true
    
    echo "  ${GREEN}✓ All metrics extracted successfully!${NC}"
    echo ""
    echo "  Request Summary:"
    echo "    Total:                  $GATLING_TOTAL"
    echo "    Successful (OK):        $GATLING_SUCCESS"
    echo "    Failed (KO):            $GATLING_FAILED"
    echo ""
    echo "  Response Time (ms):"
    echo "    Min:                    $RESPONSE_TIME_MIN"
    echo "    Mean:                   $RESPONSE_TIME_MEAN"
    echo "    Max:                    $RESPONSE_TIME_MAX"
    echo "    Std Deviation:          $RESPONSE_TIME_STDDEV"
    echo ""
    echo "  Percentiles (ms):"
    echo "    P50 (Median):           $PERCENTILE_50"
    echo "    P75:                    $PERCENTILE_75"
    echo "    P95:                    $PERCENTILE_95"
    echo "    P99:                    $PERCENTILE_99"
    echo ""
    echo "  Throughput:"
    echo "    Requests/second:        $REQUESTS_PER_SECOND"
    echo ""
    echo "  Response Time Distribution:"
    echo "    < 800ms:                $GROUP_UNDER_800MS requests ($GROUP_UNDER_800MS_PCT%)"
    echo "    800-1200ms:             $GROUP_800_1200MS requests ($GROUP_800_1200MS_PCT%)"
    echo "    > 1200ms:               $GROUP_OVER_1200MS requests ($GROUP_OVER_1200MS_PCT%)"
    echo ""
    
elif [ -n "$GATLING_STATS_PATH" ]; then
    echo ""
    echo -e "  ${RED}✗ File not found: $GATLING_STATS_PATH${NC}"
    echo "  Falling back to manual input..."
    echo ""
fi


if [ "$AUTO_EXTRACTED" = false ]; then
    echo "  ${YELLOW}Manual input mode - Enter all Gatling metrics:${NC}"
    echo ""
    
    echo "  Request Summary:"
    read -p "    Total requests:           " GATLING_TOTAL
    read -p "    Successful (OK):          " GATLING_SUCCESS
    read -p "    Failed (KO):              " GATLING_FAILED
    
    echo ""
    echo "  Response Time (ms):"
    read -p "    Min:                      " RESPONSE_TIME_MIN
    read -p "    Mean:                     " RESPONSE_TIME_MEAN
    read -p "    Max:                      " RESPONSE_TIME_MAX
    read -p "    Std Deviation:            " RESPONSE_TIME_STDDEV
    
    echo ""
    echo "  Percentiles (ms):"
    read -p "    P50 (Median):             " PERCENTILE_50
    read -p "    P75:                      " PERCENTILE_75
    read -p "    P95:                      " PERCENTILE_95
    read -p "    P99:                      " PERCENTILE_99
    
    echo ""
    echo "  Throughput:"
    read -p "    Requests/second:          " REQUESTS_PER_SECOND
    
    echo ""
    echo "  Response Time Distribution:"
    read -p "    < 800ms (count):          " GROUP_UNDER_800MS
    read -p "    < 800ms (percentage):     " GROUP_UNDER_800MS_PCT
    read -p "    800-1200ms (count):       " GROUP_800_1200MS
    read -p "    800-1200ms (percentage):  " GROUP_800_1200MS_PCT
    read -p "    > 1200ms (count):         " GROUP_OVER_1200MS
    read -p "    > 1200ms (percentage):    " GROUP_OVER_1200MS_PCT
fi


echo ""


if [ "$GATLING_TOTAL" -gt 0 ]; then
    GATLING_ERROR_RATE=$(echo "scale=3; ($GATLING_FAILED / $GATLING_TOTAL) * 100" | bc)
else
    GATLING_ERROR_RATE="0"
fi

DELTA=$(echo "$TOTAL_REQUESTS - $GATLING_SUCCESS" | bc)
if [ "$GATLING_SUCCESS" -gt 0 ]; then
    DELTA_PERCENT=$(echo "scale=3; (${DELTA#-} / $GATLING_SUCCESS) * 100" | bc)
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
echo "    Prometheus-Gatling Delta: $([ $DELTA -ge 0 ] && echo "+")$DELTA requests"
echo "    Delta Percentage:         ${DELTA_PERCENT}%"
echo "    Gatling Error Rate:       ${GATLING_ERROR_RATE}%"
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
HEAP_AVAILABLE_GB="6"

GC_TIME_INITIAL_MS=$(echo "scale=0; $GC_TIME_INITIAL * 1000" | bc)
GC_TIME_FINAL_MS=$(echo "scale=0; $GC_TIME_FINAL * 1000" | bc)

cat > "$RESULT_FILE" << EOF
RESULTS - ${IMPLEMENTATION^^}
Date: $(date +"%Y-%m-%d %H:%M:%S")

METRICS PROMETHEUS
────────────────────────────────────────────────
CPU Peak:                 $CPU_PEAK_FORMATTED %
Heap Available:           $HEAP_AVAILABLE_GB GB
Heap Peak:                $HEAP_GB GB
Heap Peak (%):            $HEAP_PERCENT %
GC Time Initial:          $GC_TIME_INITIAL_MS ms
GC Time Final:            $GC_TIME_FINAL_MS ms
GC Time (test):           $GC_TIME_MS ms
GC Collections Initial:   $GC_COUNT_INITIAL
GC Collections Final:     $GC_COUNT_FINAL
GC Collections (test):    $GC_COUNT
Requests Initial:         $REQUESTS_INITIAL
Requests Final:           $REQUESTS_FINAL
Total Requests:           $TOTAL_REQUESTS

GATLING METRICS
────────────────────────────────────────────────
RESPONSE TIME DISTRIBUTION
< 800ms:                  $GROUP_UNDER_800MS requests ($GROUP_UNDER_800MS_PCT%)
800-1200ms:               $GROUP_800_1200MS requests ($GROUP_800_1200MS_PCT%)
> 1200ms:                 $GROUP_OVER_1200MS requests ($GROUP_OVER_1200MS_PCT%)

REQUEST SUMMARY AND THROUGHPUT
Requests/Second:          $REQUESTS_PER_SECOND req/s
Total Requests:           $GATLING_TOTAL
Successful Requests:      $GATLING_SUCCESS
Failed Requests:          $GATLING_FAILED
Error Rate:               $GATLING_ERROR_RATE %

RESPONSE TIME (ms)
Min:                      $RESPONSE_TIME_MIN ms
Mean:                     $RESPONSE_TIME_MEAN ms
Max:                      $RESPONSE_TIME_MAX ms
Std Deviation:            $RESPONSE_TIME_STDDEV ms

PERCENTILES (ms)
P50 (Median):             $PERCENTILE_50 ms
P75:                      $PERCENTILE_75 ms
P95:                      $PERCENTILE_95 ms
P99:                      $PERCENTILE_99 ms

VALIDATION
────────────────────────────────────────────────
Prometheus-Gatling Delta: $([ $DELTA -ge 0 ] && echo "+")$DELTA requests (${DELTA_PERCENT}%)
Sync Status:              $SYNC_STATUS
Error Status:             $ERROR_STATUS

INTERPRETATION
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

if (( $(echo "$PERCENTILE_95 < 10" | bc -l) )); then
    echo "P95 Latency: ✓ EXCELLENT (< 10ms)" >> "$RESULT_FILE"
elif (( $(echo "$PERCENTILE_95 < 50" | bc -l) )); then
    echo "P95 Latency: ✓ GOOD (< 50ms)" >> "$RESULT_FILE"
elif (( $(echo "$PERCENTILE_95 < 100" | bc -l) )); then
    echo "P95 Latency: ⚠ ACCEPTABLE (50-100ms)" >> "$RESULT_FILE"
else
    echo "P95 Latency: ⚠ HIGH (> 100ms)" >> "$RESULT_FILE"
fi

if (( $(echo "$RESPONSE_TIME_STDDEV < 5" | bc -l) )); then
    echo "Response Consistency: ✓ EXCELLENT (StdDev < 5ms)" >> "$RESULT_FILE"
elif (( $(echo "$RESPONSE_TIME_STDDEV < 20" | bc -l) )); then
    echo "Response Consistency: ✓ GOOD (StdDev < 20ms)" >> "$RESULT_FILE"
elif (( $(echo "$RESPONSE_TIME_STDDEV < 50" | bc -l) )); then
    echo "Response Consistency: ⚠ MODERATE (StdDev < 50ms)" >> "$RESULT_FILE"
else
    echo "Response Consistency: ⚠ VARIABLE (StdDev > 50ms)" >> "$RESULT_FILE"
fi

if (( $(echo "$GROUP_UNDER_800MS_PCT > 90" | bc -l) )); then
    echo "Response Distribution: ✓ EXCELLENT (>90% under 800ms)" >> "$RESULT_FILE"
elif (( $(echo "$GROUP_UNDER_800MS_PCT > 70" | bc -l) )); then
    echo "Response Distribution: ✓ GOOD (>70% under 800ms)" >> "$RESULT_FILE"
elif (( $(echo "$GROUP_UNDER_800MS_PCT > 50" | bc -l) )); then
    echo "Response Distribution: ⚠ MODERATE (>50% under 800ms)" >> "$RESULT_FILE"
else
    echo "Response Distribution: ⚠ NEEDS IMPROVEMENT (<50% under 800ms)" >> "$RESULT_FILE"
fi

echo ""
echo -e "${GREEN}✓ Results saved in: $RESULT_FILE${NC}"
echo ""


CSV_FILE="metrics-results/metrics-comparison.csv"

if [ ! -f "$CSV_FILE" ]; then
    echo "Implementation,Timestamp,Prometheus_Success,Gatling_Total,Gatling_Success,Gatling_Failed,Error_Rate_%,Prom_Gatling_Delta_%,GC_Time_ms,GC_Collections,Heap_Peak_GB,Heap_Peak_%,CPU_Peak_%,Sync_Status,Error_Status,P50_ms,P75_ms,P95_ms,P99_ms,Min_ms,Mean_ms,Max_ms,StdDev_ms,Req_Per_Sec,Under_800ms,Under_800ms_%,Between_800_1200ms,Between_800_1200ms_%,Over_1200ms,Over_1200ms_%" > "$CSV_FILE"
fi

echo "${IMPLEMENTATION},${TIMESTAMP},${TOTAL_REQUESTS},${GATLING_TOTAL},${GATLING_SUCCESS},${GATLING_FAILED},${GATLING_ERROR_RATE},${DELTA_PERCENT},${GC_TIME_MS},${GC_COUNT},${HEAP_GB},${HEAP_PERCENT},${CPU_PEAK_FORMATTED},${SYNC_STATUS},${ERROR_STATUS},${PERCENTILE_50},${PERCENTILE_75},${PERCENTILE_95},${PERCENTILE_99},${RESPONSE_TIME_MIN},${RESPONSE_TIME_MEAN},${RESPONSE_TIME_MAX},${RESPONSE_TIME_STDDEV},${REQUESTS_PER_SECOND},${GROUP_UNDER_800MS},${GROUP_UNDER_800MS_PCT},${GROUP_800_1200MS},${GROUP_800_1200MS_PCT},${GROUP_OVER_1200MS},${GROUP_OVER_1200MS_PCT}" >> "$CSV_FILE"


echo "═══════════════════════════════════════════════════"
echo "  COMPREHENSIVE SUMMARY"
echo "═══════════════════════════════════════════════════"
echo ""
echo "  ${BLUE}PROMETHEUS METRICS:${NC}"
echo "    Total Requests:            $TOTAL_REQUESTS"
echo "    GC Time:                   $GC_TIME_MS ms"
echo "    GC Collections:            $GC_COUNT"
echo "    Heap Peak:                 $HEAP_GB GB ($HEAP_PERCENT%)"
echo "    CPU Peak:                  $CPU_PEAK_FORMATTED%"
echo ""
echo "  ${BLUE}GATLING METRICS - REQUESTS:${NC}"
echo "    Total:                     $GATLING_TOTAL"
echo "    Successful:                $GATLING_SUCCESS"
echo "    Failed:                    $GATLING_FAILED"
echo "    Error Rate:                $GATLING_ERROR_RATE%"
echo ""
echo "  ${BLUE}GATLING METRICS - LATENCY:${NC}"
echo "    Min Response:              $RESPONSE_TIME_MIN ms"
echo "    Mean Response:             $RESPONSE_TIME_MEAN ms"
echo "    P50 (Median):              $PERCENTILE_50 ms"
echo "    P95:                       $PERCENTILE_95 ms"
echo "    P99:                       $PERCENTILE_99 ms"
echo "    Max Response:              $RESPONSE_TIME_MAX ms"
echo "    Std Deviation:             $RESPONSE_TIME_STDDEV ms"
echo ""
echo "  ${BLUE}GATLING METRICS - THROUGHPUT:${NC}"
echo "    Requests/Second:           $REQUESTS_PER_SECOND req/s"
echo ""
echo "  ${BLUE}GATLING METRICS - DISTRIBUTION:${NC}"
echo "    < 800ms:                   $GROUP_UNDER_800MS ($GROUP_UNDER_800MS_PCT%)"
echo "    800-1200ms:                $GROUP_800_1200MS ($GROUP_800_1200MS_PCT%)"
echo "    > 1200ms:                  $GROUP_OVER_1200MS ($GROUP_OVER_1200MS_PCT%)"
echo ""
echo "═══════════════════════════════════════════════════"
echo ""
echo -e "${GREEN}✓ Data added to CSV: $CSV_FILE${NC}"
echo ""
echo "Next steps:"
echo "  1. Wait 5 minutes for stabilization"
echo "  2. Run next implementation:"
echo -e "     ${GREEN}./collect-metrics.sh [java|clojure-idiomatic|clojure-interop-java]${NC}"
echo ""
#!/bin/bash

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

if [ -z "$1" ]; then
    echo -e "${RED}Error: Specify implementation${NC}"
    echo "Usage: $0 [java|clojure-idiomatic|clojure-optimized]"
    exit 1
fi

IMPLEMENTATION=$1
PROMETHEUS_URL="http://localhost:9090"

if ! curl -s "${PROMETHEUS_URL}/api/v1/query?query=up" > /dev/null 2>&1; then
    echo -e "${RED}Error: Prometheus not accessible at ${PROMETHEUS_URL}${NC}"
    exit 1
fi

if ! command -v jq &> /dev/null; then
    echo -e "${YELLOW}Installing jq...${NC}"
    sudo apt-get update && sudo apt-get install -y jq
fi

echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  METRICS COLLECTION - $(echo $IMPLEMENTATION | tr '[:lower:]' '[:upper:]')${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
echo ""

echo -e "${YELLOW}[STEP 1] Collecting initial metrics...${NC}"
echo ""

GC_INITIAL=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=sum(jvm_gc_pause_seconds_sum)" | jq -r '.data.result[0].value[1]')
COLLECTIONS_INITIAL=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=sum(jvm_gc_pause_seconds_count)" | jq -r '.data.result[0].value[1]')
REQUESTS_INITIAL=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=sum(http_server_requests_seconds_count)" | jq -r '.data.result[0].value[1]')

if [ "$GC_INITIAL" == "null" ] || [ "$COLLECTIONS_INITIAL" == "null" ]; then
    echo -e "${RED}Error: Could not retrieve metrics. Check if application is running.${NC}"
    exit 1
fi

echo -e "  GC Time Initial:        ${GREEN}${GC_INITIAL}${NC} seconds"
echo -e "  GC Collections Initial: ${GREEN}${COLLECTIONS_INITIAL}${NC}"
echo -e "  Requests Initial:       ${GREEN}${REQUESTS_INITIAL}${NC}"
echo ""

echo -e "${YELLOW}[STEP 2] Ready to run test${NC}"
echo ""
echo -e "${BLUE}Press ENTER after configuring Gatling for $IMPLEMENTATION...${NC}"
read -r

echo -e "${GREEN}Execute now:${NC} ./run-gatling.sh"
echo ""
echo -e "${BLUE}Press ENTER after test finishes...${NC}"
read -r

echo ""
echo -e "${YELLOW}[STEP 3] Collecting final metrics...${NC}"
echo ""

GC_FINAL=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=sum(jvm_gc_pause_seconds_sum)" | jq -r '.data.result[0].value[1]')
COLLECTIONS_FINAL=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=sum(jvm_gc_pause_seconds_count)" | jq -r '.data.result[0].value[1]')
REQUESTS_FINAL=$(curl -s "${PROMETHEUS_URL}/api/v1/query?query=sum(http_server_requests_seconds_count)" | jq -r '.data.result[0].value[1]')
HEAP_PEAK=$(curl -s -G "${PROMETHEUS_URL}/api/v1/query" --data-urlencode 'query=max(max_over_time(jvm_memory_used_bytes{area="heap"}[10m]))' | jq -r '.data.result[0].value[1]')
CPU_PEAK=$(curl -s -G "${PROMETHEUS_URL}/api/v1/query" --data-urlencode 'query=max(max_over_time(process_cpu_usage[10m])) * 100' | jq -r '.data.result[0].value[1]')

if [ "$GC_FINAL" == "null" ] || [ "$COLLECTIONS_FINAL" == "null" ] || [ "$HEAP_PEAK" == "null" ] || [ -z "$HEAP_PEAK" ] || [ "$CPU_PEAK" == "null" ] || [ -z "$CPU_PEAK" ]; then
    echo -e "${RED}Error: Could not retrieve final metrics.${NC}"
    echo -e "${YELLOW}Debug info:${NC}"
    echo "  GC_FINAL: $GC_FINAL"
    echo "  COLLECTIONS_FINAL: $COLLECTIONS_FINAL"
    echo "  REQUESTS_FINAL: $REQUESTS_FINAL"
    echo "  HEAP_PEAK: $HEAP_PEAK"
    echo "  CPU_PEAK: $CPU_PEAK"
    exit 1
fi

echo -e "  GC Time Final:          ${GREEN}${GC_FINAL}${NC} seconds"
echo -e "  GC Collections Final:   ${GREEN}${COLLECTIONS_FINAL}${NC}"
echo -e "  Requests Final:         ${GREEN}${REQUESTS_FINAL}${NC}"
echo -e "  Heap Peak:              ${GREEN}${HEAP_PEAK}${NC} bytes"
echo -e "  CPU Peak:               ${GREEN}${CPU_PEAK}${NC} %"
echo ""

echo -e "${YELLOW}[CALCULATIONS]${NC}"
echo ""

GC_TIME=$(echo "($GC_FINAL - $GC_INITIAL) * 1000" | bc)
GC_COLLECTIONS=$(echo "$COLLECTIONS_FINAL - $COLLECTIONS_INITIAL" | bc)
TOTAL_REQUESTS=$(echo "$REQUESTS_FINAL - $REQUESTS_INITIAL" | bc)
HEAP_GB=$(echo "scale=2; $HEAP_PEAK / 1000000000" | bc)
HEAP_MAX=6000000000
HEAP_PERCENT=$(echo "scale=2; ($HEAP_PEAK / $HEAP_MAX) * 100" | bc)
CPU_PEAK_FORMATTED=$(echo "scale=2; $CPU_PEAK" | bc)

echo -e "  GC Time (test):         ${GREEN}${GC_TIME}${NC} ms"
echo -e "  GC Collections (test):  ${GREEN}${GC_COLLECTIONS}${NC}"
echo -e "  Total Requests:         ${GREEN}${TOTAL_REQUESTS}${NC}"
echo -e "  Heap Peak:              ${GREEN}${HEAP_GB}${NC} GB"
echo -e "  Heap Peak (%):          ${GREEN}${HEAP_PERCENT}${NC} %"
echo -e "  CPU Peak:               ${GREEN}${CPU_PEAK_FORMATTED}${NC} %"
echo ""

RESULTS_DIR="metrics-results"
mkdir -p "$RESULTS_DIR"

RESULT_FILE="${RESULTS_DIR}/metrics-${IMPLEMENTATION}-$(date +%Y%m%d-%H%M%S).txt"

cat > "$RESULT_FILE" << EOF
═══════════════════════════════════════════════════
RESULTS - $(echo $IMPLEMENTATION | tr '[:lower:]' '[:upper:]')
Date: $(date '+%Y-%m-%d %H:%M:%S')
═══════════════════════════════════════════════════

RAW METRICS:
────────────────────────────────────────────────
GC Time Initial:          $GC_INITIAL seconds
GC Time Final:            $GC_FINAL seconds
GC Collections Initial:   $COLLECTIONS_INITIAL
GC Collections Final:     $COLLECTIONS_FINAL
Requests Initial:         $REQUESTS_INITIAL
Requests Final:           $REQUESTS_FINAL
Heap Peak:                $HEAP_PEAK bytes
CPU Peak:                 $CPU_PEAK %

CALCULATED METRICS:
────────────────────────────────────────────────
GC Time (test):           $GC_TIME ms
GC Collections (test):    $GC_COLLECTIONS
Total Requests:           $TOTAL_REQUESTS
Heap Peak:                $HEAP_GB GB
Heap Peak (%):            $HEAP_PERCENT %
CPU Peak:                 $CPU_PEAK_FORMATTED %

INTERPRETATION:
────────────────────────────────────────────────
EOF

if (( $(echo "$HEAP_PERCENT < 30" | bc -l) )); then
    echo "Heap Usage: ✓ EXCELLENT (< 30%)" >> "$RESULT_FILE"
elif (( $(echo "$HEAP_PERCENT < 50" | bc -l) )); then
    echo "Heap Usage: ✓ GOOD (30-50%)" >> "$RESULT_FILE"
elif (( $(echo "$HEAP_PERCENT < 70" | bc -l) )); then
    echo "Heap Usage: ⚠ MODERATE (50-70%)" >> "$RESULT_FILE"
elif (( $(echo "$HEAP_PERCENT < 85" | bc -l) )); then
    echo "Heap Usage: ⚠ HIGH (70-85%)" >> "$RESULT_FILE"
else
    echo "Heap Usage: ✗ CRITICAL (> 85%)" >> "$RESULT_FILE"
fi

GC_TIME_INT=$(echo "$GC_TIME" | cut -d. -f1)
if [ "$GC_TIME_INT" -lt 500 ]; then
    echo "GC Overhead: ✓ EXCELLENT (< 500ms)" >> "$RESULT_FILE"
elif [ "$GC_TIME_INT" -lt 2000 ]; then
    echo "GC Overhead: ✓ ACCEPTABLE (500-2000ms)" >> "$RESULT_FILE"
else
    echo "GC Overhead: ⚠ HIGH (> 2000ms)" >> "$RESULT_FILE"
fi

CPU_PEAK_INT=$(echo "$CPU_PEAK_FORMATTED" | cut -d. -f1)
if [ "$CPU_PEAK_INT" -lt 70 ]; then
    echo "CPU Peak: ✓ EXCELLENT (< 70%)" >> "$RESULT_FILE"
elif [ "$CPU_PEAK_INT" -lt 90 ]; then
    echo "CPU Peak: ⚠ HIGH (70-90%)" >> "$RESULT_FILE"
else
    echo "CPU Peak: ✗ CRITICAL (> 90%)" >> "$RESULT_FILE"
fi

echo -e "${GREEN}Results saved in: ${RESULT_FILE}${NC}"
echo ""

echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  SUMMARY${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
echo ""
printf "  %-25s ${GREEN}%s${NC}\n" "Implementation:" "$IMPLEMENTATION"
printf "  %-25s ${GREEN}%s${NC}\n" "Total Requests:" "$TOTAL_REQUESTS"
printf "  %-25s ${GREEN}%s ms${NC}\n" "GC Time:" "$GC_TIME"
printf "  %-25s ${GREEN}%s${NC}\n" "GC Collections:" "$GC_COLLECTIONS"
printf "  %-25s ${GREEN}%s GB${NC}\n" "Heap Peak:" "$HEAP_GB"
printf "  %-25s ${GREEN}%s%%${NC}\n" "Heap Peak (%):" "$HEAP_PERCENT"
printf "  %-25s ${GREEN}%s%%${NC}\n" "CPU Peak:" "$CPU_PEAK_FORMATTED"
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
echo ""

CSV_FILE="${RESULTS_DIR}/metrics-comparison.csv"

if [ ! -f "$CSV_FILE" ]; then
    echo "Implementation,Date,Total_Requests,GC_Time_ms,GC_Collections,Heap_GB,Heap_Percent,CPU_Peak_Percent" > "$CSV_FILE"
fi

echo "$IMPLEMENTATION,$(date '+%Y-%m-%d %H:%M:%S'),$TOTAL_REQUESTS,$GC_TIME,$GC_COLLECTIONS,$HEAP_GB,$HEAP_PERCENT,$CPU_PEAK_FORMATTED" >> "$CSV_FILE"

echo -e "${GREEN}Data added to CSV: ${CSV_FILE}${NC}"
echo ""

echo -e "${YELLOW}Next steps:${NC}"
echo "  1. Wait 5 minutes for stabilization"
echo "  2. Run next implementation:"
echo "     ${GREEN}./collect-metrics.sh [java|clojure-idiomatic|clojure-optimized]${NC}"
echo ""
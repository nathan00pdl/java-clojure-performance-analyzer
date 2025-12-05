#!/bin/bash

PROMETHEUS_URL="http://localhost:9090"

echo "Testing Prometheus queries..."
echo ""

echo "1. Testing GC Time query:"
curl -s "${PROMETHEUS_URL}/api/v1/query?query=sum(jvm_gc_pause_seconds_sum)" | jq '.'
echo ""

echo "2. Testing GC Collections query:"
curl -s "${PROMETHEUS_URL}/api/v1/query?query=sum(jvm_gc_pause_seconds_count)" | jq '.'
echo ""

echo "3. Testing Heap Peak query:"
curl -s -G "${PROMETHEUS_URL}/api/v1/query" --data-urlencode 'query=max(max_over_time(jvm_memory_used_bytes{area="heap"}[10m]))' | jq '.'
echo ""

echo "4. Testing CPU Peak query:"
curl -s -G "${PROMETHEUS_URL}/api/v1/query" --data-urlencode 'query=max(max_over_time(process_cpu_usage[10m])) * 100' | jq '.'
echo ""

echo "All queries match collect-metrics.sh"
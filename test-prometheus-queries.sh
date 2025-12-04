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

echo "3. Testing Heap Peak query (CORRECTED):"
curl -s -G "${PROMETHEUS_URL}/api/v1/query" --data-urlencode 'query=max(max_over_time(jvm_memory_used_bytes{area="heap"}[10m]))' | jq '.'
echo ""

echo "4. Testing simple heap query:"
curl -s "${PROMETHEUS_URL}/api/v1/query?query=sum(jvm_memory_used_bytes)" | jq '.'
echo ""

echo "5. Testing heap with area filter:"
curl -s -G "${PROMETHEUS_URL}/api/v1/query" --data-urlencode 'query=sum(jvm_memory_used_bytes{area="heap"})' | jq '.'
echo ""

echo "6. Testing CPU current value:"
curl -s "${PROMETHEUS_URL}/api/v1/query?query=process_cpu_usage" | jq '.'
echo ""

echo "7. Testing CPU Peak (max over 10 minutes):"
curl -s -G "${PROMETHEUS_URL}/api/v1/query" --data-urlencode 'query=max_over_time(process_cpu_usage[10m]) * 100' | jq '.'
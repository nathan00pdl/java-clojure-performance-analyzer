#!/bin/bash

watch -n 2 '
METRICS=$(curl -s http://localhost:8080/actuator/prometheus)
echo "JVM PERFORMANCE METRICS"
echo ""

echo "HTTP REQUESTS"
echo "-----------------------------------"
HTTP_TOTAL=$(curl -s "http://localhost:9090/api/v1/query?query=http_server_requests_seconds_count" 2>/dev/null)

if echo "$HTTP_TOTAL" | jq -e ".data.result | length > 0" >/dev/null 2>&1; then
    echo "$HTTP_TOTAL" | jq -r ".data.result[] | select(.metric.uri | startswith(\"/api/\")) | \"\(.metric.method) \(.metric.uri): \(.value[1]) requests\""
else
    echo "Nenhuma requisicao registrada"
fi

LATENCY=$(curl -s http://localhost:8080/actuator/metrics/http.server.requests 2>/dev/null | jq -r ".measurements[] | select(.statistic==\"TOTAL_TIME\") | .value" 2>/dev/null)
COUNT=$(curl -s http://localhost:8080/actuator/metrics/http.server.requests 2>/dev/null | jq -r ".measurements[] | select(.statistic==\"COUNT\") | .value" 2>/dev/null)

if [ ! -z "$LATENCY" ] && [ ! -z "$COUNT" ] && [ "$COUNT" != "0" ]; then
    AVG_LATENCY=$(echo "$LATENCY $COUNT" | awk "{printf \"%.2f\", (\$1/\$2)*1000}")
    echo ""
    echo "Latencia media geral: ${AVG_LATENCY}ms"
fi

echo ""
echo "HEAP MEMORY"
echo "-----------------------------------"

# CORREÇÃO: Usar Prometheus para heap ao invés de grep/awk
HEAP_QUERY=$(curl -s "http://localhost:9090/api/v1/query?query=sum(jvm_memory_used_bytes{area=\"heap\"})" 2>/dev/null)
HEAP_MAX_QUERY=$(curl -s "http://localhost:9090/api/v1/query?query=sum(jvm_memory_max_bytes{area=\"heap\"})" 2>/dev/null)

HEAP_USED=$(echo "$HEAP_QUERY" | jq -r ".data.result[0].value[1]" 2>/dev/null)
HEAP_MAX=$(echo "$HEAP_MAX_QUERY" | jq -r ".data.result[0].value[1]" 2>/dev/null)

if [ ! -z "$HEAP_USED" ] && [ "$HEAP_USED" != "null" ] && [ ! -z "$HEAP_MAX" ] && [ "$HEAP_MAX" != "null" ] && [ "$HEAP_MAX" != "0" ]; then
    HEAP_USED_GB=$(echo "$HEAP_USED" | awk "{printf \"%.2f\", \$1/1024/1024/1024}")
    HEAP_MAX_GB=$(echo "$HEAP_MAX" | awk "{printf \"%.2f\", \$1/1024/1024/1024}")
    HEAP_PERCENT=$(echo "$HEAP_USED $HEAP_MAX" | awk "{printf \"%.1f\", (\$1/\$2)*100}")
    
    echo "Usado:  ${HEAP_USED_GB} GB"
    echo "Maximo: ${HEAP_MAX_GB} GB"
    echo "Uso:    ${HEAP_PERCENT}%"
else
    echo "Dados nao disponiveis"
fi

echo ""
echo "GARBAGE COLLECTOR"
echo "-----------------------------------"
GC_TOTAL=$(curl -s "http://localhost:9090/api/v1/query?query=jvm_gc_pause_seconds_count" 2>/dev/null)

if echo "$GC_TOTAL" | jq -e ".data.result | length > 0" >/dev/null 2>&1; then
    echo "$GC_TOTAL" | jq -r ".data.result[] | \"\(.metric.action) (\(.metric.cause)): \(.value[1]) coletas\""
    
    GC_TIME=$(curl -s "http://localhost:9090/api/v1/query?query=jvm_gc_pause_seconds_sum" 2>/dev/null)
    if echo "$GC_TIME" | jq -e ".data.result | length > 0" >/dev/null 2>&1; then
        echo ""
        echo "Tempo total de pausa:"
        echo "$GC_TIME" | jq -r ".data.result[] | \"\(.metric.action): \(.value[1] | tonumber * 1000 | round)ms acumulado\""
    fi
else
    echo "Sem atividade de GC registrada"
fi

echo ""
echo "CPU USAGE"
echo "-----------------------------------"

CPU_PROCESS=$(echo "$METRICS" | grep "^process_cpu_usage " | awk "{print \$2}")
CPU_SYSTEM=$(echo "$METRICS" | grep "^system_cpu_usage " | awk "{print \$2}")
CPU_CORES=$(echo "$METRICS" | grep "^system_cpu_count " | awk "{print \$2}")

if [ ! -z "$CPU_PROCESS" ]; then
    echo "Process: $(echo "$CPU_PROCESS" | awk "{printf \"%.1f%%\", \$1*100}")"
fi
if [ ! -z "$CPU_SYSTEM" ]; then
    echo "System:  $(echo "$CPU_SYSTEM" | awk "{printf \"%.1f%%\", \$1*100}")"
fi
if [ ! -z "$CPU_CORES" ]; then
    echo "Cores:   $(echo "$CPU_CORES" | awk "{printf \"%.0f\", \$1}")"
fi

echo ""
echo "THREADS"
echo "-----------------------------------"
THREADS_LIVE=$(echo "$METRICS" | grep "jvm_threads_live_threads{" | head -1 | awk "{print \$2}")
THREADS_DAEMON=$(echo "$METRICS" | grep "jvm_threads_daemon_threads{" | head -1 | awk "{print \$2}")
THREADS_PEAK=$(echo "$METRICS" | grep "jvm_threads_peak_threads{" | head -1 | awk "{print \$2}")

if [ ! -z "$THREADS_LIVE" ]; then
    echo "Ativas:  $(echo "$THREADS_LIVE" | awk "{printf \"%.0f\", \$1}")"
fi
if [ ! -z "$THREADS_DAEMON" ]; then
    echo "Daemon:  $(echo "$THREADS_DAEMON" | awk "{printf \"%.0f\", \$1}")"
fi
if [ ! -z "$THREADS_PEAK" ]; then
    echo "Pico:    $(echo "$THREADS_PEAK" | awk "{printf \"%.0f\", \$1}")"
fi

echo ""
echo "CLASSES (Metaspace)"
echo "-----------------------------------"
CLASSES_LOADED=$(echo "$METRICS" | grep "jvm_classes_loaded_classes{" | head -1 | awk "{print \$2}")
if [ ! -z "$CLASSES_LOADED" ]; then
    echo "Carregadas: $(echo "$CLASSES_LOADED" | awk "{printf \"%.0f\", \$1}")"
fi

echo ""
echo "SYSTEM LOAD"
echo "-----------------------------------"

LOAD_AVG=$(echo "$METRICS" | grep "^system_load_average_1m " | awk "{print \$2}")
if [ ! -z "$LOAD_AVG" ]; then
    echo "Load avg (1m): $(echo "$LOAD_AVG" | awk "{printf \"%.2f\", \$1}")"
fi

echo ""
echo "-----------------------------------"
echo "Atualizado: $(date +\"%H:%M:%S\")"
'
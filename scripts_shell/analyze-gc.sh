#!/bin/bash

LOG=$1

echo "=== Minor GC (Young/Eden) ==="
grep -c "Pause Young" "$LOG"

echo "=== Mixed GC ==="
grep -c "Pause Mixed" "$LOG"

echo "=== Full GC ==="
grep -c "Pause Full" "$LOG"

echo "=== Heap por região ao fim ==="
grep "Heap" "$LOG" | tail -20
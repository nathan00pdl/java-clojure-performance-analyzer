#!/usr/bin/env bash

set -e

echo "═══════════════════════════════════════════════════════════════════"
echo "  BYTECODE ANALYSIS - TCC Programming Paradigms"
echo "═══════════════════════════════════════════════════════════════════"
echo ""

OUTPUT_DIR="bytecode-analysis"
mkdir -p "$OUTPUT_DIR"

CLASS_DIR="target/classes/com/example/java_clojure_performance_analyzer"

echo "[STEP 1] Checking compilation..."
if [ ! -d "$CLASS_DIR" ]; then
    echo "  Compiling the project..."
    mvn clean compile -q
fi
echo "  ✓ Classes found"
echo ""

echo "[STEP 2] Generating Java bytecode..."
JAVA_CLASS="$CLASS_DIR/services/JavaCompoundInterestService.class"

if [ -f "$JAVA_CLASS" ]; then
    javap -c -p "$JAVA_CLASS" > "$OUTPUT_DIR/bytecode_java.txt"
    echo "  ✓ bytecode_java.txt"
else
    echo "  ✗ ERROR: JavaCompoundInterestService.class not found"
    exit 1
fi
echo ""

echo "[STEP 3] Generating Idiomatic Clojure bytecode..."

IDIOMATIC_CALC="$CLASS_DIR/service/compound_interest_service_idiomatic\$calculate_compound_interest.class"
IDIOMATIC_INIT="$CLASS_DIR/service/compound_interest_service_idiomatic__init.class"
IDIOMATIC_LOOP="$CLASS_DIR/service/compound_interest_service_idiomatic\$calculate_compound_interest\$fn__275.class"

{
    echo "================================================================================"
    echo "IDIOMATIC CLOJURE - BYTECODE ANALYSIS"
    echo "================================================================================"
    echo ""

    echo "=== MAIN FUNCTION: calculate_compound_interest ==="
    if [ -f "$IDIOMATIC_CALC" ]; then
        javap -c -p "$IDIOMATIC_CALC" 2>/dev/null
    else
        echo "File not found: $IDIOMATIC_CALC"
    fi
    echo ""

    echo "=== INNER LOOP FUNCTION (fn__275) ==="
    if [ -f "$IDIOMATIC_LOOP" ]; then
        javap -c -p "$IDIOMATIC_LOOP" 2>/dev/null
    fi
    echo ""

    echo "=== NAMESPACE INITIALIZER ==="
    if [ -f "$IDIOMATIC_INIT" ]; then
        javap -c -p "$IDIOMATIC_INIT" 2>/dev/null
    fi

} > "$OUTPUT_DIR/bytecode_clojure_idiomatic.txt"

echo "  ✓ bytecode_clojure_idiomatic.txt"
echo ""

echo "[STEP 4] Generating Clojure Java Interop bytecode..."

INTEROP_CALC="$CLASS_DIR/service/compound_interest_service_interop_java\$calculate_compound_interest.class"
INTEROP_INIT="$CLASS_DIR/service/compound_interest_service_interop_java__init.class"
INTEROP_LOOP="$CLASS_DIR/service/compound_interest_service_interop_java\$calculate_compound_interest\$fn__285.class"

{
    echo "================================================================================"
    echo "CLOJURE JAVA INTEROP - BYTECODE ANALYSIS"
    echo "================================================================================"
    echo ""

    echo "=== MAIN FUNCTION: calculate_compound_interest ==="
    if [ -f "$INTEROP_CALC" ]; then
        javap -c -p "$INTEROP_CALC" 2>/dev/null
    else
        echo "File not found: $INTEROP_CALC"
    fi
    echo ""

    echo "=== INNER LOOP FUNCTION (fn__285) ==="
    if [ -f "$INTEROP_LOOP" ]; then
        javap -c -p "$INTEROP_LOOP" 2>/dev/null
    fi
    echo ""

    echo "=== NAMESPACE INITIALIZER ==="
    if [ -f "$INTEROP_INIT" ]; then
        javap -c -p "$INTEROP_INIT" 2>/dev/null
    fi

} > "$OUTPUT_DIR/bytecode_clojure_interop.txt"

echo "  ✓ bytecode_clojure_interop.txt"
echo ""

echo "[STEP 5] Generating comparative analysis..."

{
    echo "================================================================================"
    echo "COMPARATIVE BYTECODE ANALYSIS"
    echo "TCC: Cost of Idiomatic Abstractions on the JVM"
    echo "================================================================================"
    echo ""
    echo "Date: $(date)"
    echo ""

    echo "================================================================================"
    echo "1. INSTRUCTION COUNT"
    echo "================================================================================"
    echo ""

    echo "--- JAVA ---"
    echo "  invokevirtual (direct method calls):            $(grep -c 'invokevirtual' "$OUTPUT_DIR/bytecode_java.txt" 2>/dev/null || echo 0)"
    echo "  invokespecial (constructors):                   $(grep -c 'invokespecial' "$OUTPUT_DIR/bytecode_java.txt" 2>/dev/null || echo 0)"
    echo "  invokestatic:                                   $(grep -c 'invokestatic' "$OUTPUT_DIR/bytecode_java.txt" 2>/dev/null || echo 0)"
    echo "  invokeinterface:                                $(grep -c 'invokeinterface' "$OUTPUT_DIR/bytecode_java.txt" 2>/dev/null || echo 0)"
    echo "  new (object allocation):                        $(grep -c ' new ' "$OUTPUT_DIR/bytecode_java.txt" 2>/dev/null || echo 0)"
    echo "  double operations (dmul/dadd/dsub/ddiv):        $(grep -cE '(dmul|dadd|dsub|ddiv)' "$OUTPUT_DIR/bytecode_java.txt" 2>/dev/null || echo 0)"
    echo ""

    echo "--- IDIOMATIC CLOJURE ---"
    echo "  invokevirtual:                                  $(grep -c 'invokevirtual' "$OUTPUT_DIR/bytecode_clojure_idiomatic.txt" 2>/dev/null || echo 0)"
    echo "  invokespecial:                                  $(grep -c 'invokespecial' "$OUTPUT_DIR/bytecode_clojure_idiomatic.txt" 2>/dev/null || echo 0)"
    echo "  invokestatic (Numbers.*, RT.*):                 $(grep -c 'invokestatic' "$OUTPUT_DIR/bytecode_clojure_idiomatic.txt" 2>/dev/null || echo 0)"
    echo "  invokeinterface (IFn, collections):             $(grep -c 'invokeinterface' "$OUTPUT_DIR/bytecode_clojure_idiomatic.txt" 2>/dev/null || echo 0)"
    echo "  new (object allocation):                        $(grep -c ' new ' "$OUTPUT_DIR/bytecode_clojure_idiomatic.txt" 2>/dev/null || echo 0)"
    echo "  double operations (dmul/dadd/dsub/ddiv):        $(grep -cE '(dmul|dadd|dsub|ddiv)' "$OUTPUT_DIR/bytecode_clojure_idiomatic.txt" 2>/dev/null || echo 0)"
    echo ""

    echo "--- CLOJURE JAVA INTEROP ---"
    echo "  invokevirtual:                                  $(grep -c 'invokevirtual' "$OUTPUT_DIR/bytecode_clojure_interop.txt" 2>/dev/null || echo 0)"
    echo "  invokespecial:                                  $(grep -c 'invokespecial' "$OUTPUT_DIR/bytecode_clojure_interop.txt" 2>/dev/null || echo 0)"
    echo "  invokestatic:                                   $(grep -c 'invokestatic' "$OUTPUT_DIR/bytecode_clojure_interop.txt" 2>/dev/null || echo 0)"
    echo "  invokeinterface:                                $(grep -c 'invokeinterface' "$OUTPUT_DIR/bytecode_clojure_interop.txt" 2>/dev/null || echo 0)"
    echo "  new (object allocation):                        $(grep -c ' new ' "$OUTPUT_DIR/bytecode_clojure_interop.txt" 2>/dev/null || echo 0)"
    echo "  double operations (dmul/dadd/dsub/ddiv):        $(grep -cE '(dmul|dadd|dsub|ddiv)' "$OUTPUT_DIR/bytecode_clojure_interop.txt" 2>/dev/null || echo 0)"
    echo ""

    echo "================================================================================"
    echo "2. PARADIGM-SPECIFIC PATTERNS"
    echo "================================================================================"
    echo ""

    echo "--- JAVA: Mutable Structures ---"
    echo "  ArrayList references:                           $(grep -c 'ArrayList' "$OUTPUT_DIR/bytecode_java.txt" 2>/dev/null || echo 0)"
    echo "  ArrayList.add calls:                            $(grep -c 'ArrayList.add' "$OUTPUT_DIR/bytecode_java.txt" 2>/dev/null || echo 0)"
    echo ""

    echo "--- IDIOMATIC CLOJURE: Persistent Structures ---"
    echo "  PersistentVector references:                    $(grep -c 'PersistentVector' "$OUTPUT_DIR/bytecode_clojure_idiomatic.txt" 2>/dev/null || echo 0)"
    echo "  RT.conj calls (add to collection):              $(grep -c 'RT.conj' "$OUTPUT_DIR/bytecode_clojure_idiomatic.txt" 2>/dev/null || echo 0)"
    echo "  clojure.lang.Numbers calls:                     $(grep -c 'clojure/lang/Numbers' "$OUTPUT_DIR/bytecode_clojure_idiomatic.txt" 2>/dev/null || echo 0)"
    echo "  RT (Runtime) calls:                             $(grep -c 'clojure/lang/RT' "$OUTPUT_DIR/bytecode_clojure_idiomatic.txt" 2>/dev/null || echo 0)"
    echo ""

    echo "--- CLOJURE INTEROP: Optimizations ---"
    echo "  ArrayList references:                           $(grep -c 'ArrayList' "$OUTPUT_DIR/bytecode_clojure_interop.txt" 2>/dev/null || echo 0)"
    echo "  ArrayList.add calls:                            $(grep -c 'ArrayList.add' "$OUTPUT_DIR/bytecode_clojure_interop.txt" 2>/dev/null || echo 0)"
    echo "  unchecked operations:                           $(grep -c 'unchecked' "$OUTPUT_DIR/bytecode_clojure_interop.txt" 2>/dev/null || echo 0)"
    echo "  effective type hints (checkcast):               $(grep -c 'checkcast' "$OUTPUT_DIR/bytecode_clojure_interop.txt" 2>/dev/null || echo 0)"
    echo ""

    echo "================================================================================"
    echo "3. CONTROL FLOW INSTRUCTIONS (LOOPS)"
    echo "================================================================================"
    echo ""

    echo "--- JAVA ---"
    echo "  goto (loop jumps):                              $(grep -c 'goto' "$OUTPUT_DIR/bytecode_java.txt" 2>/dev/null || echo 0)"
    echo "  if_icmp* (int comparisons):                     $(grep -cE 'if_icmp' "$OUTPUT_DIR/bytecode_java.txt" 2>/dev/null || echo 0)"
    echo ""

    echo "--- IDIOMATIC CLOJURE ---"
    echo "  goto (optimized recur):                         $(grep -c 'goto' "$OUTPUT_DIR/bytecode_clojure_idiomatic.txt" 2>/dev/null || echo 0)"
    echo "  if_icmp* (comparisons):                         $(grep -cE 'if_icmp' "$OUTPUT_DIR/bytecode_clojure_idiomatic.txt" 2>/dev/null || echo 0)"
    echo ""

    echo "--- CLOJURE INTEROP ---"
    echo "  goto (optimized recur):                         $(grep -c 'goto' "$OUTPUT_DIR/bytecode_clojure_interop.txt" 2>/dev/null || echo 0)"
    echo "  if_icmp* (comparisons):                         $(grep -cE 'if_icmp' "$OUTPUT_DIR/bytecode_clojure_interop.txt" 2>/dev/null || echo 0)"
    echo ""

    echo "================================================================================"
    echo "4. INTERPRETIVE SUMMARY"
    echo "================================================================================"
    echo ""
    echo "JAVA:"
    echo "  - Uses mutable ArrayList (single allocation)"
    echo "  - Direct primitive arithmetic operations (dmul, dadd)"
    echo "  - Traditional for loop compiled to efficient goto"
    echo ""
    echo "IDIOMATIC CLOJURE:"
    echo "  - Uses immutable PersistentVector (new version each iteration via conj)"
    echo "  - Operations via clojure.lang.Numbers (overflow checking)"
    echo "  - loop/recur compiled to goto (no stack consumption)"
    echo "  - More invokestatic and invokeinterface = more indirection"
    echo ""
    echo "CLOJURE INTEROP:"
    echo "  - Uses Java's ArrayList (like pure Java)"
    echo "  - Type hints (^ArrayList) eliminate reflection"
    echo "  - unchecked-inc avoids overflow checking"
    echo "  - Combines Clojure expressiveness with Java performance"
    echo ""

} > "$OUTPUT_DIR/comparative_analysis.txt"

echo "  ✓ comparative_analysis.txt"
echo ""

echo "═══════════════════════════════════════════════════════════════════"
echo "  ANALYSIS COMPLETE!"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "  Files generated in: $OUTPUT_DIR/"
ls -la "$OUTPUT_DIR/"
echo ""
echo "  To view the comparative analysis:"
echo "    cat $OUTPUT_DIR/comparative_analysis.txt"
echo ""
echo "  To view specific bytecode:"
echo "    cat $OUTPUT_DIR/bytecode_java.txt"
echo "    cat $OUTPUT_DIR/bytecode_clojure_idiomatic.txt"
echo "    cat $OUTPUT_DIR/bytecode_clojure_interop.txt"
echo ""
package DSA_Interview_Questions.CompanyWisePrep.Fivetran;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ====================================================================================
 * FIVETRAN PERFORMANCE ENGINEERING & CODE OPTIMIZATION REVISION SHEET
 * ====================================================================================
 * Role: Senior Software Engineer (SSE) @ Fivetran
 * Target Round: Performance Engineering, Code Debugging & Optimization
 *
 * THIS FILE CONTAINS:
 * 1. Flame Graph Mechanics & Interview Speaking Framework
 * 2. Pattern 1: Regex Performance (Compilation & Catastrophic Backtracking)
 * 3. Pattern 2: String Parsing & Allocations (String.split vs Manual Scanning)
 * 4. Pattern 3: JVM Memory, Collection Sizing & Micro-Optimizations
 * 5. Section 4: Full Case Study Fix (LogCleaner Refactoring)
 * ====================================================================================
 */
public class FivetranPerformanceCheatSheet {

    // ================================================================================
    // SECTION 0: FLAME GRAPH READING & INTERVIEW NARRATIVE FRAMEWORK
    // ================================================================================
    /*
     * CORE ANATOMY OF A FLAME GRAPH:
     * --------------------------------------------------------------------------------
     * 1. X-Axis (Width): Represents TOTAL CPU execution time or sample percentage spent.
     *    - Wider box = More CPU time spent in that function stack.
     *    - NOT chronological time moving left-to-right (frames are sorted alphabetically).
     *
     * 2. Y-Axis (Height): Represents call stack depth.
     *    - Bottom = Root function (e.g., main / pipeline run loop).
     *    - Top = Leaf function currently executing on the CPU core.
     *
     * 3. The "Plateau" (Hotspot):
     *    - Look for wide horizontal bars at the VERY TOP of the flame graph.
     *    - A wide flat top means CPU time is heavily spent inside THAT method body itself
     *      rather than delegating work to child methods.
     *
     * 4-STEP INTERVIEW ANSWER SCRIPT FORMULA:
     * --------------------------------------------------------------------------------
     * Step 1: OBSERVE -> "Looking at the flame graph, the main bottleneck is the top leaf frame
     *                    'Pattern.compile' taking up ~70% of total CPU width."
     *
     * Step 2: ROOT CAUSE -> "Tracing down the stack, 'sanitizeRecord()' calls 'String.replaceAll()'
     *                       inside a loop for every row. This forces the JVM to repeatedly parse
     *                       and build the Regex AST state machine millions of times."
     *
     * Step 3: PROPOSE FIX -> "We can extract the regex into a static final Pattern constant,
     *                        pre-size our ArrayList to avoid dynamic resizing copies, and use
     *                        an indexed loop to avoid Iterator allocations."
     *
     * Step 4: ESTIMATE IMPACT -> "This reduces time complexity to linear string scanning, eliminates
     *                            repetitive heap object allocations, and cuts GC pressure dramatically."
     */

    // ================================================================================
    // SECTION 1: REGEX OPTIMIZATION & CATASTROPHIC BACKTRACKING
    // ================================================================================

    /*
     * --------------------------------------------------------------------------------
     * SCENARIO 1A: Regex Re-compilation inside Hot Loops
     * Symptom: Flame graph top plateau dominated by java.util.regex.Pattern.compile
     * --------------------------------------------------------------------------------
     */

    // ❌ UNOPTIMIZED: String.matches() recompiles Pattern on EVERY call inside loop
    public List<String> extractEmailsUnoptimized(List<String> rawLogs) {
        List<String> results = new ArrayList<>();
        for (String log : rawLogs) {
            // String.matches() calls Pattern.compile(regex) under the hood on EVERY iteration!
            if (log.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")) {
                results.add(log);
            }
        }
        return results;
    }

    // ✅ OPTIMIZED: Pre-compile Pattern ONCE as static final constant
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");



    public List<String> extractEmailsOptimized(List<String> rawLogs) {
        List<String> results = new ArrayList<>(rawLogs.size());
        for (int i = 0; i < rawLogs.size(); i++) {
            String log = rawLogs.get(i);
            if (EMAIL_PATTERN.matcher(log).matches()) {
                results.add(log);
            }
        }
        return results;
    }


    /*
     * --------------------------------------------------------------------------------
     * SCENARIO 1B: Catastrophic Backtracking & Expression Efficiency
     * Symptom: Top leaf frame dominated by Matcher.search / Pattern$Start.match (90%+ CPU).
     * Cause: Greedy wildcards (.*) + nested quantifiers ((a+)+) force NFA engine into
     * O(2^N) exponential backtracking paths on near-matching or failing inputs.
     * --------------------------------------------------------------------------------
     */

    // ❌ UNOPTIMIZED REGEX MATCHING:
    public boolean validateErrorLogUnoptimized(String logRecord) {
        // 1. String.matches() forces FULL-STRING match from ^ to $ -> requires ^.* and .*$
        // 2. Greedy .* consumes entire line, then backtracks 1 character at a time.
        return logRecord.matches("^.*\\[ERROR\\].*ID:([0-9]+).*$");
    }

    // ✅ OPTIMIZED REGEX MATCHING:
    // Why did the pattern change?
    // 1. Switched from String.matches() to Matcher.find() -> Dropped ^.* and .*$ completely!
    //    (find() scans forward to find [ERROR], skipping thousands of unneeded string checks).
    // 2. Switched greedy .* to non-greedy .*? -> Stops scanning immediately when "ID:" is reached.
    // 3. Changed [0-9]+ to \d+ -> Standard clean regex notation for digits.
    private static final Pattern ERROR_ID_PATTERN = Pattern.compile("\\[ERROR\\].*?ID:(\\d+)");

    public boolean validateErrorLogOptimized(String logRecord) {
        return ERROR_ID_PATTERN.matcher(logRecord).find();
    }

    /*
     * 💡 THREE WAYS TO FIX CATASTROPHIC BACKTRACKING IN INTERVIEWS:
     *
     * Fix 1: Negated Character Classes (Most Common Fix)
     *   - Bad:  Pattern.compile("\"(.*)\",\"(.*)\"");
     *   - Good: Pattern.compile("\"([^\"]*)\",\"([^\"]*)\""); // Stops at quote, no backtracking past quotes!
     *
     * Fix 2: Possessive Quantifiers (++ or *+)
     *   - Bad:  Pattern.compile("(a+)+b");
     *   - Good: Pattern.compile("(a++)++b"); // Tells engine: NEVER give back matched characters!
     *
     * Fix 3: Direct String Operations
     *   - Replace regex completely with String.indexOf() and substring() for linear O(N) scanning.
     */


    // ================================================================================
    // SECTION 2: STRING PARSING & ALLOCATION OPTIMIZATIONS
    // ================================================================================

    /*
     * --------------------------------------------------------------------------------
     * SCENARIO 2A: String.split() vs Index-Based Scanning
     * Symptom: High GC allocation frames in String.split, Pattern.split, or [GC Failure].
     * Cause: String.split(",") compiles regex, creates a String[] array, and creates new
     * String objects for every single field on every row.
     * --------------------------------------------------------------------------------
     */

    // ❌ UNOPTIMIZED: String.split allocates array + sub-strings per line
    public List<String> parseCsvRowUnoptimized(String csvLine) {
        String[] parts = csvLine.split(","); // Heavy memory footprint
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            result.add(part.trim());
        }
        return result;
    }


    // ✅ OPTIMIZED (Dynamic N Parts): Zero-regex index scanning using indexOf()
    public List<String> parseCsvRowOptimized(String line, int expectedColumnCount) {
        // Pre-allocate list capacity to avoid dynamic array resizing copies
        List<String> tokens = new ArrayList<>(expectedColumnCount);
        int start = 0;
        int nextComma;

        while ((nextComma = line.indexOf(',', start)) != -1) {
            tokens.add(line.substring(start, nextComma).trim());
            start = nextComma + 1;
        }
        tokens.add(line.substring(start).trim()); // Add final remaining token
        return tokens;
    }

    // ✅ OPTIMIZED (On-the-Fly Processing / Zero Array Allocation):
    public void processCsvOnTheFly(String line) {
        int start = 0;
        int nextComma;
        while ((nextComma = line.indexOf(',', start)) != -1) {
            // CharSequence / subSequence avoids creating full String objects where applicable
            CharSequence token = line.subSequence(start, nextComma);
            // processToken(token);
            start = nextComma + 1;
        }
        CharSequence lastToken = line.subSequence(start, line.length());
        // processToken(lastToken);
    }

    // ✅ OPTIMIZED (Early-Stop Column Extraction):
    // Example: Only extract Column 0 and Column 3 from a row with 20 columns
    public void parseSpecificColumnsOnly(String line) {
        int start = 0;
        int commaCount = 0;
        String col0 = null;
        String col3 = null;
        int nextComma;

        while ((nextComma = line.indexOf(',', start)) != -1) {
            if (commaCount == 0) {
                col0 = line.substring(start, nextComma);
            } else if (commaCount == 3) {
                col3 = line.substring(start, nextComma);
                break; // EARLY EXIT! Ignore remaining 16+ columns completely!
            }
            commaCount++;
            start = nextComma + 1;
        }
        // process(col0, col3);
    }


    // ================================================================================
    // SECTION 3: JVM MEMORY, COLLECTION SIZING & MICRO-OPTIMIZATIONS
    // ================================================================================

    /*
     * --------------------------------------------------------------------------------
     * PATTERN 3A: ArrayList Capacity Pre-Sizing
     * --------------------------------------------------------------------------------
     * Problem: new ArrayList<>() starts with capacity 10. Adding 500,000 items forces ~15
     * dynamic array re-allocations and element copying operations.
     * Fix: Always pre-size when size is known: new ArrayList<>(rawLogs.size());
     */

    /*
     * --------------------------------------------------------------------------------
     * PATTERN 3B: Enhanced for-each Loop vs Indexed for Loop
     * --------------------------------------------------------------------------------
     * WHY USE INDEXED LOOP (for (int i = 0; i < list.size(); i++)) IN HOT PATHS?
     * 1. Enhanced for-each (for (String s : list)) calls list.iterator() under the hood.
     * 2. Calls to iterator() allocate short-lived Iterator heap objects per method run,
     *    creating high allocation churn & Garbage Collection pressure.
     * 3. Primitive counter `int i` stays on thread execution STACK (Zero Heap allocations).
     * 4. Virtual method calls (hasNext(), next()) eliminated; easier JIT compiler inlining.
     *
     * ⚠️ CRITICAL INTERVIEW CAVEAT:
     * - ONLY use indexed loops for ArrayList or classes implementing RandomAccess (O(1) access).
     * - NEVER use indexed loops for LinkedList (list.get(i) turns O(N) into O(N^2)).
     */

    /*
     * --------------------------------------------------------------------------------
     * PATTERN 3C: Primitive Auto-Boxing vs Primitive Collections/Arrays
     * Symptom: Flame graph wide bars at Integer.valueOf(int) / Long.valueOf(long).
     * --------------------------------------------------------------------------------
     */

    // ❌ UNOPTIMIZED: Wrapper objects cause auto-unboxing & re-boxing per iteration
    public Long calculateTotalSizeUnoptimized(List<Integer> packetSizes) {
        Long total = 0L;
        for (Integer size : packetSizes) {
            total += size; // Auto-unboxing and re-boxing every step!
        }
        return total;
    }

    // ✅ OPTIMIZED: Primitive variables + Indexed loop
    public long calculateTotalSizeOptimized(List<Integer> packetSizes) {
        long total = 0L;
        for (int i = 0; i < packetSizes.size(); i++) {
            total += packetSizes.get(i);
        }
        return total;
    }

    // 🚀 ULTIMATE OPTIMIZED: Primitive Array (Zero Heap Objects, Max CPU Cache Locality)
    public long calculateTotalSizeUltimate(int[] packetSizes) {
        long total = 0L;
        for (int i = 0; i < packetSizes.length; i++) {
            total += packetSizes[i];
        }
        return total;
    }

    /*
     * --------------------------------------------------------------------------------
     * PATTERN 3D: Java Streams vs Hot-Path Traditional Loops
     * --------------------------------------------------------------------------------
     * Streams introduce intermediate pipeline overhead (ReferencePipeline, Spliterator,
     * and Lambda object allocations). For hot data-ingestion paths processing millions
     * of items/sec, traditional for-loops run significantly faster with smaller memory footprints.
     */


    // ================================================================================
    // SECTION 4: COMPLETE PRACTICE CASE STUDY (LOG CLEANER)
    // ================================================================================

    /*
     * QUESTION SCENARIO:
     * Flame Graph shows:
     * 68% CPU width in java.lang.String.replaceAll (Leaf Frame)
     * 82% CPU width in LogCleaner.sanitizeRecord
     * 95% CPU width in LogCleaner.processLogs
     */

    // ❌ UNOPTIMIZED ORIGINAL CODE:
    static class LogCleanerUnoptimized {
        public List<String> processLogs(List<String> rawLogs) {
            List<String> cleanedLogs = new ArrayList<>(); // Issue 1: Unsized List (Resizing overhead)
            for (String log : rawLogs) {                  // Issue 2: Enhanced loop (Iterator allocation)
                cleanedLogs.add(sanitizeRecord(log));
            }
            return cleanedLogs;
        }

        private String sanitizeRecord(String record) {
            String trimmed = record.trim();
            return trimmed.replaceAll("\\s+", " ");        // Issue 3: Re-compiles regex on every row!
        }
    }

    // ✅ COMPLETE REFACTORED CODE:
    static class LogCleanerOptimized {

        // 1. Pre-compiled Pattern: Compiled ONCE at class loading time (Eliminates ~68% CPU compilation overhead)
        private static final Pattern MULTI_SPACE_PATTERN = Pattern.compile("\\s+");

        public List<String> processLogs(List<String> rawLogs) {
            if (rawLogs == null || rawLogs.isEmpty()) {
                return Collections.emptyList();
            }

            // 2. Pre-allocated capacity: Eliminates dynamic array growth & memory churn
            List<String> cleanedLogs = new ArrayList<>(rawLogs.size());

            // 3. Indexed loop: Eliminates Iterator object heap allocations (Stack primitive counter)
            for (int i = 0; i < rawLogs.size(); i++) {
                String log = rawLogs.get(i);
                if (log != null) {
                    cleanedLogs.add(sanitizeRecord(log));
                }
            }
            return cleanedLogs;
        }

        private String sanitizeRecord(String record) {
            String trimmed = record.trim();
            // 4. Reuse pre-compiled Pattern Matcher directly
            return MULTI_SPACE_PATTERN.matcher(trimmed).replaceAll(" ");
        }
    }


    // ================================================================================
    // MAIN METHOD (Verification & Quick Test Run)
    // ================================================================================
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("Fivetran Performance Engineering Revision Sheet Loaded!");
        System.out.println("==========================================================");

        // Test LogCleaner Refactor
        List<String> sampleLogs = List.of(
                "2026-09-14   10:00:00    [INFO]   Pipeline    started ",
                "2026-09-14   10:00:01    [ERROR]  Record failure   ID:9021 ",
                "2026-09-14   10:00:02    [INFO]   Batch   completed "
        );

        LogCleanerOptimized cleaner = new LogCleanerOptimized();
        List<String> cleaned = cleaner.processLogs(sampleLogs);

        System.out.println("\nSample Cleaning Output:");
        for (String log : cleaned) {
            System.out.println("Cleaned: \"" + log + "\"");
        }

        System.out.println("\nAll systems operational. Good luck with your Fivetran interview!");
    }
}
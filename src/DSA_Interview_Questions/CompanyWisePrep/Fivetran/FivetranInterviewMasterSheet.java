package DSA_Interview_Questions.CompanyWisePrep.Fivetran;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ====================================================================================
 * FIVETRAN SSE PERFORMANCE ENGINEERING & CODE OPTIMIZATION - MASTER SHEET
 * ====================================================================================
 * Role: Senior Software Engineer (SSE) @ Fivetran
 * Target Round: Performance Engineering, Code Debugging & Optimization
 *
 * CONTENTS:
 * 1. Flame Graph Mechanics & Verbal Framework (Includes 27.73% vs Total % Nuance)
 * 2. Real Fivetran Interview Case Study: Mixpanel Connector Pipeline
 * 3. Pattern 1: Regex Tuning (Re-compilation, Backtracking, find vs matches)
 * 4. Pattern 2: String Parsing & Allocations (String.split vs Manual Indexing)
 * 5. Pattern 3: JVM Memory, Collection Sizing & Iterator Churn (Indexed Loops)
 * 6. Interactive Main Method for Quick Pre-Interview Execution
 * ====================================================================================
 */
public class FivetranInterviewMasterSheet {

    // ================================================================================
    // SECTION 0: FLAME GRAPH MECHANICS & VERBAL FRAMEWORK
    // ================================================================================
    /*
     * FLAME GRAPH READING RULES:
     * --------------------------------------------------------------------------------
     * 1. X-Axis (Width): Represents TOTAL CPU execution time or sample percentage.
     *    - Wider box = More CPU time spent in that function stack.
     *    - NOT chronological time (ordered alphabetically).
     *
     * 2. Y-Axis (Height): Call stack depth.
     *    - Bottom = Root method (e.g., Worker.run / UpdaterBase.run).
     *    - Top = Leaf method currently executing on CPU.
     *
     * 3. The "Plateau" (Hotspot):
     *    - Wide horizontal flat bars at the VERY TOP mean CPU time is heavily spent inside
     *      that method's body rather than delegating to children.
     *
     * 💡 INTERVIEW NUANCE (Single Tower % vs Total Profile %):
     * --------------------------------------------------------------------------------
     * When hovering over a tooltip (e.g., 27.73% on Pattern.matches above interimEvent):
     * - 27.73% is the CPU width for THAT SPECIFIC METHOD TOWER alone.
     * - The TOTAL regex footprint across the whole application includes OTHER towers
     *   (e.g., castToPrimitiveFromJson at ~15-20%), adding up to ~70% overall CPU time.
     *
     * 4-STEP VERBAL ANSWER FRAMEWORK:
     * --------------------------------------------------------------------------------
     * Step 1: OBSERVE -> "Looking at the flame graph, Pattern.matches in interimEvent consumes
     *                    27.73% CPU time in its tower alone, with overall regex overhead dominating ~70%."
     * Step 2: CAUSE   -> "This happens because String.matches() compiles the regex AST state machine
     *                    on every single record inside high-volume data loops."
     * Step 3: FIX     -> "We extract pre-compiled static final Pattern constants (or use direct O(N)
     *                    String methods), pre-allocate List capacities, and use indexed loops."
     * Step 4: IMPACT  -> "This eliminates repeated state-machine compilation, avoids Iterator heap
     *                    churn, and drastically lowers Garbage Collection pauses."
     */


    // ================================================================================
    // SECTION 1: ACTUAL FIVETRAN INTERVIEW CASE STUDY (Mixpanel Pipeline)
    // ================================================================================

    /*
     * PROBLEM DISCOVERED IN YOUR PREVIOUS INTERVIEW FLAME GRAPH:
     * - interimEvent() consumed 27.73% CPU width calling String.matches("^\\$[a-zA-Z0-9_]+_interim$").
     * - castToPrimitiveFromJson() consumed ~15% CPU width calling String.matches("^\\d+$").
     * - Total regex state-machine recompilation burned ~70% of CPU cycles.
     */

    // ❌ UNOPTIMIZED REAL CODE (As seen in the Flame Graph)
    static class MixpanelPipelineUnoptimized {

        public boolean interimEvent(String eventName) {
            if (eventName == null) return false;
            // String.matches() re-compiles the pattern on EVERY call (27.73% CPU HOG)
            return eventName.matches("^\\$[a-zA-Z0-9_]+_interim$");
        }

        public Object castToPrimitiveFromJson(String value) {
            if (value == null) return null;

            // Re-compiles "^\d+$" for millions of property fields
            if (value.matches("^\\d+$")) {
                return Long.parseLong(value);
            }

            if (value.matches("^(true|false)$")) {
                return Boolean.parseBoolean(value);
            }

            return value;
        }
    }

    // ✅ OPTIMIZED CODE (What to present in the interview)
    static class MixpanelPipelineOptimized {


        private static final Pattern Name = Pattern.compile("^\\$[a-zA-Z0-9_]+_interim$");

        // Pre-compiled patterns (Loaded once into JVM memory)
        private static final Pattern INTERIM_EVENT_PATTERN =
                Pattern.compile("^\\$[a-zA-Z0-9_]+_interim$");
        private static final Pattern NUMERIC_PATTERN = Pattern.compile("^\\d+$");
        private static final Pattern BOOLEAN_PATTERN = Pattern.compile("^(?i)(true|false)$");

        // Option A: Pattern Reuse Fix (Fast & Clean)
        public boolean interimEvent(String eventName) {
            if (eventName == null) return false;
            return INTERIM_EVENT_PATTERN.matcher(eventName).matches();
        }

        // Option B: Zero-Regex String Scan (🚀 Ultra-Fast Alternative)
        public boolean interimEventZeroRegex(String eventName) {
            if (eventName == null) return false;
            return eventName.startsWith("$") && eventName.endsWith("_interim");
        }

        public Object castToPrimitiveFromJson(String value) {
            if (value == null || value.isEmpty()) return value;

            // 1. Fast boolean check
            if (BOOLEAN_PATTERN.matcher(value).matches()) {
                return Boolean.parseBoolean(value);
            }

            // 2. Fast numeric check using primitive char scan (Zero Regex Overhead)
            if (isNumericManual(value)) {
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException ignored) {
                    return value; // Fallback if number exceeds Long.MAX_VALUE
                }
            }

            return value;
        }

        // Direct O(N) primitive char checking eliminates regex engine overhead completely
        private static boolean isNumericManual(String str) {
            int len = str.length();
            for (int i = 0; i < len; i++) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }


    // ================================================================================
    // SECTION 2: REGEX PATTERNS & CATASTROPHIC BACKTRACKING RECAP
    // ================================================================================

    /*
     * Why change logRecord.matches("^.*\\[ERROR\\].*ID:([0-9]+).*$")
     * to ERROR_ID_PATTERN.matcher(logRecord).find()?
     *
     * 1. Matcher.find() vs String.matches():
     *    - String.matches() forces full string match from start to end (^ and $).
     *    - Matcher.find() scans forward to find the substring match, dropping ^.* and .*$.
     *
     * 2. Greedy (.*) vs Non-Greedy (.*?):
     *    - Greedy (.*) swallows string to the end and backtracks step-by-step.
     *    - Reluctant (.*?) advances one char at a time and stops immediately at "ID:".
     */
    private static final Pattern ERROR_ID_PATTERN = Pattern.compile("\\[ERROR\\].*?ID:(\\d+)");

    public boolean validateErrorLog(String logRecord) {
        return ERROR_ID_PATTERN.matcher(logRecord).find();
    }

    /*
     * CATASTROPHIC BACKTRACKING & 3 FIXES TO DEFEND IN CROSS-EXAMINATION:
     * - Problem: Nested quantifiers like (a+)+ or overlapping wildcards (.*)* force
     *   exponential O(2^N) backtracking on near-matching strings.
     *
     * - Fix 1: Negated Character Classes -> Pattern.compile("\"([^\"]*)\",\"([^\"]*)\"")
     * - Fix 2: Possessive Quantifiers     -> Pattern.compile("(a++)++b") (Disables backtracking)
     * - Fix 3: Direct String Operations   -> String.indexOf() / substring()
     */


    // ================================================================================
    // SECTION 3: STRING PARSING & ALLOCATION OPTIMIZATIONS
    // ================================================================================

    /*
     * String.split(",") creates String[] + N new String objects per line.
     * Use index-based scanning with indexOf(',', start) to eliminate array allocation.
     */
    public List<String> parseMultiColumnCsv(String line, int expectedColumns) {
        List<String> tokens = new ArrayList<>(expectedColumns); // Pre-sized!
        int start = 0;
        int nextComma;

        while ((nextComma = line.indexOf(',', start)) != -1) {
            tokens.add(line.substring(start, nextComma).trim());
            start = nextComma + 1;
        }
        tokens.add(line.substring(start).trim()); // Last token
        return tokens;
    }


    // ================================================================================
    // SECTION 4: JVM MEMORY, COLLECTION SIZING & INDEXED LOOPS
    // ================================================================================

    /*
     * WHY USE INDEXED LOOP (for (int i = 0; i < list.size(); i++)) IN HOT PATHS?
     * --------------------------------------------------------------------------------
     * 1. Enhanced for-each (for (String s : list)) calls list.iterator() under the hood.
     * 2. Calls to iterator() allocate short-lived Iterator heap objects per method call,
     *    triggering Garbage Collection pauses.
     * 3. Primitive counter `int i` stays on thread execution STACK (Zero Heap allocations).
     * 4. Eliminates virtual method calls (hasNext(), next()); easier JIT compiler inlining.
     *
     * ⚠️ CRITICAL INTERVIEW CAVEAT:
     * - ONLY use indexed loops for ArrayList or classes implementing RandomAccess (O(1) access).
     * - NEVER use indexed loops for LinkedList (list.get(i) turns O(N) into O(N^2)).
     */

    // FULL CASE STUDY FIX (LogCleaner Case Study)
    static class LogCleanerOptimized {

        private static final Pattern MULTI_SPACE_PATTERN = Pattern.compile("\\s+");

        public List<String> processLogs(List<String> rawLogs) {
            if (rawLogs == null || rawLogs.isEmpty()) {
                return Collections.emptyList();
            }

            // 1. Pre-allocated capacity (Avoids dynamic array growth & array copying)
            List<String> cleanedLogs = new ArrayList<>(rawLogs.size());

            // 2. Indexed loop (Eliminates Iterator heap allocation)
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
            // 3. Reuses pre-compiled pattern matcher
            return MULTI_SPACE_PATTERN.matcher(trimmed).replaceAll(" ");
        }
    }


    // ================================================================================
    // MAIN METHOD (Verification & Quick Practice)
    // ================================================================================
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("Fivetran Master Revision Sheet Loaded!");
        System.out.println("==========================================================");

        // Test Mixpanel Pipeline Fixes
        MixpanelPipelineOptimized pipeline = new MixpanelPipelineOptimized();

        boolean isInterim1 = pipeline.interimEvent("$login_click_interim");
        boolean isInterim2 = pipeline.interimEventZeroRegex("$login_click_interim");
        Object castVal1 = pipeline.castToPrimitiveFromJson("123456789");
        Object castVal2 = pipeline.castToPrimitiveFromJson("true");

        System.out.println("\nMixpanel Optimization Results:");
        System.out.println("Pattern interimEvent match: " + isInterim1);
        System.out.println("Zero-Regex interimEvent match: " + isInterim2);
        System.out.println("Casted Numeric Value Class: " + castVal1.getClass().getSimpleName() + " (" + castVal1 + ")");
        System.out.println("Casted Boolean Value Class: " + castVal2.getClass().getSimpleName() + " (" + castVal2 + ")");

        // Test LogCleaner Fix
        LogCleanerOptimized cleaner = new LogCleanerOptimized();
        List<String> sampleLogs = List.of(
                "2026-09-16   10:00:00    [INFO]   Mixpanel    ingestion ",
                "2026-09-16   10:00:01    [ERROR]  Record failure   ID:9021 "
        );
        List<String> cleaned = cleaner.processLogs(sampleLogs);

        System.out.println("\nLogCleaner Results:");
        for (String log : cleaned) {
            System.out.println("Cleaned: \"" + log + "\"");
        }

        System.out.println("\nReady for Fivetran performance engineering round!");
    }
}
package DSA_Interview_Questions.Fivetran;

public class MaximumNetworkingBreak {
    /**
     * You are organizing an event where there will be a number of presenters. The event starts at time 0 and ends at time t.
     * Time with no presentation is used for networking. Presentations may not overlap and must be given in the same relative order,
     * but they can be shifted earlier or later in time. You are allowed to reschedule at most k presentations.
     * Your goal is to maximize the length of the longest continuous networking break between time 0 and time t.
     * <p>
     * You are given two arrays:
     * start[i]: the scheduled start time of the i‑th presentation
     * finish[i]: the scheduled end time of the i‑th presentation
     * The presentations are non‑overlapping in the original schedule, and start[i] < finish[i] for all i.
     * When you reschedule a presentation, you may move its time interval earlier or later, but:
     * Presentations must not overlap after rescheduling.
     * The order of presentations must remain the same (presentation i must still appear before presentation i + 1).
     * All presentations must lie within the time interval [0, t].
     * You may reschedule at most k presentations.
     * Return the maximum possible length of any single continuous networking break (a contiguous time interval with no presentations)
     **/

    public static void main(String[] args) {
        int n = 3, k = 2, t = 15;
        int[] start = {0, 6, 7}, finish = {5, 7, 8}; // Expected: 8
        int n2 = 4, k2 = 2, t2 = 15;
        int[] start2 = {4, 6, 7, 10}, finish2 = {5, 7, 8, 11}; // Expected: 6
        
        System.out.println("Test 1 - New approach: " + findBreakDuration(n, k, t, start, finish));
        System.out.println("Test 2 - New approach: " + findBreakDuration(n2, k2, t2, start2, finish2));
        
        System.out.println("\nTest 1 - Original: " + findBreakDuration_Original(n, k, t, start, finish));
        System.out.println("Test 2 - Original: " + findBreakDuration_Original(n2, k2, t2, start2, finish2));
    }

    /**
     * ============================================================================
     * EASIER TO UNDERSTAND APPROACH
     * ============================================================================
     * 
     * PROBLEM BREAKDOWN:
     * - We have presentations that can be rescheduled (moved in time)
     * - We can reschedule at most k presentations
     * - Goal: Find the maximum continuous networking break
     * 
     * KEY INSIGHT:
     * The maximum networking break can only occur in 2 places:
     * 1. At the BEGINNING: from time 0 to when the first presentation starts
     * 2. At the END: from when the last presentation ends to time t
     * 
     * WHY? Because presentations must maintain their order and can't overlap.
     * If we create a gap in the middle, we're wasting space that could be used
     * to extend a break at the beginning or end.
     * 
     * STRATEGY:
     * 1. Try every possible break length from 0 to t
     * 2. For each break length, check TWO scenarios:
     *    a) Can we create this break at the START?
     *    b) Can we create this break at the END?
     * 3. To check feasibility: pack all presentations tightly and count moves needed
     * 4. If moves needed ≤ k, this break length is achievable
     * 5. Return the maximum achievable break length
     * 
     * TIME COMPLEXITY: O(t * n) where t is event duration and n is number of presentations
     */
    public static int findBreakDuration(int n, int k, int t, int[] start, int[] finish) {
        if (n <= 0 || t <= 0) return 0;

        // Step 1: Calculate duration of each presentation (this never changes)
        int[] duration = new int[n];
        for (int i = 0; i < n; i++) {
            duration[i] = finish[i] - start[i];
        }

        int maxBreak = 0;

        // Step 2: Try all possible break lengths from 0 to t
        for (int breakLength = 0; breakLength <= t; breakLength++) {
            
            // SCENARIO 1: Break at the BEGINNING
            // Pack all presentations tightly starting after the break
            int movesNeeded = canAchieveBreakAtStart(n, k, t, start, duration, breakLength);
            if (movesNeeded <= k) {
                maxBreak = Math.max(maxBreak, breakLength);
            }
            
            // SCENARIO 2: Break at the END
            // Pack all presentations tightly ending before the break
            movesNeeded = canAchieveBreakAtEnd(n, k, t, start, duration, breakLength);
            if (movesNeeded <= k) {
                maxBreak = Math.max(maxBreak, breakLength);
            }
        }

        return maxBreak;
    }
    
    /**
     * Check if we can create a break of given length at the START
     * 
     * APPROACH:
     * - First presentation should start at 'breakLength' (after the break)
     * - Pack all presentations tightly one after another (no gaps)
     * - Count how many presentations need to be moved from their original position
     * 
     * EXAMPLE: breakLength = 5, presentations with durations [2, 3, 1]
     * - Presentation 0: starts at 5, ends at 7
     * - Presentation 1: starts at 7, ends at 10
     * - Presentation 2: starts at 10, ends at 11
     * 
     * @return number of moves needed, or Integer.MAX_VALUE if not feasible
     */
    private static int canAchieveBreakAtStart(int n, int k, int t, int[] start, int[] duration, int breakLength) {
        int currentTime = breakLength;  // First presentation starts here
        int movesNeeded = 0;
        
        for (int i = 0; i < n; i++) {
            // Check if this presentation needs to be moved
            if (start[i] != currentTime) {
                movesNeeded++;
            }
            
            // Early exit optimization: if we already exceed k moves, no point continuing
            if (movesNeeded > k) {
                return movesNeeded;
            }
            
            // Move to next presentation's start time (current ends, next starts immediately)
            currentTime += duration[i];
        }
        
        // Check if all presentations fit within the event time [0, t]
        if (currentTime > t) {
            return Integer.MAX_VALUE; // Not feasible - presentations overflow past time t
        }
        
        return movesNeeded;
    }
    
    /**
     * Check if we can create a break of given length at the END
     * 
     * APPROACH:
     * - Last presentation should end at 't - breakLength' (before the break)
     * - Pack all presentations tightly backwards from that point
     * - Count how many presentations need to be moved from their original position
     * 
     * EXAMPLE: t = 15, breakLength = 5, presentations with durations [2, 3, 1]
     * - Last presentation ends at 15 - 5 = 10
     * - Presentation 2: starts at 9, ends at 10
     * - Presentation 1: starts at 6, ends at 9
     * - Presentation 0: starts at 4, ends at 6
     * 
     * @return number of moves needed, or Integer.MAX_VALUE if not feasible
     */
    private static int canAchieveBreakAtEnd(int n, int k, int t, int[] start, int[] duration, int breakLength) {
        int currentEndTime = t - breakLength;  // Last presentation ends here
        int movesNeeded = 0;
        
        // Go through presentations in reverse order (from last to first)
        for (int i = n - 1; i >= 0; i--) {
            // Calculate where this presentation should start
            int requiredStartTime = currentEndTime - duration[i];
            
            // Check if it fits (can't start before time 0)
            if (requiredStartTime < 0) {
                return Integer.MAX_VALUE; // Not feasible - presentations overflow before time 0
            }
            
            // Check if this presentation needs to be moved
            if (start[i] != requiredStartTime) {
                movesNeeded++;
            }
            
            // Early exit optimization: if we already exceed k moves, no point continuing
            if (movesNeeded > k) {
                return movesNeeded;
            }
            
            // Move to previous presentation's end time (this one starts where previous ends)
            currentEndTime = requiredStartTime;
        }
        
        return movesNeeded;
    }

    /**
     * ============================================================================
     * ORIGINAL IMPLEMENTATION (kept for reference)
     * ============================================================================
     * 
     * This is the original solution - it works correctly but is harder to understand
     * because it uses less descriptive variable names and combines logic inline.
     */
    public static int findBreakDuration_Original(int n, int k, int t, int[] start, int[] finish) {
        if (n <= 0 || t <= 0) return 0;

        int[] d = new int[n];
        for (int i = 0; i < n; i++) {
            d[i] = finish[i] - start[i];
        }

        int maxBreak = 0;

        // 1. Break at the START: first presentation starts at L (pack everything after L)
        for (int L = 0; L <= t; L++) {
            long reqStart = L;
            int moves = 0;
            boolean valid = true;
            for (int i = 0; i < n; i++) {
                if (start[i] != reqStart) moves++;
                if (moves > k) {
                    valid = false;
                    break;
                }
                reqStart += d[i];
            }
            if (valid && reqStart <= t) {
                maxBreak = Math.max(maxBreak, L);
            }
        }

        // 2. Break at the END: last presentation ends at t - L (pack everything before t - L)
        for (int L = 0; L <= t; L++) {
            long reqEnd = t - L;
            int moves = 0;
            boolean valid = true;
            for (int i = n - 1; i >= 0; i--) {
                long reqStart = reqEnd - d[i];
                if (reqStart < 0) {
                    valid = false;
                    break;
                }
                if (start[i] != reqStart) moves++;
                if (moves > k) {
                    valid = false;
                    break;
                }
                reqEnd = reqStart;
            }
            if (valid && reqEnd >= 0) {
                maxBreak = Math.max(maxBreak, L);
            }
        }

        return maxBreak;
    }
}

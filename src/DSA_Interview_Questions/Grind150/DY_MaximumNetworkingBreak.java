package DSA_Interview_Questions.Grind150;

import java.util.List;

public class DY_MaximumNetworkingBreak {
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
        int[] start = {0, 6, 7}, finish = {5, 7, 8}; // 8
        int n2 = 4, k2 = 2, t2 = 15;
        int[] start2 = {4, 6, 7, 10}, finish2 = {5, 7, 8, 11}; //6
        System.out.println(findBreakDuration(n, k, t, start, finish));
    }

    /**
     * The longest break is either at the start, at the end, or between two consecutive presentations.
     * For a fixed break length L in a fixed place, we pack the presentations in the only valid way and count how many must be moved; that’s the minimum moves needed.
     * Because this move count is not monotonic in L, we iterate over all L in [0, t] instead of binary searching.
     * The solution is the maximum L for which that minimum number of moves is ≤ k, over “break at start” and “break at end” (and optionally “break in the middle”)
     * @param n
     * @param k
     * @param t
     * @param start
     * @param finish
     * @return
     */
    public static int findBreakDuration(int n, int k, int t, int[] start, int[] finish) {
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

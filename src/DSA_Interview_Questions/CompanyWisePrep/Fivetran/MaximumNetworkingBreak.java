package DSA_Interview_Questions.CompanyWisePrep.Fivetran;

/**
 * LeetCode / Fivetran Interview Question: Maximum Networking Break
 * 
 * STRATEGY: Sliding Window over Fixed Endpoints with Prefix Sums
 * - Fixes presentation i as the left boundary of the break (ends at finish[i]).
 * - Fixes presentation j = min(n, i + k + 1) as the right boundary (starts at start[j]).
 * - Reschedules all (j - i - 1) intermediate presentations in O(1) time using prefix sums.
 */
public class MaximumNetworkingBreak {

    /**
     * Finds the maximum continuous networking break duration in O(n) time.
     * 
     * TIME COMPLEXITY:  O(n) single linear pass.
     * SPACE COMPLEXITY: O(n) for prefix sum array.
     */
    public static int maxNetworkingBreak(int n, int k, int t, int[] start, int[] finish) {
        if (n <= 0 || t <= 0) {
            return t;
        }

        // Step 1: Compute prefix sums of presentation durations
        long[] prefD = new long[n + 1];
        for (int i = 0; i < n; i++) {
            prefD[i + 1] = prefD[i] + (finish[i] - start[i]);
        }

        // Base Case: If we can reschedule all presentations, pack them at one end
        if (k >= n) {
            return (int) (t - prefD[n]);
        }

        long maxBreak = 0;

        // Step 2: Iterate over all possible left boundary indices i (-1 represents time 0)
        for (int i = -1; i < n; i++) {
            // Right boundary index j (n represents time t)
            int j = Math.min(n, i + k + 1);

            long leftBound = (i == -1) ? 0 : finish[i];
            long rightBound = (j == n) ? t : start[j];

            // Sum of durations of rescheduled presentations between i and j in O(1)
            long internalDurations = prefD[j] - prefD[i + 1];

            // Maximum break in window [leftBound, rightBound]
            long currentBreak = (rightBound - leftBound) - internalDurations;

            maxBreak = Math.max(maxBreak, currentBreak);
        }

        return (int) maxBreak;
    }

    public static void main(String[] args) {
        // Test Case 1: Standard case
        int n1 = 3, k1 = 2, t1 = 15;
        int[] start1 = {0, 6, 7};
        int[] finish1 = {5, 7, 8};
        System.out.println("Test 1 Result: " + maxNetworkingBreak(n1, k1, t1, start1, finish1));
        // Expected: 8

        // Test Case 2: Middle break counterexample
        int n2 = 3, k2 = 1, t2 = 100;
        int[] start2 = {10, 30, 80};
        int[] finish2 = {20, 40, 90};
        System.out.println("Test 2 Result: " + maxNetworkingBreak(n2, k2, t2, start2, finish2));
        // Expected: 50
    }
}
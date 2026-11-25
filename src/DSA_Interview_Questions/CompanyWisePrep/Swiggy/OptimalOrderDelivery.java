package DSA_Interview_Questions.CompanyWisePrep.Swiggy;

import java.util.Arrays;

/**
 * Problem: Optimal Restaurant Order Delivery (Game Theory / Minimax DP)
 *
 * Description:
 * Restaurants on a 1D road have order counts. Two drivers ("Ram" and "Raju") take turns
 * picking restaurants. Each driver can only choose the leftmost or rightmost available
 * restaurant. Both play optimally to maximize their own total orders. Ram picks first.
 *
 * Find the maximum number of orders Ram can deliver.
 *
 * LeetCode Equivalents:
 * - LC 486: Predict the Winner
 * - LC 877: Stone Game
 */
public class OptimalOrderDelivery {

    // ==========================================
    // Approach 1: Bottom-Up Tabulation (O(N^2))
    // ==========================================
    public static int maxOrdersTabulation(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int n = nums.length;
        int[][] dp = new int[n][n];

        // Process intervals of increasing length
        for (int len = 1; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;

                if (len == 1) {
                    dp[i][j] = nums[i];
                } else if (len == 2) {
                    dp[i][j] = Math.max(nums[i], nums[j]);
                } else {
                    // Ram takes nums[i]: Raju leaves Ram min(dp[i+2][j], dp[i+1][j-1])
                    int pickLeft = nums[i] + Math.min(dp[i + 2][j], dp[i + 1][j - 1]);

                    // Ram takes nums[j]: Raju leaves Ram min(dp[i+1][j-1], dp[i][j-2])
                    int pickRight = nums[j] + Math.min(dp[i + 1][j - 1], dp[i][j - 2]);

                    dp[i][j] = Math.max(pickLeft, pickRight);
                }
            }
        }

        return dp[0][n - 1];
    }

    // =======================================================
    // Approach 2: Top-Down Recursion + Memoization (O(N^2))
    // =======================================================
    public static int maxOrdersMemoization(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int n = nums.length;
        Integer[][] memo = new Integer[n][n];
        return solve(nums, 0, n - 1, memo);
    }

    private static int solve(int[] nums, int i, int j, Integer[][] memo) {
        if (i > j) return 0;
        if (i == j) return nums[i];
        if (i + 1 == j) return Math.max(nums[i], nums[j]);

        if (memo[i][j] != null) {
            return memo[i][j];
        }

        int pickLeft = nums[i] + Math.min(
                solve(nums, i + 2, j, memo),
                solve(nums, i + 1, j - 1, memo)
        );

        int pickRight = nums[j] + Math.min(
                solve(nums, i + 1, j - 1, memo),
                solve(nums, i, j - 2, memo)
        );

        return memo[i][j] = Math.max(pickLeft, pickRight);
    }


    // ==========================================
    // Test Harness
    // ==========================================
    public static void main(String[] args) {
        int[][] testCases = {
                {1, 6, 9, 3},       // Expected: 10
                {8, 15, 3, 7},      // Expected: 22
                {10, 100, 20, 1}    // Expected: 101
        };

        for (int[] testCase : testCases) {
            int resultTab = maxOrdersTabulation(testCase);
            int resultMemo = maxOrdersMemoization(testCase);

            System.out.println("Input: " + Arrays.toString(testCase));
            System.out.println("Output (Tabulation) : Ram delivers " + resultTab + " orders.");
            System.out.println("Output (Memoization): Ram delivers " + resultMemo + " orders.");
            System.out.println("--------------------------------------------------");
        }
    }
}
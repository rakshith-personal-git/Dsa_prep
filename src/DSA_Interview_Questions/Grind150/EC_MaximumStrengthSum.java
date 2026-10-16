package DSA_Interview_Questions.Grind150;

import java.util.Arrays;

public class EC_MaximumStrengthSum {
    /**
     * Given an array arr of n integers, you can perform the following operation any number of times:
     *
     * Choose any index i (0≤i<n−1) and swap arr[i] and arr[i+1].
     * Constraint: Each element in the array can be swapped at most once during the entire process.
     *
     * The strength of an index i is defined as: Strength(i)=arr[i]×(i+1)(using 0-based indexing for the array, but 1-based multiplier for the position strength).
     *
     * Return the maximum possible sum of strengths of all indices after applying optimal swaps.
     */

    public static long maxStrengthSum(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0L;
        }

        int n = arr.length;

        // Base Case 1: Single element
        if (n == 1) {
            return (long) arr[0] * 1;
        }

        long[] dp = new long[n];

        // Base Case 0: dp[0] holds strength of first element
        dp[0] = (long) arr[0] * 1;

        // Base Case 1: Max strength between swapping or not swapping first 2 elements
        long noSwapFirstTwo = dp[0] + (long) arr[1] * 2;
        long swapFirstTwo = (long) arr[1] * 1 + (long) arr[0] * 2;
        dp[1] = Math.max(noSwapFirstTwo, swapFirstTwo);

        // Fill DP table for indices 2 to n-1
        for (int i = 2; i < n; i++) {
            // Option 1: Don't swap arr[i-1] and arr[i]
            long option1 = dp[i - 1] + (long) arr[i] * (i + 1);

            // Option 2: Swap arr[i-1] and arr[i]
            long option2 = dp[i - 2] + (long) arr[i] * i + (long) arr[i - 1] * (i + 1);

            dp[i] = Math.max(option1, option2);
        }

        return dp[n - 1];
    }

    // Main method for testing in local IntelliJ terminal
    public static void main(String[] args) {
        // Test Case 1
        int[] arr1 = {3, 1, 4, 2};
        // Explanation: Swapping (3,1) -> (1,3) and (4,2) -> (2,4) gives [1, 3, 2, 4]
        // Strength = 1*1 + 3*2 + 2*3 + 4*4 = 1 + 6 + 6 + 16 = 29
        System.out.println("Test Case 1 Input: " + Arrays.toString(arr1));
        System.out.println("Maximum Strength Sum: " + maxStrengthSum(arr1)); // Expected: 29
        System.out.println("----------------------------------------");

        // Test Case 2
        int[] arr2 = {1, 2, 3, 4, 5};
        // Explanation: Array is already sorted ascending, swapping reduces strength, so 0 swaps made.
        // Strength = 1*1 + 2*2 + 3*3 + 4*4 + 5*5 = 1 + 4 + 9 + 16 + 25 = 55
        System.out.println("Test Case 2 Input: " + Arrays.toString(arr2));
        System.out.println("Maximum Strength Sum: " + maxStrengthSum(arr2)); // Expected: 55
        System.out.println("----------------------------------------");

        // Test Case 3: Overflow check with large values
        int[] arr3 = {100000, 10, 100000};
        System.out.println("Test Case 3 Input: " + Arrays.toString(arr3));
        System.out.println("Maximum Strength Sum: " + maxStrengthSum(arr3)); // Expected: 500020
    }

}
/**
 *
 * COMPLEXITY:
 * - Time Complexity:  O(N) - single linear pass.
 * - Space Complexity: O(N) - 1D DP table (can be optimized to O(1)).
 */


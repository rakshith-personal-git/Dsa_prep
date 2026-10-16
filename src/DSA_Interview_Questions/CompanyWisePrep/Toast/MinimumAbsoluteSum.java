package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.Arrays;

public class MinimumAbsoluteSum {

    /**
     * Finds two integers x and y such that the sum of the absolute difference 
     * between each element of the array to one of the two chosen integers is minimal.
     * 
     * @param N The size of the array
     * @param A The input integer array
     * @return Minimum possible sum of absolute differences
     */
    public static long minimumAbsoluteSum(int N, int[] A) {
        // Edge Case: If array has 2 or fewer elements, we can pick x = A[0], y = A[1], total cost = 0
        if (N <= 2) {
            return 0;
        }

        // Step 1: Work on a sorted copy of array A
        int[] sortedA = A.clone();
        Arrays.sort(sortedA);

        // Step 2: Build Prefix Sum array (using long to handle large values without overflow)
        long[] P = new long[N + 1];
        for (int i = 0; i < N; i++) {
            P[i + 1] = P[i] + sortedA[i];
        }

        long minTotalCost = Long.MAX_VALUE;

        // Step 3: Iterate over all possible split boundary points k from 1 to N - 1
        // Left group:  sortedA[0 ... k-1]
        // Right group: sortedA[k ... N-1]
        for (int k = 1; k < N; k++) {
            long leftCost = getSubarrayCost(0, k - 1, P);
            long rightCost = getSubarrayCost(k, N - 1, P);

            long totalCost = leftCost + rightCost;
            minTotalCost = Math.min(minTotalCost, totalCost);
        }

        return minTotalCost;
    }

    public static int solve(int[] A) {
        // [SAY OUT LOUD]: "First, check edge cases. If array has 2 or fewer elements, cost is 0."
        if (A == null || A.length <= 2) {
            return 0;
        }

        int n = A.length;

        // [SAY OUT LOUD]: "Step 1: Sort the array so that elements assigned to x form a prefix and
        // elements assigned to y form a suffix."
        Arrays.sort(A);

        // [SAY OUT LOUD]: "dp1[j] stores min absolute difference cost for prefix subarray A[0 ... j]."
        // [SAY OUT LOUD]: "dp2[j] stores min absolute difference cost for suffix subarray A[j ... n-1]."
        int[] dp1 = new int[n];
        int[] dp2 = new int[n];

        // [SAY OUT LOUD]: "Step 2: Populate dp1 left-to-right. Adding A[j] increases median cost by exactly A[j] - A[j/2]."
        dp1[0] = 0; // Single element has cost 0
        for (int j = 1; j < n; j++) {
            dp1[j] = dp1[j - 1] + A[j] - A[j / 2];
        }

        // [SAY OUT LOUD]: "Step 3: Populate dp2 right-to-left. Adding A[j] to suffix increases cost by A[j + (n-j)/2] - A[j]."
        dp2[n - 1] = 0; // Single element has cost 0
        for (int j = n - 2; j >= 0; j--) {
            dp2[j] = dp2[j + 1] + A[j + (n - j) / 2] - A[j];
        }

        // [SAY OUT LOUD]: "Step 4: Iterate over all possible split points j (where left group is [0..j-1] and right group is [j..n-1])."
        int minTotalCost = Integer.MAX_VALUE;
        for (int j = 1; j < n; j++) {
            // [SAY OUT LOUD]: "Combine prefix cost dp1[j-1] and suffix cost dp2[j]."
            minTotalCost = Math.min(minTotalCost, dp1[j - 1] + dp2[j]);
        }

        // [SAY OUT LOUD]: "Return the minimum total absolute difference cost found across all split points."
        return minTotalCost;
    }

    /**
     * Computes the minimum absolute difference cost for sorted range sortedA[L...R] in O(1).
     */
    private static long getSubarrayCost(int L, int R, long[] P) {
        int len = R - L + 1;
        int m = len / 2; // Half size for pairing top and bottom elements
        
        if (m == 0) {
            return 0; // A group of size 1 has cost 0
        }

        // Sum of bottom 'm' elements: sortedA[L ... L + m - 1]
        long bottomSum = P[L + m] - P[L];

        // Sum of top 'm' elements: sortedA[R - m + 1 ... R]
        long topSum = P[R + 1] - P[R + 1 - m];

        return topSum - bottomSum;
    }

    public static void main(String[] args) {
        // Test Case 1 (Problem Example)
        int N1 = 4;
        int[] A1 = {2, 3, 6, 7};
        System.out.println("Test Case 1 Output: " + minimumAbsoluteSum(N1, A1)); 
        System.out.println("Test Case 1 Output: " + solve( A1));
        // Expected: 2 (x=3, y=7 -> |2-3| + |3-3| + |6-7| + |7-7| = 1 + 0 + 0 + 1 = 2)

        // Test Case 2 (Explanation Test Case 1)
        int N2 = 3;
        int[] A2 = {1, 3, 5};
        System.out.println("Test Case 2 Output: " + minimumAbsoluteSum(N2, A2)); 
        System.out.println("Test Case 2 Output: " + solve(A2));
        // Expected: 2 (x=1, y=4 -> |1-1| + |3-4| + |5-4| = 0 + 1 + 1 = 2)

        // Test Case 3 (Explanation Test Case 2)
        int N3 = 4;
        int[] A3 = {3, 2, 5, 11};
        System.out.println("Test Case 3 Output: " + minimumAbsoluteSum(N3, A3)); 
        System.out.println("Test Case 3 Output: " + solve(A3));
        // Expected: 3 (x=3, y=11 -> |2-3| + |3-3| + |5-3| + |11-11| = 1 + 0 + 2 + 0 = 3)
    }
}
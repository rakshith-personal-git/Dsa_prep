package DSA_Interview_Questions.Grind150;

import java.util.Arrays;

/**
 * LeetCode 1423: Maximum Points You Can Obtain from Cards
 *
 * STRATEGY: Fixed-Size Sliding Window (Complement Minimization)
 * - Instead of maximizing k cards picked from ends, find the minimum sum
 *   of the contiguous subarray of length (N - k) left behind in the middle.
 *
 * COMPLEXITY:
 * - Time Complexity:  O(N) - single linear pass to compute total sum and slide window.
 * - Space Complexity: O(1) - auxiliary space using scalar variables.
 */
public class ED_MaximumPointsFromCards {

    public static int maxScore(int[] cardPoints, int k) {
        if (cardPoints == null || cardPoints.length == 0 || k <= 0) {
            return 0;
        }

        int n = cardPoints.length;
        int targetWindowSize = n - k;

        // Calculate total sum of the array
        int totalSum = 0;
        for (int point : cardPoints) {
            totalSum += point;
        }

        // Edge Case: If k equals total length, we must take ALL cards
        if (targetWindowSize == 0) {
            return totalSum;
        }

        // 1. Calculate sum of the first window of size (N - k)
        int currentWindowSum = 0;
        for (int i = 0; i < targetWindowSize; i++) {
            currentWindowSum += cardPoints[i];
        }

        int minWindowSum = currentWindowSum;

        // 2. Slide the window from index targetWindowSize to n - 1
        for (int i = targetWindowSize; i < n; i++) {
            // Add new element on the right, remove old element on the left
            currentWindowSum += cardPoints[i] - cardPoints[i - targetWindowSize];
            minWindowSum = Math.min(minWindowSum, currentWindowSum);
        }

        // Result is Total Sum minus the Minimum Middle Window Sum
        return totalSum - minWindowSum;
    }

    // Main method for testing in local IntelliJ terminal
    public static void main(String[] args) {
        // Test Case 1
        int[] cards1 = {1, 2, 3, 4, 5, 6, 1};
        int k1 = 3;
        System.out.println("Test 1 Cards: " + Arrays.toString(cards1) + ", k = " + k1);
        System.out.println("Max Score: " + maxScore(cards1, k1)); // Expected: 12
        System.out.println("----------------------------------------");

        // Test Case 2
        int[] cards2 = {2, 2, 2};
        int k2 = 2;
        System.out.println("Test 2 Cards: " + Arrays.toString(cards2) + ", k = " + k2);
        System.out.println("Max Score: " + maxScore(cards2, k2)); // Expected: 4
        System.out.println("----------------------------------------");

        // Test Case 3
        int[] cards3 = {9, 7, 7, 9, 7, 7, 9};
        int k3 = 7;
        System.out.println("Test 3 Cards: " + Arrays.toString(cards3) + ", k = " + k3);
        System.out.println("Max Score: " + maxScore(cards3, k3)); // Expected: 55
        System.out.println("----------------------------------------");
    }
}
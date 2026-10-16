package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.ArrayDeque;
import java.util.Deque;

public class KitchenBatchDeliveryWindow {

    /**
     * Finds the maximum number of consecutive orders a runner can pick up such that:
     * 1. Sum of weights <= maxWeight
     * 2. Max prep time difference (max - min) in batch <= maxWaitTime
     */
    public static int maxBatchOrders(int[] prepTime, int[] weight, int maxWeight, int maxWaitTime) {
        // [SAY OUT LOUD]: "Base guard checks for null or mismatched array inputs."
        if (prepTime == null || weight == null || prepTime.length != weight.length || prepTime.length == 0) {
            return 0;
        }

        int n = prepTime.length;
        int left = 0;
        int maxBatchLength = 0;
        int currentWeightSum = 0;

        // [SAY OUT LOUD]: "Monotonic Deques to maintain max and min prep times in O(1) amortized time."
        Deque<Integer> maxDeque = new ArrayDeque<>(); // Monotonically decreasing values
        Deque<Integer> minDeque = new ArrayDeque<>(); // Monotonically increasing values

        // [SAY OUT LOUD]: "Step 1: Expand right pointer across order stream."
        for (int right = 0; right < n; right++) {
            currentWeightSum += weight[right];

            // Update maxDeque (maintain decreasing order)
            while (!maxDeque.isEmpty() && prepTime[maxDeque.peekLast()] <= prepTime[right]) {
                maxDeque.pollLast();
            }
            maxDeque.offerLast(right);

            // Update minDeque (maintain increasing order)
            while (!minDeque.isEmpty() && prepTime[minDeque.peekLast()] >= prepTime[right]) {
                minDeque.pollLast();
            }
            minDeque.offerLast(right);

            // [SAY OUT LOUD]: "Step 2: Shrink left pointer if weight limit OR max wait time difference is violated."
            while (left <= right && (currentWeightSum > maxWeight || 
                   (prepTime[maxDeque.peekFirst()] - prepTime[minDeque.peekFirst()] > maxWaitTime))) {

                currentWeightSum -= weight[left];

                // Evict left index from deques if it matches the current max/min boundary
                if (!maxDeque.isEmpty() && maxDeque.peekFirst() == left) {
                    maxDeque.pollFirst();
                }
                if (!minDeque.isEmpty() && minDeque.peekFirst() == left) {
                    minDeque.pollFirst();
                }

                left++;
            }

            // [SAY OUT LOUD]: "Step 3: Update global maximum window length."
            maxBatchLength = Math.max(maxBatchLength, right - left + 1);
        }

        return maxBatchLength;
    }

    // --- Main Method with Executable IntelliJ Test Cases ---
    public static void main(String[] args) {
        // Test Case 1: Prompt Example
        int[] prepTime1 = {10, 12, 15, 25, 18};
        int[] weight1   = {2,  3,  4,  5,  2};
        int maxWeight1 = 10;
        int maxWaitTime1 = 7;

        int result1 = maxBatchOrders(prepTime1, weight1, maxWeight1, maxWaitTime1);
        System.out.println("Test Case 1 Output: " + result1); // Expected: 3 (Subarray [10, 12, 15])

        // Test Case 2: Weight Bottleneck
        int[] prepTime2 = {10, 10, 10};
        int[] weight2   = {5,  5,  5};
        int maxWeight2 = 9;
        int maxWaitTime2 = 5;

        int result2 = maxBatchOrders(prepTime2, weight2, maxWeight2, maxWaitTime2);
        System.out.println("Test Case 2 Output: " + result2); // Expected: 1
    }
}

/*
 * ============================================================================
 * COMPLEXITY ANALYSIS:
 * ============================================================================
 * Time Complexity: O(N)
 *   Where N is the total number of kitchen orders. Both `right` and `left` 
 *   pointers traverse array indices at most once. Each index is pushed and popped 
 *   from `maxDeque` and `minDeque` at most once, yielding linear O(N) runtime.
 *
 * Space Complexity: O(N) Auxiliary Space
 *   Auxiliary memory space is used by `maxDeque` and `minDeque`, which store 
 *   at most N array indices.
 * ============================================================================
 */
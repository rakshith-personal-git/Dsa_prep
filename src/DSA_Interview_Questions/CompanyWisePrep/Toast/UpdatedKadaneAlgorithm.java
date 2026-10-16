package DSA_Interview_Questions.CompanyWisePrep.Toast;

/**
 * Solution for Updated Kadane's Algorithm:
 * 1. Maximum Sum Circular Subarray (LeetCode 918)
 * 2. Range Index Tracking for Maximum Subarray
 *
 * <p>Time Complexity: O(N)
 * Space Complexity: O(1) auxiliary space
 */
public class UpdatedKadaneAlgorithm {

    // Helper class to return both max sum and corresponding range indices
    public static class SubarrayResult {
        public int maxSum;
        public int startIndex;
        public int endIndex;

        public SubarrayResult(int maxSum, int startIndex, int endIndex) {
            this.maxSum = maxSum;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public String toString() {
            return "SubarrayResult{maxSum=" + maxSum + ", range=[" + startIndex + ", " + endIndex + "]}";
        }
    }

    /**
     * Calculates maximum circular subarray sum in O(N) time and O(1) space.
     */
    public static int maxSubarraySumCircular(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int totalSum = 0;
        int currentMax = 0, maxSum = nums[0];
        int currentMin = 0, minSum = nums[0];

        for (int num : nums) {
            totalSum += num;

            // Standard Kadane: Max Subarray
            currentMax = Math.max(num, currentMax + num);
            maxSum = Math.max(maxSum, currentMax);

            // Inverted Kadane: Min Subarray
            currentMin = Math.min(num, currentMin + num);
            minSum = Math.min(minSum, currentMin);
        }

        // Edge case: All elements are negative
        if (maxSum < 0) {
            return maxSum;
        }

        return Math.max(maxSum, totalSum - minSum);
    }

    /**
     * Extended Kadane: Returns Maximum Subarray Sum along with exact [start, end] indices.
     */
    public static SubarrayResult maxSubarrayWithIndices(int[] nums) {
        if (nums == null || nums.length == 0) {
            return new SubarrayResult(0, -1, -1);
        }

        int maxSum = nums[0];
        int currentMax = 0;

        int bestStart = 0;
        int bestEnd = 0;
        int tempStart = 0;

        for (int i = 0; i < nums.length; i++) {
            currentMax += nums[i];

            if (currentMax > maxSum) {
                maxSum = currentMax;
                bestStart = tempStart;
                bestEnd = i;
            }

            // If running sum drops below 0, reset start index for the next potential subarray
            if (currentMax < 0) {
                currentMax = 0;
                tempStart = i + 1;
            }
        }

        return new SubarrayResult(maxSum, bestStart, bestEnd);
    }

    // --- Main Method with Test Cases ---
    public static void main(String[] args) {
        // Test Case 1: Standard non-wrapping max array
        int[] test1 = {1, -2, 3, -2};
        System.out.println("Test 1 Circular Max: " + maxSubarraySumCircular(test1)); // Expected: 3

        // Test Case 2: Circular wrapping max array
        int[] test2 = {5, -3, 5};
        System.out.println("Test 2 Circular Max: " + maxSubarraySumCircular(test2)); // Expected: 10 (5 + 5)

        // Test Case 3: All negative elements
        int[] test3 = {-3, -2, -1};
        System.out.println("Test 3 All Negative: " + maxSubarraySumCircular(test3)); // Expected: -1

        // Test Case 4: Index Tracking Test
        int[] test4 = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        SubarrayResult indicesResult = maxSubarrayWithIndices(test4);
        System.out.println("Test 4 Indices Tracking: " + indicesResult); 
        // Expected: maxSum=6, range=[3, 6] (subarray [4, -1, 2, 1])
    }
}
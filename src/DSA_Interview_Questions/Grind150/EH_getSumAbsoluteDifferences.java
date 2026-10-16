package DSA_Interview_Questions.Grind150;

import java.util.Arrays;

/**
 * LeetCode 1685: Sum of Absolute Differences in a Sorted Array
 *
 * STRATEGY: Prefix/Suffix Sum Algebraic Reduction
 * - Since array is sorted, elements to the left are <= nums[i] and elements to right are >= nums[i].
 * - result[i] = (i * nums[i] - leftSum) + (rightSum - (n - 1 - i) * nums[i])
 *
 * COMPLEXITY:
 * - Time Complexity:  O(N) - two linear passes over the array.
 * - Space Complexity: O(1) auxiliary space (excluding output array).
 */
public class EH_getSumAbsoluteDifferences {

    public static int[] getSumAbsoluteDifferences(int[] nums) {
        if (nums == null || nums.length == 0) {
            return new int[0];
        }

        int n = nums.length;
        int[] result = new int[n];

        // Step 1: Compute total sum of all elements -> O(N)
        int totalSum = 0;
        for (int num : nums) {
            totalSum += num;
        }

        int leftSum = 0;

        // Step 2: Compute absolute differences using the formula -> O(N)
        for (int i = 0; i < n; i++) {
            // rightSum is whatever remains in totalSum minus leftSum and current element
            int rightSum = totalSum - leftSum - nums[i];

            int leftCount = i;
            int rightCount = n - 1 - i;

            int leftContribution = (leftCount * nums[i]) - leftSum;
            int rightContribution = rightSum - (rightCount * nums[i]);

            result[i] = leftContribution + rightContribution;

            // Add nums[i] to leftSum for the next iteration
            leftSum += nums[i];
        }

        return result;
    }

    // Main method for testing in local IntelliJ terminal
    public static void main(String[] args) {
        // Test Case 1
        int[] nums1 = {2, 3, 5};
        System.out.println("Test 1 Input: " + Arrays.toString(nums1));
        System.out.println("Result: " + Arrays.toString(getSumAbsoluteDifferences(nums1)));
        // Expected: [4, 3, 5]
        System.out.println("----------------------------------------");

        // Test Case 2
        int[] nums2 = {1, 4, 6, 8, 10};
        System.out.println("Test 2 Input: " + Arrays.toString(nums2));
        System.out.println("Result: " + Arrays.toString(getSumAbsoluteDifferences(nums2)));
        // Expected: [24, 15, 13, 15, 21]
        System.out.println("----------------------------------------");
    }
}


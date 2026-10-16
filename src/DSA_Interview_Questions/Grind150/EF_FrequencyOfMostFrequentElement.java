package DSA_Interview_Questions.Grind150;

import java.util.Arrays;

/**
 * LeetCode 1838: Frequency of the Most Frequent Element
 *
 * STRATEGY: Sorting + Sliding Window + Math
 * - Operations needed to make window [l ... r] equal to nums[r]:
 *   Cost = (nums[r] * windowSize) - windowSum
 *
 * COMPLEXITY:
 * - Time Complexity:  O(N log N) - sorting dominates the linear O(N) sliding window pass.
 * - Space Complexity: O(1) auxiliary space (or O(log N) for Arrays.sort space).
 */
public class EF_FrequencyOfMostFrequentElement {

    public static int maxFrequency(int[] nums, int k) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        // Step 1: Sort array ascending so closest values sit adjacent -> O(N log N)
        Arrays.sort(nums);

        int l = 0;
        long windowSum = 0; // Use long to prevent integer overflow!
        int maxFreq = 0;

        // Step 2: Sliding Window -> O(N)
        for (int r = 0; r < nums.length; r++) {
            windowSum += nums[r];

            // Operations needed = (Target Value * Count) - Current Sum
            // If operations exceed k, shrink window from the left
            while ((long) nums[r] * (r - l + 1) - windowSum > k) {
                windowSum -= nums[l];
                l++;
            }

            // Valid window found! Update max frequency
            maxFreq = Math.max(maxFreq, r - l + 1);
        }

        return maxFreq;
    }

    // Main method for testing in local IntelliJ terminal
    public static void main(String[] args) {
        // Test Case 1
        int[] nums1 = {1, 2, 4};
        int k1 = 5;
        System.out.println("Test 1 Input: " + Arrays.toString(nums1) + ", k = " + k1);
        System.out.println("Max Frequency: " + maxFrequency(nums1, k1)); // Expected: 3
        System.out.println("----------------------------------------");

        // Test Case 2
        int[] nums2 = {1, 4, 8, 13};
        int k2 = 5;
        System.out.println("Test 2 Input: " + Arrays.toString(nums2) + ", k = " + k2);
        System.out.println("Max Frequency: " + maxFrequency(nums2, k2)); // Expected: 2 (Increment 4 to 8 -> [1, 8, 8, 13])
        System.out.println("----------------------------------------");

        // Test Case 3
        int[] nums3 = {3, 9, 6};
        int k3 = 2;
        System.out.println("Test 3 Input: " + Arrays.toString(nums3) + ", k = " + k3);
        System.out.println("Max Frequency: " + maxFrequency(nums3, k3)); // Expected: 1
        System.out.println("----------------------------------------");
    }
}
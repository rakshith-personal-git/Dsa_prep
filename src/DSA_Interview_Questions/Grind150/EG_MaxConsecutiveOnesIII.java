package DSA_Interview_Questions.Grind150;

import java.util.Arrays;

/**
 * LeetCode 1004: Max Consecutive Ones III
 *
 * STRATEGY: Dynamic Sliding Window
 * - Reframe "flip at most k zeros" to "find longest window with <= k zeros".
 *
 * COMPLEXITY:
 * - Time Complexity:  O(N) - right and left pointers each traverse array at most once.
 * - Space Complexity: O(1) - constant auxiliary space.
 */
public class EG_MaxConsecutiveOnesIII {

    public static int longestOnes(int[] nums, int k) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int left = 0;
        int zeroCount = 0;
        int maxLen = 0;

        // Expand window using right pointer -> O(N)
        for (int right = 0; right < nums.length; right++) {
            // Step 1: If current element is 0, increment zero count
            if (nums[right] == 0) {
                zeroCount++;
            }

            // Step 2: If zeros in window exceed k, shrink window from left
            while (zeroCount > k) {
                if (nums[left] == 0) {
                    zeroCount--;
                }
                left++; // Shrink window
            }

            // Step 3: Window [left ... right] is valid! Update max length
            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }

    // Main method for testing in local IntelliJ terminal
    public static void main(String[] args) {
        // Test Case 1
        int[] nums1 = {1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0};
        int k1 = 2;
        System.out.println("Test 1 Input: " + Arrays.toString(nums1) + ", k = " + k1);
        System.out.println("Max Consecutive Ones: " + longestOnes(nums1, k1)); // Expected: 6
        System.out.println("----------------------------------------");

        // Test Case 2
        int[] nums2 = {0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1};
        int k2 = 3;
        System.out.println("Test 2 Input: " + Arrays.toString(nums2) + ", k = " + k2);
        System.out.println("Max Consecutive Ones: " + longestOnes(nums2, k2)); // Expected: 10
        System.out.println("----------------------------------------");

        // Test Case 3: k = 0 (No flips allowed)
        int[] nums3 = {1, 1, 0, 1, 1, 1};
        int k3 = 0;
        System.out.println("Test 3 Input: " + Arrays.toString(nums3) + ", k = " + k3);
        System.out.println("Max Consecutive Ones: " + longestOnes(nums3, k3)); // Expected: 3
    }
}
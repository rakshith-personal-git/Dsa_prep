package DSA_Interview_Questions.DP;

import java.util.Arrays;

public class LongestIncreasingSubsequence {

    /**
     * Given an integer array nums, return the length of the longest strictly increasing subsequence
     * Example 1:
     * Input: nums = [10,9,2,5,3,7,101,18]
     * Output: 4
     * Explanation: The longest increasing subsequence is [2,3,7,101], therefore the length is 4.
     * <p>
     * Example 2:
     * Input: nums = [0,1,0,3,2,3]
     * Output: 4
     * <p>
     * Example 3:
     * Input: nums = [7,7,7,7,7,7,7]
     * Output: 1
     * <p>
     * Constraints:
     * 1 <= nums.length <= 2500
     * -104 <= nums[i] <= 104
     * Follow up: Can you come up with an algorithm that runs in O(n log(n)) time complexity?
     **/

    public static void main(String[] args) {
        int nums[] = {10, 9, 2, 5, 3, 7, 101, 18};
        int ans = lengthOfLIS(nums);
        System.out.println(ans);
    }

    private static int lengthOfLIS(int[] nums) {
        // Check if the array is null or empty
        if (nums == null || nums.length == 0) {
            return 0; // If the array is empty, LIS length is 0
        }

        // Create an array to store the length of LIS ending at each index
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1); // Initialize each LIS length to 1, as the minimum length is always 1
        int ans = 0; // Variable to store the overall LIS length

        // Iterate over each element in the array
        for (int i = 0; i < nums.length; i++) {
            int max = 0; // Variable to store the maximum LIS length ending at the current index

            // Iterate over previous elements to find the maximum LIS length
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) { // Check if the current element is greater than the previous element
                    max = Math.max(max, dp[j]); // Update the maximum LIS length
                }
            }

            // Update the LIS length for the current index i.e
            // incrementing by 1 since the element in the current index is the last element in the sequence
            dp[i] = max + 1;

            // Update the overall LIS length
            ans = Math.max(ans, dp[i]);
        }

        // Return the overall LIS length
        return ans;
    }

/**
 * TC is O(N^2) and SC is O(N)*/

}

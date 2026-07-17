package DSA_Interview_Questions.Grind150;

public class BN_MaximumSubarray {
    /**
     * Given an integer array nums, find the subarray with the largest sum, and return its sum.
     * <p>
     * Example 1:
     * Input: nums = [-2,1,-3,4,-1,2,1,-5,4]
     * Output: 6
     * Explanation: The subarray [4,-1,2,1] has the largest sum 6.
     * <p>
     * Example 2:
     * Input: nums = [1]
     * Output: 1
     * Explanation: The subarray [1] has the largest sum 1.
     * <p>
     * Example 3:
     * Input: nums = [5,4,-1,7,8]
     * Output: 23
     * Explanation: The subarray [5,4,-1,7,8] has the largest sum 23.
     * <p>
     * Constraints:
     * 1 <= nums.length <= 105
     * -104 <= nums[i] <= 104
     * <p>
     * Follow up: If you have figured out the O(n) solution, try coding another solution using the divide and conquer approach, which is more subtle.
     */

    public static void main(String[] args) {
        int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println(maxSubArray(nums));
        System.out.println(maxSubArray2(nums));
        System.out.println(maxSubArray3(nums));
    }

    private static int maxSubArray(int[] nums) {
        //solving using Kadane
        int globalSum = nums[0];
        int currentSum = nums[0];

        for (int i = 1 ; i < nums.length; i++) {
            currentSum = Math.max(currentSum + nums[i], nums[i]);
            globalSum = Math.max(globalSum, currentSum);
        }
        return globalSum;
    }
    //TC is O(N)
    //SC is O(1)

    private static int maxSubArray2(int[] nums) {
        // Kick off the divide and conquer with the full array boundaries
        return findMaxSubArray(nums, 0, nums.length - 1);
    }

    private static int findMaxSubArray(int[] nums, int left, int right) {
        // Base case: Only one element left in this segment
        if (left == right) {
            return nums[left];
        }

        int mid = left + (right - left) / 2;

        // 1. Find max in the left half
        int leftMax = findMaxSubArray(nums, left, mid);

        // 2. Find max in the right half
        int rightMax = findMaxSubArray(nums, mid + 1, right);

        // 3. Find max crossing the midpoint
        int crossMax = findMaxCrossingSubarray(nums, left, mid, right);

        // Return the absolute maximum of the three possibilities
        return Math.max(Math.max(leftMax, rightMax), crossMax);
    }
    //Time Complexity: O(N log N)
    //SC O(log N)

    private static int findMaxCrossingSubarray(int[] nums, int left, int mid, int right) {
        // Scan left from mid
        int leftSum = Integer.MIN_VALUE;
        int currentSum = 0;
        for (int i = mid; i >= left; i--) {
            currentSum += nums[i];
            if (currentSum > leftSum) {
                leftSum = currentSum;
            }
        }

        // Scan right from mid + 1
        int rightSum = Integer.MIN_VALUE;
        currentSum = 0;
        for (int i = mid + 1; i <= right; i++) {
            currentSum += nums[i];
            if (currentSum > rightSum) {
                rightSum = currentSum;
            }
        }

        // Combine the two halves
        return leftSum + rightSum;
    }
    //SDE-3 Edge Insight: If an interviewer asks, "Can you make the Divide and Conquer approach run in $O(N)$?",
    // the answer is yes. You can achieve this by returning a custom object from the recursive calls that tracks four variables:
    // totalSum, maxPrefixSum, maxSuffixSum, and maxSubarraySum.
    // This allows you to merge the left and right halves in $O(1)$ time instead of iterating, which drops the overall time to $O(N)$.

    private static class SubarrayState {
        int totalSum;
        int maxPrefixSum;
        int maxSuffixSum;
        int maxSubArraySum;

        public SubarrayState(int totalSum, int maxPrefixSum, int maxSuffixSum, int maxSubArraySum) {
            this.totalSum = totalSum;
            this.maxPrefixSum = maxPrefixSum;
            this.maxSuffixSum = maxSuffixSum;
            this.maxSubArraySum = maxSubArraySum;
        }
    }

    private static int maxSubArray3(int[] nums) {
        // The answer is the maxSubArraySum of the entire array segment
        return findMax(nums, 0, nums.length - 1).maxSubArraySum;
    }



    private static SubarrayState findMax(int[] nums, int left, int right) {
        // Base case: A single element segment
        if (left == right) {
            int val = nums[left];
            return new SubarrayState(val, val, val, val);
        }

        int mid = left + (right - left) / 2;

        // Recursively fetch the state of the left and right halves
        SubarrayState leftState = findMax(nums, left, mid);
        SubarrayState rightState = findMax(nums, mid + 1, right);

        // Merge the states in O(1) time
        int totalSum = leftState.totalSum + rightState.totalSum;

        int maxPrefixSum = Math.max(leftState.maxPrefixSum, leftState.totalSum + rightState.maxPrefixSum);

        int maxSuffixSum = Math.max(rightState.maxSuffixSum, rightState.totalSum + leftState.maxSuffixSum);

        int maxSubArraySum = Math.max(
                Math.max(leftState.maxSubArraySum, rightState.maxSubArraySum), // Best entirely in left or right
                leftState.maxSuffixSum + rightState.maxPrefixSum               // Best crossing the midpoint
        );

        return new SubarrayState(totalSum, maxPrefixSum, maxSuffixSum, maxSubArraySum);
    }
}

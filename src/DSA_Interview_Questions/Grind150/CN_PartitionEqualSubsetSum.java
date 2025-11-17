package DSA_Interview_Questions.Grind150;

public class CN_PartitionEqualSubsetSum {

    /**
     * Given an integer array nums, return true if you can partition the array into two subsets
     * such that the sum of the elements in both subsets is equal or false otherwise.
     * <p>
     * Example 1:
     * Input: nums = [1,5,11,5]
     * Output: true
     * Explanation: The array can be partitioned as [1, 5, 5] and [11].
     * <p>
     * Example 2:
     * Input: nums = [1,2,3,5]
     * Output: false
     * Explanation: The array cannot be partitioned into equal sum subsets.
     * <p>
     * Constraints:
     * 1 <= nums.length <= 200
     * 1 <= nums[i] <= 100
     */

    public static void main(String[] args) {
        int[] nums = {1, 5, 11, 5};
//        int[] nums = {1,2,5};
        System.out.println(canPartition(nums));
        System.out.println(canPartitionUsingDfs(nums));
    }

    private static boolean canPartition(int[] nums) {
        int sum = 0;
        for (int i : nums) {
            sum += i;
        }
        if (sum % 2 != 0) {
            return false;
        }

        int subsetSum = sum / 2;
        boolean[] dp = new boolean[subsetSum + 1];
        dp[0] = true;

        // Iterate through the nums array
        for (int num : nums) {
            // Start from the target and move backward to update dp[]
            for (int i = subsetSum; i >= num; i--) {
                dp[i] = dp[i] || dp[i - num];
            }
        }

        return dp[subsetSum]; //because if there is one subset equal to target, then other subset will be equal to target since its half of total sum
    }

    public static boolean canPartitionUsingDfs(int[] nums) {
        int sum = 0;
        for (int i : nums) {
            sum += i;
        }
        if (sum % 2 != 0) {
            return false;
        }

        int target = sum / 2;
        Boolean[][] memo = new Boolean[nums.length + 1][target + 1];

        return dfs(nums, 0, 0, target, memo);
    }

    public static boolean dfs(int[] nums, int start, int subtotal, int target, Boolean[][] memo) {
        // Base Cases
        if (subtotal == target)
            return true;
        if (start >= nums.length - 1 || subtotal > target)
            return false;
        // check if subtotal for given n is already computed and stored in memo
        if (memo[start][subtotal] != null) {
            return memo[start][subtotal];
        }

        return memo[start][subtotal] =
                dfs(nums, start + 1, subtotal + nums[start + 1], target, memo) ||
                        dfs(nums, start + 1, subtotal, target, memo);
    }

}

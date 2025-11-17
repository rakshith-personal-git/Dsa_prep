package DSA_Interview_Questions.HashMap;

import java.util.HashMap;

public class SubarraySumsDivisibleByK {
    /**
     * Given an integer array nums and an integer k, return the number of non-empty subarrays that have a sum divisible by k.
     * A subarray is a contiguous part of an array.
     * <p>
     * Example 1:
     * Input: nums = [4,5,0,-2,-3,1], k = 5
     * Output: 7
     * Explanation: There are 7 subarrays with a sum divisible by k = 5:
     * [4, 5, 0, -2, -3, 1], [5], [5, 0], [5, 0, -2, -3], [0], [0, -2, -3], [-2, -3]
     * <p>
     * Example 2:
     * Input: nums = [5], k = 9
     * Output: 0
     * <p>
     * Constraints:
     * 1 <= nums.length <= 3 * 104
     * -104 <= nums[i] <= 104
     * 2 <= k <= 104
     **/
    public static void main(String[] args) {
        int[] nums = {4, 5, 0, -2, -3, 1};
        int k = 5;
        int ans = subArraysDivByK(nums, k);
        System.out.println(ans);
    }

    private static int subArraysDivByK(int[] nums, int k) {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);
        int prefixSum = 0;
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            prefixSum += nums[i];
            int mod = ((prefixSum % k) + k) % k; //adding & dividing by k separately in case if mod result was -ve
            if (map.containsKey(mod)) {
               count += map.get(mod);
            }
            map.put(mod, map.getOrDefault(mod, 0) + 1);
        }
        return count;
    }
}

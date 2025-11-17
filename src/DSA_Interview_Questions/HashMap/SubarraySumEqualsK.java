package DSA_Interview_Questions.HashMap;

import java.util.HashMap;

public class SubarraySumEqualsK {
    /**
     * Given an array of integers nums and an integer k, return the total number of subarrays whose sum equals to k.
     * A subarray is a contiguous non-empty sequence of elements within an array.
     *
     * Example 1:
     * Input: nums = [1,1,1], k = 2
     * Output: 2
     *
     * Example 2:
     * Input: nums = [1,2,3], k = 3
     * Output: 2
     *
     * Constraints:
     * 1 <= nums.length <= 2 * 104
     * -1000 <= nums[i] <= 1000
     * -107 <= k <= 107
     * */

    public static void main(String[] args) {
        int[] nums = {1,2,3};
//        int[] nums = {1,-1,0};
        int k = 2;
//        int k = 0;
        int ans = subarraySum(nums, k);
        System.out.println(ans);
    }

    private static int subarraySum(int[] nums, int k) {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(0, 1); //this is for when prefix == k
        int ans = 0;
        int prefixSum = 0;
        for (int i : nums){
            prefixSum += i;
            if (map.containsKey(prefixSum - k)) {
                ans+= map.get(prefixSum - k);
            }
            map.put(prefixSum, map.getOrDefault(prefixSum, 0) + 1);
        }
        return ans;
    }

    //TC is O(N) and SC is O(N)
}

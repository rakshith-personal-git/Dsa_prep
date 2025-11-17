package DSA_Interview_Questions.HashMap;

import java.util.HashMap;

public class LongestSubarrayWithSumDivisibleByK {
    /**
     * Given an unsorted array of integers and an integer K, find the length of longest subArray whose sum is divisible by K
     * */

    public static void main(String[] args) {
        int[] nums = {2,7,6,1,4,5};
        int k = 3;
        int ans = longestSubArrayWithSumDivisibleByK(nums, k);
        System.out.println(ans);
    }

    //Approach is that we can use hashMap to store the reminder of prefixSum for each index
    // there is one logic/observation i.e if a reminder is repeating then the sum of the subArray b/w those index's is divisible by K
    private static int longestSubArrayWithSumDivisibleByK(int[] nums, int k) {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(0, -1);
        int prefixSum = 0;
        int maxLength = 0;
        for (int i = 0; i < nums.length; i++) {
            prefixSum += nums[i];
            int mod = ((prefixSum % k) + k) % k; //adding & dividing by k separately in case if mod result was -ve
            if (map.containsKey(mod)) {
                maxLength = Math.max(maxLength, i - map.get(mod));
            } else {
                map.put(mod,i);
            }
        }
        return maxLength;
    }
}

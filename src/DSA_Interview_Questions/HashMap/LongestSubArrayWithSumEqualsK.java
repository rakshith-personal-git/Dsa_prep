package DSA_Interview_Questions.HashMap;

import java.util.HashMap;

public class LongestSubArrayWithSumEqualsK {
    /**
     * Given an unsorted array of integers and an integer K, find the length of longest subArray with sum = K.
     * */

    public static void main(String[] args) {
        int[] arr = {10,5,2,7,1,9};
        int k = 15;
        int ans = longestSubArrayWithSumK(arr, k);
        System.out.println(ans);
    }

    private static int longestSubArrayWithSumK(int[] arr, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(0, -1); //to cover the scenario when prefix == target
        int maxLength = -1;
        int prefixSum = 0;

        for (int i = 0; i < arr.length; i++) {
            prefixSum += arr[i];
            if (map.containsKey(prefixSum - target)) {
                maxLength += Math.max(maxLength, i - map.get(prefixSum - target));
                // getting the maximum out of the maxLength variable or from (subTracking index position from the map value)
            }
            if (!map.containsKey(prefixSum)){
                map.put(prefixSum, 1); //the prefix sum is appearing for the first time
            }
        }
        return maxLength;
    }
}

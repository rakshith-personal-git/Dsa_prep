package DSA_Interview_Questions.Grind150;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AW_ContainsDuplicate {
    /**
     *Given an integer array nums, return true if any value appears at least twice in the array, and return false if every element is distinct.
     *
     * Example 1:
     * Input: nums = [1,2,3,1]
     * Output: true
     *
     *  Example 2:
     * Input: nums = [1,2,3,4]
     * Output: false
     *
     * Example 3:
     * Input: nums = [1,1,1,3,3,4,3,2,4,2]
     * Output: true
     *
     * Constraints:
     * 1 <= nums.length <= 105
     * -109 <= nums[i] <= 109
     * */

    public static void main(String[] args) {
        int[] nums = {1,1,1,3,3,4,3,2,4,2};
        boolean ans = containsDuplicate(nums);
        System.out.println(ans);
        ans = containsDuplicate2(nums);
        System.out.println(ans);
    }

    private static boolean containsDuplicate(int[] nums) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i : nums) {
            if (map.containsKey(i)) {
                return true;
            } else {
                map.put(i, 1);
            }
        }
        return false;
    }

    //or I could use HasSet also since I'm just checking if it contains and not incrementing any values
    private static boolean containsDuplicate2(int[] nums) {
        HashSet<Integer> hashSet = new HashSet<>();
        for(int i = 0; i < nums.length; i++){
            if(hashSet.contains(nums[i])){
                return true;
            } else {
                hashSet.add(nums[i]);
            }
        }
        return false;
    }

    // no change in TC and SC for either of them, it's
    //Time complexity: O(n)
    //Space complexity: O(n)

}

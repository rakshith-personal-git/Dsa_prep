package DSA_Interview_Questions.Grind150;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BH_MissingNumber {
    /**
     * Given an array nums containing n distinct numbers in the range [0, n], return the only number in the range that is missing from the array.
     * <p>
     * Example 1:
     * Input: nums = [3,0,1]
     * Output: 2
     * Explanation: n = 3 since there are 3 numbers, so all numbers are in the range [0,3]. 2 is the missing number in the range since it does not appear in nums.
     * <p>
     * Example 2:
     * Input: nums = [0,1]
     * Output: 2
     * Explanation: n = 2 since there are 2 numbers, so all numbers are in the range [0,2]. 2 is the missing number in the range since it does not appear in nums.
     * <p>
     * Example 3:
     * Input: nums = [9,6,4,2,3,5,7,0,1]
     * Output: 8
     * Explanation: n = 9 since there are 9 numbers, so all numbers are in the range [0,9]. 8 is the missing number in the range since it does not appear in nums.
     * <p>
     * Constraints:
     * <p>
     * n == nums.length
     * 1 <= n <= 104
     * 0 <= nums[i] <= n
     * All the numbers of nums are unique.
     * <p>
     * Follow up: Could you implement a solution using only O(1) extra space complexity and O(n) runtime complexity?
     */

    public static void main(String[] args) {
        int[] nums = {9, 8, 4, 2, 3, 5, 7, 0, 1};
        int ans = missingNumber(nums);
        System.out.println(ans);
        ans = missingNumberOptimised(nums);
        System.out.println(ans);
        ans = missingNumberOptimised2(nums);
        System.out.println(ans);
    }


    private static int missingNumber(int[] nums) {
        Map<Integer, Boolean> map = new HashMap<>();

        for (int i : nums) {
            map.put(i, true);
        }

        for (int i = 0; i <= nums.length; i++) {
            if (!map.containsKey(i)) {
                return i;
            }
        }
        //it will never enter here
        return nums.length;
    }


    private static int missingNumberOptimised(int[] nums) {
        int n = nums.length;
        boolean[] booleanArray = new boolean[n + 1];
        Arrays.fill(booleanArray, false);
        for (int i = 0; i < nums.length; i++) {
            booleanArray[nums[i]] = true;
        }
        for (int i = 0; i < booleanArray.length; i++) {
            if (!booleanArray[i]) return i;
        }
        //will never enter here
        return 0;
    }

    private static int missingNumberOptimised2(int[] nums) {
        int res = nums.length;
        for (int i = 0; i < nums.length; i++) {
            res += (i - nums[i]);
        }
        return res;
        //logic is simple if we sum up 0 to n and then subtract from sum of values in array, it shd be equal to missing number
    }

}

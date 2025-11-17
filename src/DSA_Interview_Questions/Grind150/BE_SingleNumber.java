package DSA_Interview_Questions.Grind150;

import java.util.HashMap;
import java.util.Map;

public class BE_SingleNumber {
    /**
     * Given a non-empty array of integers nums, every element appears twice except for one. Find that single one.
     * You must implement a solution with a linear runtime complexity and use only constant extra space.
     * <p>
     * Example 1:
     * Input: nums = [2,2,1]
     * Output: 1
     * <p>
     * Example 2:
     * Input: nums = [4,1,2,1,2]
     * Output: 4
     * <p>
     * Example 3:
     * Input: nums = [1]
     * Output: 1
     * <p>
     * Constraints:
     * 1 <= nums.length <= 3 * 104
     * -3 * 104 <= nums[i] <= 3 * 104
     * Each element in the array appears twice except for one element which appears only once.
     */

    public static void main(String[] args) {
//        int[] nums = {4, 1, 2, 1, 2};
        int[] nums = {2,2,1};
        int ans = singleNumber(nums);
        System.out.println(ans);
        ans = singleNumberOptimalSolution(nums);
        System.out.println(ans);
    }

    private static int singleNumberOptimalSolution(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result = result^num;
        }
        return result;
    }
    //The ^ ie XOR (exclusive OR) operation returns 1 if the two bits are different, and 0 if they are the same.
    //For example, consider the array [2, 2, 1]:
    //2 XOR 2 XOR 1 = (2 XOR 2) XOR 1 = 0 XOR 1 = 1
    //
    //Let's walk through the example [4, 1, 2, 1, 2]:
    //result = 0.
    //Iterate through each element:
    //result = 0 XOR 4 = 4.
    //result = 4 XOR 1 = 5.
    //result = 5 XOR 2 = 7.
    //result = 7 XOR 1 = 6.
    //result = 6 XOR 2 = 4.
    //After the iteration, result holds the value 4, which is the single number that appears only once.

    private static int singleNumber(int[] nums) {
        Map<Integer, Integer> numsCount = new HashMap<>();
        Integer ans = null;
        for (int i = 0; i < nums.length; i++) {
            if (numsCount.containsKey(nums[i])) {
                numsCount.put(nums[i], numsCount.get(nums[i]) + 1);
                ans = null;
            } else {
                numsCount.put(nums[i], 1);
                ans = nums[i];
            }
        }
        if (ans == null) {
            for (Map.Entry<Integer, Integer> entry : numsCount.entrySet()) {
                if (entry.getValue() == 1) {
                    return entry.getKey();
                }
            }
        } else {
            return ans;
        }

        return 0;
    }
}

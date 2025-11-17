package DSA_Interview_Questions.Grind150;

import java.util.HashMap;
import java.util.Map;

public class AR_MajorityElement {
    /**
     * Given an array nums of size n, return the majority element.
     * The majority element is the element that appears more than ⌊n / 2⌋ times.
     * You may assume that the majority element always exists in the array.
     * <p>
     * Example 1:
     * Input: nums = [3,2,3]
     * Output: 3
     * <p>
     * Example 2:
     * Input: nums = [2,2,1,1,1,2,2]
     * Output: 2
     * <p>
     * Constraints:
     * n == nums.length
     * 1 <= n <= 5 * 104
     * -109 <= nums[i] <= 109
     * <p>
     * Follow-up: Could you solve the problem in linear time and in O(1) space?
     */

    public static void main(String[] args) {
        int[] nums = {2, 2, 1, 1, 1, 2, 2};
        int ans = majorityElement(nums);
        System.out.println(ans);
        ans = majorityElementUsingMooreVotingAlgorithm(nums);
        System.out.println(ans);
    }

    ////TC O(N) and SC is O(1);
    private static int majorityElementUsingMooreVotingAlgorithm(int[] nums) {
        /**
         * The algorithm works on the basis of the assumption that the majority element occurs more than n/2 times in the array.
         * This assumption guarantees that even if the count is reset to 0 by other elements, the majority element will eventually regain the lead.
         * */

        int count = 0;
        int requiredElement = 0;

        for (int i : nums) {
            if (count == 0) {
                requiredElement = i;
            }

            if (requiredElement == i) {
                count++;
            } else {
                count--;
            }
        }
        return requiredElement;
    }

    private static int majorityElement(int[] nums) {
        //TC and SC is O(N)
        int n = nums.length;
        int majorityLengthRequirement = n / 2;

        Map<Integer, Integer> map = new HashMap<>();
        for (int i : nums) {
            map.compute(i, (key, value) -> value != null ? value + 1 : 1);
            if (map.get(i) > majorityLengthRequirement) {
                return i;
            }
        }

        return -1;
    }
}

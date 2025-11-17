package DSA_Interview_Questions.HashMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class LongestConsecutiveSequence {
    /**
     * Given an unsorted array of integers nums, return the length of the longest consecutive elements sequence.
     * You must write an algorithm that runs in O(n) time.
     * <p>
     * Example 1:
     * Input: nums = [100,4,200,1,3,2]
     * Output: 4
     * Explanation: The longest consecutive elements sequence is [1, 2, 3, 4]. Therefore its length is 4.
     * <p>
     * Example 2:
     * Input: nums = [0,3,7,2,5,8,4,6,0,1]
     * Output: 9
     * <p>
     * Constraints:
     * 0 <= nums.length <= 105
     * -109 <= nums[i] <= 109
     */

    public static void main(String[] args) {
//        int[] nums = {100, 4, 200, 1, 3, 2};
        int[] nums = {0, 3, 7, 2, 5, 8, 4, 6, 0, 1};
        int ansLength = longestConsecutive(nums); //took 1066ms to run
        int ansLength2 = longestConsecutiveBySorting(nums); //took 10ms to run!!!
        System.out.println(ansLength);
        System.out.println(ansLength2);
        //TC is O(N) and SC is O(N)
    }

    //Approach :- 1. Add all elements into hashMap assuming every element is starting element of sequence
    //2. Find the valid starting Points... basically the ones which doesn't have previous consecutive element in the array
    //3. For every valid starting point, find the longest consecutive sequence
    private static int longestConsecutive(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        HashMap<Integer, Boolean> map = new HashMap<>();
        for (int i : nums) {
            map.put(i, true);
        }

        for (int i : nums) {
            //if a number has a previous consecutive element then this number is not a valid starting point
            if (map.containsKey(i - 1)) {
                map.put(i, false);
            }
        }

        int maxLength = 0;
        for (int i : nums) {
            if (Boolean.TRUE.equals(map.get(i))) {
                int currentLength = 1;
                int value = i + 1;
                while (map.containsKey(value)) {
                    currentLength++;
                    value++;
                }
                maxLength = Math.max(maxLength, currentLength);
            }
        }
        return maxLength;
    }

    public static int longestConsecutiveUsingSet(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        HashSet<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num);
        }

        int maxLength = 1;
        for (int num : nums) {
            if (!set.contains(num - 1)) {
                int currentLength = 1;
                int value = num + 1;

                while (set.contains(value)) {
                    currentLength++;
                    value++;
                }

                maxLength = Math.max(maxLength, currentLength);
            }
        }

        return maxLength;
    }

    public static int longestConsecutiveBySorting(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        Arrays.sort(nums);

        int maxLength = 1;
        int currentLength = 1;

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[i - 1]) {
                if (nums[i] == nums[i - 1] + 1) {
                    currentLength++;
                } else if (nums[i] != nums[i - 1]) {
                    currentLength = 1;
                }

                maxLength = Math.max(maxLength, currentLength);
            }
        }

        return maxLength;
    }
}

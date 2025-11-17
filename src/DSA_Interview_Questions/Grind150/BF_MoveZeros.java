package DSA_Interview_Questions.Grind150;

public class BF_MoveZeros {
    /**
     * Given an integer array nums, move all 0's to the end of it while maintaining the relative order of the non-zero elements.
     * Note that you must do this in-place without making a copy of the array.
     *
     * Example 1:
     * Input: nums = [0,1,0,3,12]
     * Output: [1,3,12,0,0]
     *
     *  Example 2:
     * Input: nums = [0]
     * Output: [0]
     *
     * Constraints:
     * 1 <= nums.length <= 104
     * -231 <= nums[i] <= 231 - 1
     * */

    public static void main(String[] args) {
        int[] nums = {0,1,0,3,12};
        moveZeroes(nums);
        for (int i : nums) {
            System.out.print(i + " ");
        }
    }

    private static void moveZeroes(int[] nums) {
        int i = 0;
        int length = nums.length;
        for (int n : nums) {
            if (n != 0) {
                nums[i] = n;
                i++;
            }
        }

        while (i < length){
            nums[i] = 0;
            i++;
        }
    }
}

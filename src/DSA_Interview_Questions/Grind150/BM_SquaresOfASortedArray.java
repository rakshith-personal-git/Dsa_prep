package DSA_Interview_Questions.Grind150;

import java.util.Arrays;

public class BM_SquaresOfASortedArray {
    /***
     * Given an integer array nums sorted in non-decreasing order, return an array of the squares of each number sorted in non-decreasing order.
     *
     * Example 1:
     * Input: nums = [-4,-1,0,3,10]
     * Output: [0,1,9,16,100]
     * Explanation: After squaring, the array becomes [16,1,0,9,100].
     * After sorting, it becomes [0,1,9,16,100].
     *
     * Example 2:
     * Input: nums = [-7,-3,2,3,11]
     * Output: [4,9,9,49,121]
     *
     * Constraints:
     * 1 <= nums.length <= 104
     * -104 <= nums[i] <= 104
     * nums is sorted in non-decreasing order.
     *
     * Follow up: Squaring each element and sorting the new array is very trivial, could you find an O(n) solution using a different approach?
     * */

    public static void main(String[] args) {
        int[] nums = {-4,-1,0,3,10};
        int[] nums2 = {-4,-1,0,3,10};
        int[] ans = sortedSquares(nums);
        for (int i : ans) {
            System.out.print(i + " ");
        }
        System.out.println();
        ans = sortedSquares2(nums2);
        for (int i : ans) {
            System.out.print(i + " ");
        }
    }

    private static int[] sortedSquares2(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        int leftPointer = 0;
        int rightPointer = n - 1;
        for (int i = n - 1; i >= 0; i--) {
            if (Math.abs(nums[leftPointer]) >= Math.abs(nums[rightPointer])) {
                result[i] = nums[leftPointer] * nums[leftPointer];
                leftPointer++;
            } else {
                result[i] = nums[rightPointer] * nums[rightPointer];
                rightPointer--;
            }
        }
        return result;
    }
    //TC is O(N)
    //SC is O(N)

    private static int[] sortedSquares(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            nums[i] = nums[i] * nums[i];
        }

        Arrays.sort(nums); // O(n log(n)
        return nums;
    }
}

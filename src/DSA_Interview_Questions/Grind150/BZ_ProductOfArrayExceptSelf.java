package DSA_Interview_Questions.Grind150;

import static DSA_Interview_Questions.Sorting.SortUsingMergeSortAlgo.printArray;

public class BZ_ProductOfArrayExceptSelf {
    /**
     * Given an integer array nums, return an array answer such that answer[i] is equal to the product of all the elements of nums except nums[i].
     * The product of any prefix or suffix of nums is guaranteed to fit in a 32-bit integer.
     * You must write an algorithm that runs in O(n) time and without using the division operation.
     * <p>
     * Example 1:
     * Input: nums = [1,2,3,4]
     * Output: [24,12,8,6]
     * <p>
     * Example 2:
     * Input: nums = [-1,1,0,-3,3]
     * Output: [0,0,9,0,0]
     * <p>
     * Constraints:
     * 2 <= nums.length <= 105
     * -30 <= nums[i] <= 30
     * The product of any prefix or suffix of nums is guaranteed to fit in a 32-bit integer.
     * <p>
     * Follow up: Can you solve the problem in O(1) extra space complexity? (The output array does not count as extra space for space complexity analysis.)
     */

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4};
        int[] ans = productExceptSelf(nums);
        printArray(ans);
    }

    /**
     * The approach involves calculating the product of all elements to the left and right of each element
     * in the array separately and then combining them to get the final result.
     */
    private static int[] productExceptSelf(int[] nums) {
        int n = nums.length;

        int[] leftProducts = new int[n];
        leftProducts[0] = 1;
        for (int i = 1; i < n; i++) {
            leftProducts[i] = leftProducts[i - 1] * nums[i - 1];
        }

        int[] rightProducts = new int[n];
        rightProducts[n - 1] = 1;
        for (int i = n - 2; i >= 0; i--) {
            rightProducts[i] = rightProducts[i + 1] * nums[i + 1];
        }


        for (int i = 0; i < n; i++) {
            nums[i] = leftProducts[i] * rightProducts[i];
        }

        return nums;
    }

    //Time Complexity: O(n) - Two passes through the input array.
    //Space Complexity: O(n) - Additional space is used for two arrays to store left and right products.
}

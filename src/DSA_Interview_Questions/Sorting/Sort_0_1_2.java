package DSA_Interview_Questions.Sorting;

import java.util.Arrays;

public class Sort_0_1_2 {

    /**
     *Given an array nums with n objects colored red, white, or blue, sort them in-place so that objects of the same color are adjacent, with the colors in the order red, white, and blue.
     * We will use the integers 0, 1, and 2 to represent the color red, white, and blue, respectively.
     * You must solve this problem without using the library's sort function.
     *
     * Example 1:
     * Input: nums = [2,0,2,1,1,0]
     * Output: [0,0,1,1,2,2]
     *
     *  Example 2:
     * Input: nums = [2,0,1]
     * Output: [0,1,2]
     *
     * Constraints:
     * n == nums.length
     * 1 <= n <= 300
     * nums[i] is either 0, 1, or 2.
     * */

    public static void main(String[] args) {
        //int[] arr = {2,0,2,1,1,0};
        int[] arr = {2,0,1};
        sort012(arr);
        Arrays.stream(arr).forEach(i -> {System.out.print(i);
            System.out.print(" ");});
    }

    private static void sort012(int[] arr) {
        int i = 0;
        int j = 0;
        int k = arr.length - 1;
        // assuming idx 0 to j-1 its 0s, j to i-1 its 1, i-1 to k its 2

        while (i <= k) {
            if (arr[i] == 0) {
                swapArr(arr, i , j);
                i++;
                j++;
            } else if (arr[i] == 1) {
                i++;
            } else {
                swapArr(arr, i, k);
                k--;
            }
        }
    }

    private static void swapArr(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

}

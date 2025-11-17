package DSA_Interview_Questions.Sorting;

import java.util.Arrays;

public class SortArrayUsingCountingSort {

    /**
     * Given an array of integers nums, sort the array in ascending order and return it.
     * You must solve the problem without using any built-in functions in O(nlog(n)) time complexity and with the smallest space complexity possible.
     * <p>
     * Example 1:
     * Input: nums = [5,2,3,1]
     * Output: [1,2,3,5]
     * Explanation: After sorting the array, the positions of some numbers are not changed (for example, 2 and 3), while the positions of other numbers are changed (for example, 1 and 5).
     * <p>
     * Example 2:
     * Input: nums = [5,1,1,2,0,0]
     * Output: [0,0,1,1,2,5]
     * Explanation: Note that the values of nums are not necessairly unique.
     * <p>
     * Constraints:
     * 1 <= nums.length <= 5 * 104
     * -5 * 104 <= nums[i] <= 5 * 104
     */

    public static void main(String[] args) {
        int[] arr = {5, 1, 1, 2, 0, 0};
        //int[] arr = {3,-1};
        int[] ans = countingSort(arr);
//        int[] ans = countingSortWholeNumbers(arr);
        Arrays.stream(ans).forEach(i -> {
            System.out.print(i);
            System.out.print(" ");
        });
    }

    private static int[] countingSort(int[] nums) {
        int n = nums.length;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            max = Math.max(max, nums[i]);
        }

        int[] p = new int[max + 1];

        //find the frequency for each element with positionIdx value in p as the element value of nums[]
        for (int i = 0; i < n; i++) {
            p[nums[i]] = p[nums[i]] + 1;
        }

        //convert the array to prefixSum array
        for (int i = 1; i < p.length; i++) {
            p[i] = p[i-1] + p[i];
        }

        //use the prefix array to populate the ans array
        // by traversing through it in reverse
        int[] ans = new int[n];
        for (int i = n-1; i >= 0; i--) {
            int positionValue = p[nums[i]];
            ans[positionValue - 1] = nums[i];
            p[nums[i]]--;
        }

        return ans;
    }

    //if i/p array has negative values
    private static int[] countingSortWholeNumbers(int[] nums) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        // Find the minimum and maximum values in the array
        for (int num : nums) {
            min = Math.min(min, num);
            max = Math.max(max, num);
        }

        // Calculate the range of values
        int range = max - min + 1;

        // Create an array to store the count of each value
        int[] count = new int[range];

        // Count the occurrences of each value
        for (int num : nums) {
            count[num - min]++;
        }

        // Update the count array to store the cumulative count
        for (int i = 1; i < range; i++) {
            count[i] += count[i - 1];
        }

        // Create the result array
        int[] result = new int[nums.length];

        // Build the result array by placing elements in their sorted order
        for (int i = nums.length - 1; i >= 0; i--) {
            result[count[nums[i] - min] - 1] = nums[i];
            count[nums[i] - min]--;
        }

        return result;
    }

    /**TC is O(N+R), SC is O(R), where R is the range of elements i.e max value in i/p array*/
}

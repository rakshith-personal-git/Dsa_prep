package DSA_Interview_Questions.DP;

public class K_ConcatenationMaximumSum {
    /**
     * Given an integer array arr and an integer k, modify the array by repeating it k times.
     * For example, if arr = [1, 2] and k = 3 then the modified array will be [1, 2, 1, 2, 1, 2].
     * Return the maximum sub-array sum in the modified array. Note that the length of the sub-array can be 0 and its sum in that case is 0.
     * As the answer can be very large, return the answer modulo 109 + 7.
     * <p>
     * <p>
     * <p>
     * Example 1:
     * Input: arr = [1,2], k = 3
     * Output: 9
     * <p>
     * Example 2:
     * Input: arr = [1,-2,1], k = 5
     * Output: 2
     * <p>
     * Example 3:
     * Input: arr = [-1,-2], k = 7
     * Output: 0
     * <p>
     * Constraints:
     * 1 <= arr.length <= 105
     * 1 <= k <= 105
     * -104 <= arr[i] <= 104
     */

    public static void main(String[] args) {
        int[] arr = {1,2};
        int k = 3;
//        int[] arr = {10000,10000,10000,10000,10000,10000,10000,10000,10000,10000};
//        int k = 100000;
        long ans = kConcatenationMaxSum(arr, k);
        System.out.println(ans);
    }

    /**
     * Approach:-
     * Case 1:- if k =1 -> apply Kadane's for 1 array
     * Case 2 :- if sum of all elements is positive then formula is
     * maxSum = sum * (k-2) + Kadane's of 2 array(1st and last subSection)
     * Case 3 :- if sum of all elements is negative then formula is
     * maxSum = Kadane's for 2 array
     */

    private static long kConcatenationMaxSum(int[] arr, int k) {
        if (k == 1) {
            return  Kadanes(arr, 1);
        } else {
            long arrSum = 0;
            for (int i : arr) {
                arrSum += i;
            }
            if (arrSum > 0) {
                return  (((k - 2) * arrSum) + Kadanes(arr, 2));
            } else {
                return  Kadanes(arr, 2);
            }
        }

    }

    private static long Kadanes(int[] arr, int c) {
        long currentSum = 0;
        long globalSum = Long.MIN_VALUE;
        for (int i = 0; i < arr.length * c; i++) {
            currentSum = Math.max(currentSum + arr[i % arr.length], arr[i % arr.length]);
            globalSum = Math.max(currentSum, globalSum);
        }
        return Math.max(0L, globalSum);
    }

    /**TC is O(N) , SC is O(1)*/
}

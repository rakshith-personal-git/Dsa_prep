package DSA_Interview_Questions.Arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SumThroughQuadruplets {

    /**
     * Given an array S of n integers, are there elements a, b, c, and d in S such that a + b + c + d = target?
     * Find all unique quadruplets in the array which gives the sum of target.
     * <p>
     * Note:
     * Elements in a quadruplet (a,b,c,d) must be in non-descending order. (ie, a ≤ b ≤ c ≤ d)
     * The solution set must not contain duplicate quadruplets.
     * Example : Given array S = {1 0 -1 0 -2 2}, and target = 0 A solution set is:
     * <p>
     * (-2, -1, 1, 2)
     * (-2,  0, 0, 2)
     * (-1,  0, 0, 1)
     * Also make sure that the solution set is lexicographically sorted.
     * Solution[i] < Solution[j] iff Solution[i][0] < Solution[j][0] OR (Solution[i][0] == Solution[j][0] AND ... Solution[i][k] < Solution[j][k])
     **/

    public static void main(String[] args) {
        int[] arr = {1, 0, -1, 0, -2, 2};
        int target = 0;
//        int[] arr = {1000000000, 1000000000, 1000000000, 1000000000};
//        int target = -294967296;
        List<List<Integer>> solution = fourSum(arr, target);
        System.out.println(solution);
    }

    private static List<List<Integer>> fourSum(int[] arr, int target) {
        Arrays.sort(arr); //sorting the array since ans must be in ascending order

        List<List<Integer>> solution = new ArrayList<>();
        int n = arr.length;

        for (int i = 0; i < n; i++) {
            if (i > 0 && arr[i] == arr[i - 1]) {
                continue;
            }

            for (int j = i + 1; j < n; j++) {
                if (j > i + 1 && arr[j] == arr[j - 1]) {
                    continue;
                }

                int left = j + 1;
                int right = n - 1;

                while (left < right) {
                    long currentSum = (long) arr[i] + arr[j] + arr[left] + arr[right];
                    if (currentSum == target) {
                        solution.add(new ArrayList<>(Arrays.asList(arr[i], arr[j], arr[left], arr[right])));

                        while (left < right && arr[left] == arr[left + 1]) {
                            left++;
                        }
                        while (right < left && arr[right] == arr[right + 1]) {
                            right--;
                        }

                        left++;
                        right--;
                    } else if (currentSum < target) {
                        left++;

                    } else {
                        right--;
                    }
                }

            }
        }
        return solution;
    }


}

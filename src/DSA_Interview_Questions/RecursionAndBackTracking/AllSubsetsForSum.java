package DSA_Interview_Questions.RecursionAndBackTracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllSubsetsForSum {

    /**
     * generate all subsets of an array and check if the sum of each subset is equal to the given target
     **/


    // TC is O(2^N) and SC is O(N)
    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5};
        int targetSum = 8;

        List<List<Integer>> result = subsetsWithTargetSum(nums, targetSum);

        System.out.println("Subsets with target sum " + targetSum + ":");
        for (List<Integer> subset : result) {
            System.out.println(subset);
        }
    }

    public static List<List<Integer>> subsetsWithTargetSum(int[] nums, int targetSum) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums); // Sort the array to handle duplicates

        generateSubsets(nums, 0, new ArrayList<>(), 0, targetSum, result);

        return result;
    }

    private static void generateSubsets(int[] nums, int index, List<Integer> currentSubset, int currentSum, int targetSum, List<List<Integer>> result) {
        if (currentSum == targetSum) {
            result.add(new ArrayList<>(currentSubset));
            return;
        }

        for (int i = index; i < nums.length; i++) {
            // Skip duplicates
            if (i > index && nums[i] == nums[i - 1]) {
                continue;
            }

            currentSubset.add(nums[i]);
            generateSubsets(nums, i + 1, currentSubset, currentSum + nums[i], targetSum, result);
            currentSubset.remove(currentSubset.size() - 1);
        }
    }

}

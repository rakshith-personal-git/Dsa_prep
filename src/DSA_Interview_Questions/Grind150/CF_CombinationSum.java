package DSA_Interview_Questions.Grind150;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CF_CombinationSum {
    /**
     * Given an array of distinct integers candidates and a target integer target,
     * return a list of all unique combinations of candidates where the chosen numbers sum to target. You may return the combinations in any order.
     * The same number may be chosen from candidates an unlimited number of times.
     * Two combinations are unique if the frequency of at least one of the chosen numbers is different.
     * <p>
     * The test cases are generated such that the number of unique combinations that sum up to target is less than 150 combinations for the given input.
     * <p>
     * Example 1:
     * Input: candidates = [2,3,6,7], target = 7
     * Output: [[2,2,3],[7]]
     * Explanation:
     * 2 and 3 are candidates, and 2 + 2 + 3 = 7. Note that 2 can be used multiple times.
     * 7 is a candidate, and 7 = 7.
     * These are the only two combinations.
     * <p>
     * Example 2:
     * Input: candidates = [2,3,5], target = 8
     * Output: [[2,2,2,2],[2,3,3],[3,5]]
     * <p>
     * Example 3:
     * Input: candidates = [2], target = 1
     * Output: []
     * <p>
     * <p>
     * Constraints:
     * 1 <= candidates.length <= 30
     * 2 <= candidates[i] <= 40
     * All elements of candidates are distinct.
     * 1 <= target <= 40
     */

    public static void main(String[] args) {
//        int[] candidates = {2, 3, 5};
//        int target = 8;
        int[] candidates = {2,3,6,7};
        int target = 7;
        List<List<Integer>> ans = combinationSum(candidates, target);
        for (List<Integer> list : ans) {
            for (int i : list) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }


    // go through this link inDetail to understand backTracking -> https://leetcode.com/problems/combination-sum/solutions/16502/a-general-approach-to-backtracking-questions-in-java-subsets-permutations-combination-sum-palindrome-partitioning/

    private static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates); //this can be skipped for this question, but if question had condition of no duplicates then this would have come in handy
        backTrack(result, new ArrayList<>(), candidates, target, 0);
        return result;
    }

    private static void backTrack(List<List<Integer>> result, List<Integer> tempList, int[] candidates, int remain, int start) {
        if (remain < 0) {
            return;
        } else if (remain == 0) {
            result.add(new ArrayList<>(tempList));
        } else {
            for (int i = start; i < candidates.length; i++ ) {
                tempList.add(candidates[i]);
                backTrack(result, tempList, candidates, remain - candidates[i], i); // not i + 1 because we can reuse same elements
                tempList.remove(tempList.size() - 1);
            }
        }
    }


}

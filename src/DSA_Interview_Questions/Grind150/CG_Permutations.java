package DSA_Interview_Questions.Grind150;

import java.util.ArrayList;
import java.util.List;

public class CG_Permutations {
    /**
     * Given an array nums of distinct integers, return all the possible permutations. You can return the answer in any order.
     * <p>
     * Example 1:
     * Input: nums = [1,2,3]
     * Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
     * <p>
     * Example 2:
     * Input: nums = [0,1]
     * Output: [[0,1],[1,0]]
     * <p>
     * Example 3:
     * Input: nums = [1]
     * Output: [[1]]
     * <p>
     * Constraints:
     * 1 <= nums.length <= 6
     * -10 <= nums[i] <= 10
     * All the integers of nums are unique.
     */


    // Main method for testing in local terminal
    public static void main(String[] args) {
        CG_Permutations solver = new CG_Permutations();
        int[] nums = {1, 2, 3};

        List<List<Integer>> output = solver.permute(nums);
        System.out.println("Total permutations generated: " + output.size()); // Expected: 6
        System.out.println("Permutations: " + output);
    }

    /**
     * TIME COMPLEXITY:  O(N * N!) - There are N! permutations. For each one,
     *                   we copy N elements into the result list.
     * SPACE COMPLEXITY: O(N) auxiliary space for the recursion call stack,
     *                   current list, and used boolean array.
     */
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length == 0) {
            return result;
        }

        // boolean array defaults to false
        boolean[] used = new boolean[nums.length];

        backtrack(nums, used, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, boolean[] used, List<Integer> current, List<List<Integer>> result) {
        // BASE CASE: Valid Permutation Found!
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current)); // Deep copy the snapshot
            return;
        }

        // Notice: Loop always starts at 0! (Not 'start')
        for (int i = 0; i < nums.length; i++) {
            // PRUNING: If element is already in our current permutation, skip it.
            if (used[i]) {
                continue;
            }

            // STEP 1: CHOOSE
            used[i] = true;
            current.add(nums[i]);

            // STEP 2: EXPLORE
            backtrack(nums, used, current, result);

            // STEP 3: UN-CHOOSE (BACKTRACK)
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }



}

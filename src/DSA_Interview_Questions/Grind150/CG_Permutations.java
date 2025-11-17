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

    public static void main(String[] args) {
        int[] nums = {1, 2, 3};
        List<List<Integer>> ans = permute(nums);
        for (List<Integer> list : ans) {
            for (int i : list) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    private static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backTrack(result, new ArrayList<>(), new boolean[nums.length], nums);
        return result;
    }

    private static void backTrack(List<List<Integer>> result, List<Integer> tempList, boolean[] visited, int[] nums) {
        if (tempList.size() == nums.length) {
            result.add(new ArrayList<>(tempList));
        } else {
            for (int i = 0; i < nums.length; i++) {
                if (visited[i]) {
                    continue; // element already exists, skip
                }
                visited[i] = true;
                tempList.add(nums[i]);
                backTrack(result, tempList, visited, nums);
                visited[i] = false;
                tempList.remove(tempList.size() - 1);
            }
        }
    }


}

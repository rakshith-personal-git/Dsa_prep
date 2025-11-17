package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class CS_BinaryTreeRightSideView {
    /**
     * Given the root of a binary tree, imagine yourself standing on the right side of it,
     * return the values of the nodes you can see ordered from top to bottom.
     *
     * Example 1:
     * Input: root = [1,2,3,null,5,null,4]
     * Output: [1,3,4]
     *
     * Example 2:
     * Input: root = [1,null,3]
     * Output: [1,3]
     *
     * Example 3:
     * Input: root = []
     * Output: []
     *
     * Constraints:
     * The number of nodes in the tree is in the range [0, 100].
     * -100 <= Node.val <= 100
     * */

    public static void main(String[] args) {
//        Integer[] input = {1,2,3,null,5,null,4};
        Integer[] input = {1,2,3,4};
        TreeNode root = TreeNode.createBinaryTree(input, 0);
        TreeNode.printTreeVisually(root);
        List<Integer> ans = rightSideView(root);
        ans.forEach(i -> System.out.println(i + " "));
    }

    private static List<Integer> rightSideView(TreeNode root) {
        if (root == null) {
            return new ArrayList<>();
        }
        List<Integer> result = new ArrayList<>();
        backTrack(root, 0, result);
        return result;
    }

    private static void backTrack(TreeNode root, int level, List<Integer> result) {
        if (root == null) {
            return;
        }
        if (result.size() == level) {
            result.add(root.val); // imp -> every first node in that level is part of right side
        }
        backTrack(root.right, level + 1, result);
        backTrack(root.left, level + 1, result);
    }
}

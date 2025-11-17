package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

public class AV_MaximumDepthOfBinaryTree {
    /**
     * Given the root of a binary tree, return its maximum depth.
     * A binary tree's maximum depth is the number of nodes along the longest path from the root node down to the farthest leaf node.
     * <p>
     * Example 1:
     * Input: root = [3,9,20,null,null,15,7]
     * Output: 3
     * <p>
     * Example 2:
     * Input: root = [1,null,2]
     * Output: 2
     * <p>
     * Constraints:
     * The number of nodes in the tree is in the range [0, 104].
     * -100 <= Node.val <= 100
     */

    public static void main(String[] args) {
        Integer[] input = {3, 9, 20, null, null, 15, 7};
        TreeNode root = TreeNode.createBinaryTree(input, 0);
        TreeNode.printTreeVisually(root);
        int ans = maxDepth(root);
        System.out.println("ans " + ans);
    }

    //logic -> height is the maximum height of the left and right subtrees plus 1 (to account for the current node).
    // i.e max(left, right) + 1;
    private static int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int left = maxDepth(root.left);
        int right = maxDepth(root.right);

        return Math.max(left, right) + 1;
    }
}

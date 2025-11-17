package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

public class AK_BalancedBinaryTree {
    /**
     * Given a binary tree, determine if it is height-balanced
     * i.e A height-balanced binary tree is a binary tree in which the depth of the two subtrees of every node never differs by more than one.
     * <p>
     * Example 1:
     * Input: root = [3,9,20,null,null,15,7]
     * Output: true
     * <p>
     * Example 2:
     * Input: root = [1,2,2,3,3,null,null,4,4]
     * Output: false
     * <p>
     * Example 3:
     * Input: root = []
     * Output: true
     * <p>
     * Constraints:
     * The number of nodes in the tree is in the range [0, 5000].
     * -104 <= Node.val <= 104
     */

    public static void main(String[] args) {
        Integer[] input = {3, 9, 20, null, null, 15, 7};
//        Integer[] input = {1,2,2,3,3,null,null,4,4};
        TreeNode root = TreeNode.createBinaryTree(input, 0);
        TreeNode.printTreeVisually(root);
        boolean ans = isBalanced(root);
        System.out.println(ans);
    }

    private static boolean isBalanced(TreeNode root) {
        if (root == null) {
            return true;
        }

        return height(root) != -1;

    }

    private static int height(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int left = height(root.left);
        int right = height(root.right);
        int diff = Math.abs(left - right);

        if (left == -1 || right == -1 || diff > 1) {
            return -1;
        }

        return 1 + Math.max(left, right);
    }
}

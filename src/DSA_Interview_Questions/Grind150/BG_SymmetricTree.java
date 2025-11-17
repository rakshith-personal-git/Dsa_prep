package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

public class BG_SymmetricTree {
    /**
     * Given the root of a binary tree, check whether it is a mirror of itself (i.e., symmetric around its center).
     *
     * Example 1:
     * Input: root = [1,2,2,3,4,4,3]
     * Output: true
     *
     *  Example 2:
     * Input: root = [1,2,2,null,3,null,3]
     * Output: false
     *
     * Constraints:
     * The number of nodes in the tree is in the range [1, 1000].
     * -100 <= Node.val <= 100
     *
     * Follow up: Could you solve it both recursively and iteratively?
     * */
    public static void main(String[] args) {
        Integer[] input = {1,2,2,3,4,4,3};
//        Integer[] input = {1,2,2,null,3,null,3};
        TreeNode root = TreeNode.createBinaryTree(input, 0);
        boolean ans = isSymmetric(root);
        System.out.println(ans);
    }

    private static boolean isSymmetric(TreeNode root) {
        if (root == null) {
            return true;
        }

        return isItSymmetric(root.left, root.right);
    }

    private static boolean isItSymmetric(TreeNode node1, TreeNode node2) {
        if (node1 == null && node2 == null) {
            return true;
        }

        if (node1 == null || node2 == null) {
            return false;
        }

        if (node1.val != node2.val) {
            return false;
        }

        return isItSymmetric(node1.left, node2.right) && isItSymmetric(node1.right, node2.left);

    }

}

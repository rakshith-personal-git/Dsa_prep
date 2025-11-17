package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

public class BB_SameTree {
    /**
     * Given the roots of two binary trees p and q, write a function to check if they are the same or not.
     * Two binary trees are considered the same if they are structurally identical, and the nodes have the same value.
     *
     * Example 1:
     * Input: p = [1,2,3], q = [1,2,3]
     * Output: true
     *
     *  Example 2:
     * Input: p = [1,2], q = [1,null,2]
     * Output: false
     *
     *  Example 3:
     * Input: p = [1,2,1], q = [1,1,2]
     * Output: false
     *
     * Constraints:
     * The number of nodes in both trees is in the range [0, 100].
     * -104 <= Node.val <= 104
     * */

    public static void main(String[] args) {
        Integer[] input1 = {1,2,3};
        Integer[] input2 = {1,2,3};
        TreeNode root1 = TreeNode.createBinaryTree(input1, 0);
        TreeNode root2 = TreeNode.createBinaryTree(input2, 0);
        boolean ans = isSameTree(root1, root2);
        System.out.println(ans);
    }

    private static boolean isSameTree(TreeNode root1, TreeNode root2) {
        if (root1 == null && root2 == null) {
            return true; // Both trees are empty, hence they are same
        } else if (root1 == null || root2 == null) {
            return false; // One tree is empty, but the other is not, hence they are not same
        } else if (root1.val != root2.val) {
            return false; // Values of current nodes are different, hence they are not same
        } else {
            // Recursively check if left subtrees and right subtrees are same
            return isSameTree(root1.left, root2.left) && isSameTree(root1.right, root2.right);
        }
    }
}

package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

public class AT_DiameterOfBinaryTree {
    /**
     * Given the root of a binary tree, return the length of the diameter of the tree.
     * The diameter of a binary tree is the length of the longest path between any two nodes in a tree. This path may or may not pass through the root.
     * The length of a path between two nodes is represented by the number of edges between them.
     * <p>
     * Example 1:
     * Input: root = [1,2,3,4,5]
     * Output: 3
     * Explanation: 3 is the length of the path [4,2,1,3] or [5,2,1,3].
     * <p>
     * Example 2:
     * Input: root = [1,2]
     * Output: 1
     * <p>
     * Constraints:
     * The number of nodes in the tree is in the range [1, 104].
     * -100 <= Node.val <= 100
     */

    public static void main(String[] args) {
        Integer[] input = {1, 2, 3, 4, 5};
        TreeNode root = TreeNode.createBinaryTree(input, 0);
        TreeNode.printTreeVisually(root);
        int ans = diameterOfBinaryTree(root);
        System.out.println("ans " + ans);
    }

    static int diameter = 0;

    //logic -> diameter is the maximum value between itself and the sum of the heights of the left and right subtrees. i.e diameter = max(diameter, left + right);
    //  and the height is the maximum height of the left and right subtrees plus 1 (to account for the current node).
    // i.e max(left, right) + 1;
    private static int diameterOfBinaryTree(TreeNode root) {
        findDiameter(root);
        return diameter;
    }

    private static int findDiameter(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int left = findDiameter(root.left);
        int right = findDiameter(root.right);
        diameter = Math.max(diameter, left + right);

        return Math.max(left, right) + 1;
    }
}

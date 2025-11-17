package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

import static DSA_Interview_Questions.Trees.TreeNode.createBinaryTree;
import static DSA_Interview_Questions.Trees.TreeNode.printTreeVisually;

public class CI_LowestCommonAncestorOfBinaryTree {
    /**
     * Given a binary tree, find the lowest common ancestor (LCA) of two given nodes in the tree.
     * According to the definition of LCA on Wikipedia:
     * “The lowest common ancestor is defined between two nodes p and q as the lowest node in T that has both p and q as descendants (where we allow a node to be a descendant of itself).”
     * <p>
     * Example 1:
     * Input: root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1
     * Output: 3
     * Explanation: The LCA of nodes 5 and 1 is 3.
     * <p>
     * Example 2:
     * Input: root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 4
     * Output: 5
     * Explanation: The LCA of nodes 5 and 4 is 5, since a node can be a descendant of itself according to the LCA definition.
     * <p>
     * Example 3:
     * Input: root = [1,2], p = 1, q = 2
     * Output: 1
     * <p>
     * Constraints:
     * The number of nodes in the tree is in the range [2, 105].
     * -109 <= Node.val <= 109
     * All Node.val are unique.
     * p != q
     * p and q will exist in the tree.
     */

    public static void main(String[] args) {
        Integer[] input = {3, 5, 1, 6, 2, 0, 8, null, null, 7, 4};

        // Create the binary tree and return the root
        TreeNode root = createBinaryTree(input, 0);
        TreeNode p = new TreeNode(5);
        TreeNode q = new TreeNode(1);

        System.out.println("Tree :- ");
        printTreeVisually(root);

        TreeNode answer = lowestCommonAncestor(root, p, q);
        System.out.println();
        System.out.println("LCA root " + answer.val);


    }

    private static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root.val == p.val || root.val == q.val) {
            return root;
        }

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        if (left == null) {
            return right;
        } else if (right == null) {
            return left;
        } else {
            return root; //i.e left && right is != null
        }
    }
    //this solution will always work because of constraint -> p and q will exist in the tree.
}

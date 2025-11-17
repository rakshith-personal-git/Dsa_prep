package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

import static DSA_Interview_Questions.Trees.TreeNode.createBinaryTree;
import static DSA_Interview_Questions.Trees.TreeNode.printTreeVisually;

public class AJ_LowestCommonAncestorOfBinarySearchTree {
    /**
     * Given a binary search tree (BST), find the lowest common ancestor (LCA) node of two given nodes in the BST.
     * According to the definition of LCA on Wikipedia:
     * “The lowest common ancestor is defined between two nodes p and q as the lowest node in T that has
     * both p and q as descendants (where we allow a node to be a descendant of itself).”
     * <p>
     * Example 1:
     * Input: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 8
     * Output: 6
     * Explanation: The LCA of nodes 2 and 8 is 6.
     * <p>
     * Example 2:
     * Input: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 4
     * Output: 2
     * Explanation: The LCA of nodes 2 and 4 is 2, since a node can be a descendant of itself according to the LCA definition.
     * <p>
     * Example 3:
     * Input: root = [2,1], p = 2, q = 1
     * Output: 2
     * <p>
     * Constraints:
     * The number of nodes in the tree is in the range [2, 105].
     * -109 <= Node.val <= 109
     * All Node.val are unique.
     * p != q
     * p and q will exist in the BST.
     */

    public static void main(String[] args) {
        Integer[] input = {6, 2, 8, 0, 4, 7, 9, null, null, 3, 5};

        // Create the binary tree and return the root
        TreeNode root = createBinaryTree(input, 0);
        System.out.println("Tree :- ");
        printTreeVisually(root);
        TreeNode p = new TreeNode(2);
        TreeNode q = new TreeNode(4);
        TreeNode answer = lowestCommonAncestor(root, p, q);
        System.out.println();
        System.out.println("LCA root " + answer.val);


    }

    private static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {
            return null;
        }

        if (root.val > p.val && root.val > q.val) {
            //since it's a binary search tree the left side will be smaller compared to right side for each node.
            return lowestCommonAncestor(root.left, p, q);
        } else if (root.val < p.val && root.val < q.val) {
            return lowestCommonAncestor(root.right, p, q);
        } else {
            return root;
        }
    }
    //Time Complexity (TC): O(h), where h is the height of the tree.
    //For a balanced BST, this is O(log n); for a skewed BST, this is O(n).

    //Space Complexity (SC): O(h), where h is the height of the tree.
    //For a balanced BST, this is O(log n); for a skewed BST, this is O(n).
}

package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class CB_ValidateBinarySearchTree {
    /**
     * Given the root of a binary tree, determine if it is a valid binary search tree (BST).
     * A valid BST is defined as follows:
     * <p>
     * The left subtree of a node contains only nodes with keys less than the node's key.
     * The right subtree of a node contains only nodes with keys greater than the node's key.
     * Both the left and right subtrees must also be binary search trees.
     * <p>
     * Example 1:
     * Input: root = [2,1,3]
     * Output: true
     * <p>
     * Example 2:
     * Input: root = [5,1,4,null,null,3,6]
     * Output: false
     * Explanation: The root node's value is 5 but its right child's value is 4.
     * <p>
     * Constraints:
     * The number of nodes in the tree is in the range [1, 104].
     * -231 <= Node.val <= 231 - 1
     */

    public static void main(String[] args) {
        //Integer[] input = {5,1,4,null,null,3,6};
        Integer[] input = {2, 1, 3};
        TreeNode root = TreeNode.createBinaryTree(input, 0);
        System.out.println(isValidBST(root));
        System.out.println(isValidBSTOptimised(root));
    }

    private static long previousNodeValue = Long.MIN_VALUE;

    private static boolean isValidBSTOptimised(TreeNode root) {
        //using logic of In-order traversal more efficiently
        if (root == null) {
            return true;
        }

        if (!isValidBSTOptimised(root.left)) {
            return false;
        }

        if (previousNodeValue >= root.val) {
            return false;
        }

        previousNodeValue = root.val;

        if (!isValidBSTOptimised(root.right)) {
            return false;
        }

        return true;

    }

    private static boolean isValidBST(TreeNode root) {
        //since In-order traversal is LNR, that shd result in a array in ascending order if valid tree
        List<Integer> inOrderTraversalResult = new ArrayList<>();
        inOrderTraversal(root, inOrderTraversalResult);
        for (int i = 0; i < inOrderTraversalResult.size() - 1; i++) {
            if (inOrderTraversalResult.get(i) >= inOrderTraversalResult.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

    private static void inOrderTraversal(TreeNode root, List<Integer> inOrderTraversalResult) {
        if (root != null) {
            inOrderTraversal(root.left, inOrderTraversalResult);
            inOrderTraversalResult.add(root.val);
            inOrderTraversal(root.right, inOrderTraversalResult);
        }
    }
}

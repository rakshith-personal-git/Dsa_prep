package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

import static DSA_Interview_Questions.Trees.TreeNode.createBinaryTree;
import static DSA_Interview_Questions.Trees.TreeNode.printTreeLevelOrder;

public class AF_InvertBinaryTree {
    /**
     * Given the root of a binary tree, invert the tree, and return its root.
     *
     * Example 1:
     *
     * Input: root = [4,2,7,1,3,6,9]
     * Output: [4,7,2,9,6,3,1]
     *
     * Example 2:
     * Input: root = [2,1,3]
     * Output: [2,3,1]
     *
     * Example 3:
     * Input: root = []
     * Output: []
     *
     *  Constraints:
     * The number of nodes in the tree is in the range [0, 100].
     * -100 <= Node.val <= 100
     * **/

    public static void main(String[] args) {
        Integer[] input = {4,2,7,1,3,6,9};

        // Creates the binary tree and return the root
        TreeNode root = createBinaryTree(input, 0);
        System.out.println("Before inverting ");
        printTreeLevelOrder(root);

        root = invertTree(root);

        System.out.println("After inverting ");
        printTreeLevelOrder(root);


    }

    private static TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }

        //swap left and right nodes recursively
        TreeNode temp = root.left;
        root.left = invertTree(root.right);
        root.right = invertTree(temp);
        return root;
    }

    //Time Complexity (TC): O(n)
    //Space Complexity (SC):
    //Worst Case: O(n) (for a skewed tree)
    //Best Case: O(logn) (for a balanced tree).
}

package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DF_KthSmallestElementInBST {
    /**
     * Given the root of a binary search tree, and an integer k, return the kth smallest value (1-indexed) of all the values of the nodes in the tree.
     * <p>
     * Example 1:
     * Input: root = [3,1,4,null,2], k = 1
     * Output: 1
     * <p>
     * Example 2:
     * Input: root = [5,3,6,2,4,null,null,1], k = 3
     * Output: 3
     * <p>
     * Constraints:
     * The number of nodes in the tree is n.
     * 1 <= k <= n <= 104
     * 0 <= Node.val <= 104
     * <p>
     * Follow up: If the BST is modified often (i.e., we can do insert and delete operations) and you need to find the kth smallest frequently, how would you optimize?
     */

    public static void main(String[] args) {
        Integer[] input = {5, 3, 6, 2, 4, null, null, 1};
        TreeNode root = TreeNode.createBinaryTree(input, 0);
        int k = 3;
        TreeNode.printTreeVisually(root);
        System.out.println(kthSmallest(root, k));
        System.out.println(kthSmallestOptimised(root, k));
    }

    private static int kthSmallest(TreeNode root, int k) {
        if (root == null) {
            return -1;
        }
        List<Integer> result = new ArrayList<>();
        inOrderTraversal(root, result);
        return result.get(k - 1);
    }

    //because inOrderTraversal for a binarySearchTree is basically elements in ascending order
    private static void inOrderTraversal(TreeNode root, List<Integer> result) {
        if (root != null) {
            inOrderTraversal(root.left, result);
            result.add(root.val);
            inOrderTraversal(root.right, result);
        }
    }


    //this takes 0ms and above method takes 1ms
    private static int kthSmallestOptimised(TreeNode root, int k) {
        Stack<TreeNode> stack = new Stack<>();
        while (true) {
            while (root != null) {
                //1st going through all left nodes and check if its size is >= k;
                stack.push(root);
                root = root.left;
            }


            root = stack.pop();

            k--;
            if (k == 0) {
                return root.val;
            }

            root = root.right;
        }
    }

}

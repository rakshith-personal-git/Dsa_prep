package DSA_Interview_Questions.Trees;

import java.util.LinkedList;
import java.util.Queue;

import static DSA_Interview_Questions.Trees.TreeNode.printTreeLevelOrder;


public class BinaryTreeFromInAndPost_OrderTraversal {
    /**
     * Given two integer arrays inorder and postorder where inorder is the inorder traversal of a binary tree
     * and postorder is the postorder traversal of the same tree,
     * construct and return the binary tree.
     * <p>
     * Example 1:
     * Input: inorder = [9,3,15,20,7], postorder = [9,15,7,20,3]
     * Output: [3,9,20,null,null,15,7]
     * <p>
     * Example 2:
     * Input: inorder = [-1], postorder = [-1]
     * Output: [-1]
     */

    public static void main(String[] args) {
        int[] inorder = {9, 3, 15, 20, 7};
        int[] postorder = {9, 15, 7, 20, 3};


//        int[] preorder = {1, 7, 2, 6, 5, 11, 9, 10, 4};
//        int[] inorder = {2, 7, 5, 6, 11, 1, 9, 4, 10};

        TreeNode root = buildTree(inorder, postorder);
        printTreeLevelOrder(root);
    }


    /**
     * Intuition
     * To construct a binary tree from inorder and postorder traversal arrays, we first need to understand what each of these traversals represents.
     * Inorder traversal visits the nodes in ascending order of their values, i.e., left child, parent, and right child. On the other hand, postorder traversal visits the nodes in the order left child, right child, and parent.
     * <p>
     * Knowing this, we can say that the last element in the postorder array is the root node,
     * and its index in the inorder array divides the tree into left and right subtrees.
     * We can recursively apply this logic to construct the entire binary tree.
     * <p>
     * Approach
     * Start with the last element of the postorder array as the root node.
     * Find the index of the root node in the inorder array.
     * Divide the inorder array into left and right subtrees based on the index of the root node.
     * Divide the postorder array into left and right subtrees based on the number of elements
     * in the left and right subtrees of the inorder array.
     * Recursively construct the left and right subtrees.
     * <p>
     * Complexity
     * Time complexity: The time complexity of this algorithm is O(n), where n is the number of nodes in the tree. We visit each node only once.
     * Space complexity: The space complexity of this algorithm is O(n). We create a hashmap to store the indices of the inorder traversal, which takes O(n) space. Additionally, the recursive call stack can go up to O(n) in the worst case if the binary tree is skewed.
     */
    private static TreeNode buildTree(int[] inorder, int[] postorder) {
        // Call the recursive function with full arrays and return the result
        return buildTree(inorder, 0, inorder.length - 1, postorder, 0, postorder.length - 1);
    }

    private static TreeNode buildTree(int[] inorder, int inStart, int inEnd, int[] postorder, int postStart, int postEnd) {
        // Base case
        if (inStart > inEnd || postStart > postEnd) {
            return null;
        }

        // Find the root node from the last element of postorder traversal
        int rootVal = postorder[postEnd];
        TreeNode root = new TreeNode(rootVal);

        // Find the index of the root node in inorder traversal
        int rootIndex = 0;
        for (int i = inStart; i <= inEnd; i++) {
            if (inorder[i] == rootVal) {
                rootIndex = i;
                break;
            }
        }

        // Recursively build the left and right subtrees
        int leftSize = rootIndex - inStart;
        int rightSize = inEnd - rootIndex;
        root.left = buildTree(inorder, inStart, rootIndex - 1, postorder, postStart, postStart + leftSize - 1);
        root.right = buildTree(inorder, rootIndex + 1, inEnd, postorder, postEnd - rightSize, postEnd - 1);

        return root;
    }



}

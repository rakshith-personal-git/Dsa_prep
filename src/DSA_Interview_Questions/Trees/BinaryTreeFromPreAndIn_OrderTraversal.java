package DSA_Interview_Questions.Trees;

import java.util.LinkedList;
import java.util.Queue;

public class BinaryTreeFromPreAndIn_OrderTraversal {
    /**
     * Given two integer arrays preorder and inorder where preorder is the preorder traversal of a binary tree and
     * inorder is the inorder traversal of the same tree, construct and return the binary tree.
     * <p>
     * Example 1:
     * Input: preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
     * Output: [3,9,20,null,null,15,7]
     * <p>
     * Example 2:
     * Input: preorder = [-1], inorder = [-1]
     * Output: [-1]
     * <p>
     * Constraints:
     * <p>
     * 1 <= preorder.length <= 3000
     * inorder.length == preorder.length
     * -3000 <= preorder[i], inorder[i] <= 3000
     * preorder and inorder consist of unique values.
     * Each value of inorder also appears in preorder.
     * preorder is guaranteed to be the preorder traversal of the tree.
     * inorder is guaranteed to be the inorder traversal of the tree.
     */

    public static void main(String[] args) {
        int[] preorder = {3, 9, 20, 15, 7};
        int[] inorder = {9, 3, 15, 20, 7};

//        int[] preorder = {1, 7, 2, 6, 5, 11, 9, 10, 4};
//        int[] inorder = {2, 7, 5, 6, 11, 1, 9, 4, 10};

        TreeNode root = buildTree(preorder, inorder);
        //printTreeInOrder(root);
        printTreeLevelOrder(root);
    }

    //logic is that the 1st element in preOrder traversal is the root, and find that element in the InOrder traversal
    //after that left side from that element in the Inorder traversal belongs to the left section of the tree and right side from that elements belongs to right side of the tree
    private static TreeNode buildTree(int[] preorder, int[] inorder) {
        return constructTree(preorder, 0, preorder.length, inorder, 0, inorder.length);
    }

    private static TreeNode constructTree(int[] preorder, int preStartIdx, int preEndIdx, int[] inorder, int inStartIdx, int inEndIdx) {
        if (preStartIdx >= preEndIdx) {
            return null;
        }

        if (preStartIdx == preEndIdx) {
            return new TreeNode(preorder[preStartIdx]);
        }

        TreeNode root = new TreeNode(preorder[preStartIdx]);
        int index = -1;
        for (int i = inStartIdx; i < inEndIdx; i++) {
            if (inorder[i] == preorder[preStartIdx]) {
                index = i;
                break;
            }
        }
        root.left = constructTree(preorder, preStartIdx + 1, preStartIdx + index - inStartIdx + 1, inorder, inStartIdx, index );
        root.right = constructTree(preorder, preStartIdx + index - inStartIdx + 1, preEndIdx, inorder, index + 1, inEndIdx);
        return root;
    }

    //TC is O(N^2) and SC is O(N)

    private static void printTreeInOrder(TreeNode node) {
        if (node != null) {
            printTreeInOrder(node.left);
            System.out.println(node.val);
            printTreeInOrder(node.right);
        }
    }

    private static void printTreeLevelOrder(TreeNode root) {
        if (root == null) {
            return;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                TreeNode current = queue.poll();
                System.out.print(current.val + " ");
                if (current.left != null) {
                    queue.offer(current.left);
                }
                if (current.right != null) {
                    queue.offer(current.right);
                }
            }
            System.out.println();  // Move to the next line after printing each level
        }
    }

}

package DSA_Interview_Questions.Trees;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static DSA_Interview_Questions.Trees.TreeNode.createBinaryTree;

public class BinaryTreeLevelOrderTraversal {
    /**
     * Given the root of a binary tree, return the level order traversal of its nodes' values. (i.e., from left to right, level by level).
     * Example 1:
     * Input: root = [3,9,20,null,null,15,7]
     * Output: [[3],[9,20],[15,7]]
     **/

    public static void main(String[] args) {
        Integer[] input = {3, 9, 20, null, null, 15, 7};

        // Create the binary tree and return the root
        TreeNode root = createBinaryTree(input, 0);
        List<List<Integer>> ans = levelOrder(root);

        // Print the elements of the ans
        for (List<Integer> innerList : ans) {
            for (Integer value : innerList) {
                System.out.print(value + " ");
            }
            System.out.println(); // Move to the next line after printing each inner list
        }
    }

    //Approach is by using a queue since its FIFO, i.e add a element to queue and before removing an element add all its child to queue and then remove it.
    public static List<List<Integer>> levelOrder(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>(); //using linked list since I'm mostly doing add and remove which is more efficient in linkedList compared to ArrayList
        List<List<Integer>> ans = new ArrayList<>();
        if (root == null) {
            return ans;
        }
        queue.add(root);

        while (!queue.isEmpty()) {
            int removables = queue.size();
            List<Integer> row = new ArrayList<>();

            for (int i = 0; i < removables; i++) {
                TreeNode node = queue.remove();
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
                row.add(node.val);
            }

            ans.add(row);
        }
        return ans;
    }

    //TC is O(N) and SC is O(N/2)
}

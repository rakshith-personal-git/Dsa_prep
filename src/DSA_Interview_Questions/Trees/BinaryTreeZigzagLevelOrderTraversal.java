package DSA_Interview_Questions.Trees;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static DSA_Interview_Questions.Trees.TreeNode.createBinaryTree;

public class BinaryTreeZigzagLevelOrderTraversal {
    /**
     * Given the root of a binary tree, return the zigzag level order traversal of its nodes' values.
     * (i.e., from left to right, then right to left for the next level and alternate between).
     * <p>
     * Example 1:
     * Input: root = [3,9,20,null,null,15,7]
     * Output: [[3],[20,9],[15,7]]
     * <p>
     * Example 2:
     * Input: root = [1]
     * Output: [[1]]
     * <p>
     * Example 3:
     * Input: root = []
     * Output: []
     * <p>
     * Constraints:
     * The number of nodes in the tree is in the range [0, 2000].
     * -100 <= Node.val <= 100
     */

    public static void main(String[] args) {
        Integer[] input = {3, 9, 20, null, null, 15, 7};
//        Integer[] input = {1};

        // Create the binary tree and return the root
        TreeNode root = createBinaryTree(input, 0);
        List<List<Integer>> ans = zigzagLevelOrder(root);

        // Print the elements of the ans
        for (List<Integer> innerList : ans) {
            for (Integer value : innerList) {
                System.out.print(value + " ");
            }
            System.out.println(); // Move to the next line after printing each inner list
        }
    }

    //Approach is by using a stack since its FILO,
    //basically have 2 stacks and for removing a node at one stack, add its child to the other stack
    //while adding if it's an even node then add left child 1st and if it's an odd node then add right child 1st.
    private static List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        Stack<TreeNode> current = new Stack<>();
        Stack<TreeNode> next = new Stack<>();
        List<List<Integer>> ans = new ArrayList<>();
        if (root == null) {
            return ans;
        }
        current.push(root);
        int level = 0;

        while (!current.isEmpty()) {
            List<Integer> row = new ArrayList<>();
            while (!current.isEmpty()) {
                TreeNode removedNode = current.pop();
                row.add(removedNode.val);
                if (level % 2 == 0) { //even node
                    if (removedNode.left != null) {
                        next.add(removedNode.left);
                    }
                    if (removedNode.right != null) {
                        next.add(removedNode.right);
                    }
                } else { //odd node
                    if (removedNode.right != null) {
                        next.add(removedNode.right);
                    }
                    if (removedNode.left != null) {
                        next.add(removedNode.left);
                    }
                }
            }
            ans.add(row);
            level++;
            current = next;
            next = new Stack<>();
        }
        return ans;
    }
}

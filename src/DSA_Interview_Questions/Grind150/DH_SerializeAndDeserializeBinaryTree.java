package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class DH_SerializeAndDeserializeBinaryTree {
    /**
     * Serialization is the process of converting a data structure or object into a sequence of bits so that it can be stored in a file or memory buffer,
     * or transmitted across a network connection link to be reconstructed later in the same or another computer environment.
     * <p>
     * Design an algorithm to serialize and deserialize a binary tree. There is no restriction on how your serialization/deserialization algorithm should work.
     * You just need to ensure that a binary tree can be serialized to a string and this string can be deserialized to the original tree structure.
     * <p>
     * Clarification: The input/output format is the same as how LeetCode serializes a binary tree.
     * You do not necessarily need to follow this format, so please be creative and come up with different approaches yourself.
     * <p>
     * Example 1:
     * Input: root = [1,2,3,null,null,4,5]
     * Output: [1,2,3,null,null,4,5]
     * <p>
     * Example 2:
     * Input: root = []
     * Output: []
     * <p>
     * Constraints:
     * The number of nodes in the tree is in the range [0, 104].
     * -1000 <= Node.val <= 1000
     */

    public static void main(String[] args) {
        Integer[] input = {1, 2, 3, null, null, 4, 5};
        TreeNode root = TreeNode.createBinaryTree(input, 0);
        String serialisedData = Codec.serialize(root);
        System.out.println(serialisedData);
        TreeNode.printTreeVisually(Codec.deserialize(serialisedData));
    }

    public class Codec {

        // Encodes a tree to a single string.
        public static String serialize(TreeNode root) {
            if (root == null) {
                return "%";
            }
            return root.val + "," + serialize(root.left) + "," + serialize(root.right);
        }

        // Decodes your encoded data to tree.
        public static TreeNode deserialize(String data) {
            Queue<String> queue = new LinkedList<>(Arrays.asList(data.split(",")));
            return createBinaryTree(queue);
        }
    }

    private static TreeNode createBinaryTree(Queue<String> queue) {
        String s = queue.poll();
        if (s.equals("%")) {
            return null;
        }
        TreeNode node = new TreeNode(Integer.parseInt(s));
        node.left = createBinaryTree(queue);
        node.right = createBinaryTree(queue);
        return node;
    }
}

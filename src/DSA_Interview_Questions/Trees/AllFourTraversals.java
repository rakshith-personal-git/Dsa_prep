package DSA_Interview_Questions.Trees;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AllFourTraversals {
    /**
     * generate and print all 4 kind of traversals in a tree
     */

    public static void main(String[] args) {
        Integer[] input = {1, 2, 3, 4, 5, 6, 7, 8, 9};
//        Integer[] input = {1,2,3,null,null,4,5};
        TreeNode root = TreeNode.createBinaryTree(input, 0);
        TreeNode.printTreeVisually(root);

        //Pre-order traversal (NLR):
        //Visit the current node.
        //Traverse the left subtree recursively.
        //Traverse the right subtree recursively.
        System.out.println("Pre-order traversal (NLR): ");
        List<Integer> preOrderResult = preOrder(root);
        System.out.println(preOrderResult);


        //In-order traversal (LNR):
        //Traverse the left subtree recursively.
        //Visit the current node.
        //Traverse the right subtree recursively.
        System.out.println("In-order traversal (LNR): ");
        List<Integer> inOrderResult = inOrder(root);
        System.out.println(inOrderResult);

        //Post-order traversal (LRN):
        //Traverse the left subtree recursively.
        //Traverse the right subtree recursively.
        //Visit the current node.
        System.out.println("Post-order traversal (LRN): ");
        List<Integer> postOrderResult = postOrder(root);
        System.out.println(postOrderResult);

        //level-order traversal
        //Visit nodes level by level from left to right.
        System.out.println("Level-order traversal:");
        List<Integer> levelOrderResult = levelOrder(root);
        System.out.println(levelOrderResult);

    }

    // Pre-order traversal
    public static List<Integer> preOrder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        preOrderTraversal(root, result);
        return result;
    }

    private static void preOrderTraversal(TreeNode root, List<Integer> result) {
        if (root != null) {
            result.add(root.val);
            preOrderTraversal(root.left, result);
            preOrderTraversal(root.right, result);
        }
    }

    // In-order traversal
    public static List<Integer> inOrder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inOrderTraversal(root, result);
        return result;
    }

    private static void inOrderTraversal(TreeNode root, List<Integer> result) {
        if (root != null) {
            inOrderTraversal(root.left, result);
            result.add(root.val);
            inOrderTraversal(root.right, result);
        }
    }

    // Post-order traversal
    public static List<Integer> postOrder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        postOrderTraversal(root, result);
        return result;
    }

    private static void postOrderTraversal(TreeNode root, List<Integer> result) {
        if (root != null) {
            postOrderTraversal(root.left, result);
            postOrderTraversal(root.right, result);
            result.add(root.val);
        }
    }

    // Level-order traversal (Breadth-first traversal)
    public static List<Integer> levelOrder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            result.add(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }

        return result;
    }


}

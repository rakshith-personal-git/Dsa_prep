package DSA_Interview_Questions.Trees;

import java.util.LinkedList;
import java.util.List;

import static DSA_Interview_Questions.Trees.TreeNode.createBinaryTree;

public class AllNodesDistanceKInBinaryTree {

    /**
     * Given the root of a binary tree, the value of a target node target, and an integer k,
     * return an array of the values of all nodes that have a distance k from the target node.
     * You can return the answer in any order.
     * <p>
     * Example 1:
     * Input: root = [3,5,1,6,2,0,8,null,null,7,4], target = 5, k = 2
     * Output: [7,4,1]
     * Explanation: The nodes that are a distance 2 from the target node (with value 5) have values 7, 4, and 1.
     * <p>
     * Example 2:
     * Input: root = [1], target = 1, k = 3
     * Output: []
     * <p>
     * Constraints:
     * The number of nodes in the tree is in the range [1, 500].
     * 0 <= Node.val <= 500
     * All the values Node.val are unique.
     * target is the value of one of the nodes in the tree.
     * 0 <= k <= 1000
     */

    public static void main(String[] args) {
        Integer[] input = {3, 5, 1, 6, 2, 0, 8, null, null, 7, 4};
        //Integer[] input = {2,7,5,2,6,null,9,null,null,5,11,4};

        // Create the binary tree and return the root
        TreeNode root = createBinaryTree(input, 0);

        TreeNode target = root.left; //5 is the target node
        int k = 2;
        List<Integer> ans = distanceK(root, target, k);
        ans.forEach(i -> System.out.printf(i + " "));
    }

    private static List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        List<Integer> valuesKDistanceDown = new LinkedList<>();
        findTargetNodeAndPopulateKDistanceValues(root, target, k, valuesKDistanceDown);
        return valuesKDistanceDown;

    }

    private static int findTargetNodeAndPopulateKDistanceValues(TreeNode root, TreeNode target, int k, List<Integer> valuesKDistanceDown) {
        if (root == null) {
            return -1;
        }
        if (root.val == target.val) {
            kDistanceFromTargetNode(root, k, null, valuesKDistanceDown);
            return 1;
        }

        int leftAns = findTargetNodeAndPopulateKDistanceValues(root.left, target, k, valuesKDistanceDown);
        if (leftAns != -1) {
            kDistanceFromTargetNode(root, k - leftAns, root.left, valuesKDistanceDown);
            return leftAns + 1;
        }

        int rightAns = findTargetNodeAndPopulateKDistanceValues(root.right, target, k, valuesKDistanceDown);
        if (rightAns != -1) {
            kDistanceFromTargetNode(root, k - rightAns, root.right, valuesKDistanceDown);
            return rightAns + 1;
        }

        return -1;
    }

    private static void kDistanceFromTargetNode(TreeNode node, int k, TreeNode blocked, List<Integer> valuesKDistanceDown) {
        if (node == null || node == blocked) {
            return;
        }
        if (k == 0) {
            valuesKDistanceDown.add(node.val);
        }
        kDistanceFromTargetNode(node.left, k - 1, blocked, valuesKDistanceDown);
        kDistanceFromTargetNode(node.right, k - 1, blocked, valuesKDistanceDown);
    }

    //TC is O(N) and SC is O(N)..{height of Tree}

}

package DSA_Interview_Questions.Trees;

import java.util.LinkedList;
import java.util.List;

import static DSA_Interview_Questions.Trees.TreeNode.createBinaryTree;

public class AllNodesKLevelDownInBinaryTree {
    /**
     * Given a binary tree, a target node and a integer k, print all the nodes k level down from that target node
     * */

    public static void main(String[] args) {
        Integer[] input = {3, 5, 1, 6, 2, 0, 8, null, null, 7, 4};
        //Integer[] input = {2,7,3,2,6,null,9,null,null,5,11,4};

        // Create the binary tree and return the root
        TreeNode root = createBinaryTree(input, 0);

        TreeNode target = root.left; //5 is the target node
        int k = 2;
        List<Integer> ans = distanceK(root, target, k);
        ans.forEach(i -> System.out.printf(i + " "));
    }

    private static List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        List<Integer> valuesKLevelDown = new LinkedList<>();
        findTargetNodeAndPopulateList(root, target, k, valuesKLevelDown);
        return valuesKLevelDown;

    }

    private static void findTargetNodeAndPopulateList(TreeNode root, TreeNode target, int k, List<Integer> valuesKLevelDown) {
        if (root == null) {
            return;
        }
        if (root == target) {
            KLevelDown(root, k, valuesKLevelDown);
            return;
        }

        findTargetNodeAndPopulateList(root.left, target, k, valuesKLevelDown);
        if (!valuesKLevelDown.isEmpty()){
            return;
        }
        findTargetNodeAndPopulateList(root.right, target, k, valuesKLevelDown);
    }

    private static void KLevelDown(TreeNode node, int k, List<Integer> valuesKLevelDown) {
        if (node == null) {
            return;
        }
        if (k == 0) {
            valuesKLevelDown.add(node.val);
            return;
        }

        KLevelDown(node.left, k - 1, valuesKLevelDown);
        KLevelDown(node.right, k - 1, valuesKLevelDown);
    }
}

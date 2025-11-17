package DSA_Interview_Questions.Trees;

import java.util.concurrent.atomic.AtomicInteger;

import static DSA_Interview_Questions.Trees.TreeNode.createBinaryTree;

public class CamerasInBinaryTree {
    /***
     * You are given the root of a binary tree.
     * We install cameras on the tree nodes where each camera at a node can monitor its parent, itself, and its immediate children.
     * Return the minimum number of cameras needed to monitor all nodes of the tree.
     *
     * Example 1:
     * Input: root = [0,0,null,0,0]
     * Output: 1
     * Explanation: One camera is enough to monitor all nodes if placed as shown.
     *
     * Example 2:
     * Input: root = [0,0,null,0,null,0,null,null,0]
     * Output: 2
     * Explanation: At least two cameras are needed to monitor all nodes of the tree. The above image shows one of the valid configurations of camera placement.
     */

    public static void main(String[] args) {
        Integer[] input = {0, 0, null, 0, null, 0, null, null, 0};

        // Create the binary tree and return the root
        TreeNode root = createBinaryTree(input, 0);

        int ans = minCameraCover(root);
        System.out.println(ans);
    }

    //approach is this -> we need to consider only three scenarios :- 1 if camera is present, 0 if node is already monitored, -1 if node requires a camera

    private static int minCameraCover(TreeNode root) {
        AtomicInteger cameras = new AtomicInteger(0);;
        int answerForRoot = minCameras(root, cameras);
        if (answerForRoot == -1) {
            cameras.addAndGet(1);
        }
        return cameras.get();
    }

    private static int minCameras(TreeNode root, AtomicInteger cameras) {
        if (root == null) {
            return 0;
        }
        int leftAns = minCameras(root.left, cameras);
        int rightAns = minCameras(root.right, cameras);
        if (leftAns == -1 || rightAns == -1) {
            //if left or right child is in need of camera, attaching a camera and returning 1 since this node is covered
            cameras.addAndGet(1);
            return 1;
        }

        if (leftAns == 1 || rightAns == 1) {
            return 0;
            //returning 0 since if left or right child have cameras then parent node is already monitored;
        }

        return -1; //neither of the above scenario, so this node needs camera
    }

    //- Time complexity: O(N)
    //- Space complexity:O(N)
}

package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

public class BJ_ConvertSortedArrayToBinarySearchTree {
    /***
     * Given an integer array nums where the elements are sorted in ascending order, convert it to a height-balanced binary search tree.
     *
     * Example 1:
     * Input: nums = [-10,-3,0,5,9]
     * Output: [0,-3,9,-10,null,5]
     * Explanation: [0,-10,5,null,-3,null,9] is also accepted:
     *
     * Example 2:
     * Input: nums = [1,3]
     * Output: [3,1]
     * Explanation: [1,null,3] and [3,1] are both height-balanced BSTs.
     *
     * Constraints:
     * 1 <= nums.length <= 104
     * -104 <= nums[i] <= 104
     * nums is sorted in a strictly increasing order.
     */

    public static void main(String[] args) {
        int nums[] = {-10, -3, 0, 5, 9};
        TreeNode root = sortedArrayToBST(nums);
        TreeNode.printTreeVisually(root);
    }

    private static TreeNode sortedArrayToBST(int[] nums) {
        if (nums.length == 0) {
            return null;
        }
        return createBinaryTree(nums, 0, nums.length - 1);
    }

    private static TreeNode createBinaryTree(int[] nums, int leftIndex, int rightIndex) {
        if (leftIndex > rightIndex) {
            return null;
        }

        int mid = leftIndex + (rightIndex - leftIndex) / 2; // this is the formula to find mid index
        TreeNode root = new TreeNode(nums[mid]);
        root.left = createBinaryTree(nums, leftIndex, mid - 1);
        root.right = createBinaryTree(nums, mid + 1, rightIndex);
        return root;
    }


}

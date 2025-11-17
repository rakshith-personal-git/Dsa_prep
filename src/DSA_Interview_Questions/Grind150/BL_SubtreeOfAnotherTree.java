package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.Trees.TreeNode;

public class BL_SubtreeOfAnotherTree {
    /**
     * Given the roots of two binary trees root and subRoot, return true if there is a subtree of root with the same structure and node values of subRoot and false otherwise.
     * A subtree of a binary tree tree is a tree that consists of a node in tree and all of this node's descendants.
     * The tree could also be considered as a subtree of itself.
     * <p>
     * Example 1:
     * Input: root = [3,4,5,1,2], subRoot = [4,1,2]
     * Output: true
     * <p>
     * Example 2:
     * Input: root = [3,4,5,1,2,null,null,null,null,0], subRoot = [4,1,2]
     * Output: false
     * <p>
     * Constraints:
     * The number of nodes in the root tree is in the range [1, 2000].
     * The number of nodes in the subRoot tree is in the range [1, 1000].
     * -104 <= root.val <= 104
     * -104 <= subRoot.val <= 104
     */

    public static void main(String[] args) {
//        Integer[] input1 = {3, 4, 5, 1, 2};
//        Integer[] input2 = {4, 1, 2};
        Integer[] input1 = {1,null,1,null,1,null,1,null,1,null,1,null,1,null,1,null,1,null,1,null,1,2};
        Integer[] input2 = {1,null,1,null,1,null,1,null,1,null,1,2};
        TreeNode root = TreeNode.createBinaryTree(input1, 0);
        TreeNode subRoot = TreeNode.createBinaryTree(input2, 0);
        boolean ans = isSubtree(root, subRoot);
        System.out.println(ans);

    }

    private static boolean isSubtree(TreeNode root, TreeNode subRoot) {
        if (root == null && subRoot == null) {
            return true;
        } else if (root == null || subRoot == null) {
            return false;
        }

        return doesSubRootExists(root, subRoot);


    }


    private static boolean doesSubRootExists(TreeNode root, TreeNode subRoot) {
        if (root == null) {
            return false;
        }


        if (root.val == subRoot.val) {
            if (isSameTree(root, subRoot)) {
                return true;
            }
        }

        if (doesSubRootExists(root.left, subRoot)) {
            return true;
        }

        if (doesSubRootExists(root.right, subRoot)) {
            return true;
        }

        return false;

    }

    private static boolean isSameTree(TreeNode root1, TreeNode root2) {
        if (root1 == null && root2 == null) {
            return true;
        } else if (root1 == null || root2 == null) {
            return false;
        } else if (root1.val != root2.val) {
            return false;
        }

        return isSameTree(root1.left, root2.left) && isSameTree(root1.right, root2.right);
    }

    //doesSubRootExists is called once for each node in root, so it can be called up to O(n) times.
    //Each call to isSameTree involves comparing up to O(m) nodes in subRoot.
    //Thus, the time complexity is O(n * m)

    //the space complexity is O(n+m) in the worst case due to the recursive calls.
}

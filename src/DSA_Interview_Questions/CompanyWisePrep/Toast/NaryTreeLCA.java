package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NaryTreeLCA {

    // Definition for an N-ary Tree Node
    public static class Node {
        public int val;
        public List<Node> children;

        public Node(int val) {
            this.val = val;
            this.children = new ArrayList<>();
        }

        public Node(int val, List<Node> children) {
            this.val = val;
            this.children = children;
        }
    }

    /**
     * Finds the Lowest Common Ancestor (LCA) of nodes p and q in an N-ary tree.
     *
     * @param root The root node of the N-ary tree.
     * @param p First node.
     * @param q Second node.
     * @return The LCA Node.
     */
    public static Node lowestCommonAncestor(Node root, Node p, Node q) {
        // Base Case 1: If root is null, or matches p or q, return root
        if (root == null || root == p || root == q) {
            return root;
        }

        if (root.children == null) {
            return null;
        }

        int count = 0;
        Node temp = null;

        // Step 1: Recursively search all child subtrees
        for (Node child : root.children) {
            Node res = lowestCommonAncestor(child, p, q);

            if (res != null) {
                count++;
                temp = res; // Track non-null subtree result
            }
        }

        // Step 2: If p and q are found in 2 or more distinct child subtrees, current root is the LCA
        if (count >= 2) {
            return root;
        }

        // Step 3: If only 1 child subtree contains targets, propagate it up. If 0, 'temp' is null.
        return temp;
    }

    // --- Main Method with Executable IntelliJ Test Cases ---
    public static void main(String[] args) {
        // Test Case 1: Prompt Example (Root = 1, Children = [2, 3, 4], p = 2, q = 3)
        //       1
        //     / | \
        //    2  3  4
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        Node root1 = new Node(1, Arrays.asList(n2, n3, n4));
        runTest("Test 1 (Direct Children of Root)", root1, n2, n3, 1);

        // Test Case 2: Deeper Tree (Nodes 4 and 5 under Node 2)
        //          1
        //        /   \
        //       2     3
        //      / \
        //     4   5
        Node n4_2 = new Node(4);
        Node n5_2 = new Node(5);
        Node n2_2 = new Node(2, Arrays.asList(n4_2, n5_2));
        Node n3_2 = new Node(3);
        Node root2 = new Node(1, Arrays.asList(n2_2, n3_2));
        runTest("Test 2 (Nodes 4 & 5 under Node 2)", root2, n4_2, n5_2, 2);

        // Test Case 3: Ancestor Relationship (Node 2 is direct parent of Node 4)
        runTest("Test 3 (Node 2 is Ancestor of Node 4)", root2, n2_2, n4_2, 2);

        // Test Case 4: Nodes in separate main subtrees (Node 4 under Node 2, and Node 3)
        runTest("Test 4 (Nodes in Different Main Branches: 4 & 3)", root2, n4_2, n3_2, 1);
    }

    private static void runTest(String testName, Node root, Node p, Node q, int expectedVal) {
        Node lca = lowestCommonAncestor(root, p, q);
        if (lca != null && lca.val == expectedVal) {
            System.out.println(testName + " PASSED -> LCA Node Val: " + lca.val);
        } else {
            int gotVal = (lca != null) ? lca.val : -1;
            System.out.println(testName + " FAILED -> Expected: " + expectedVal + ", Got: " + gotVal);
        }
    }
}

/*
 * ============================================================================
 * COMPLEXITY ANALYSIS:
 * ============================================================================
 * Time Complexity: O(N)
 *   Where N is the total number of nodes in the N-ary tree. In the worst case, 
 *   every node is visited at most once during the post-order DFS traversal.
 *
 * Space Complexity: O(H)
 *   Where H is the height of the N-ary tree. Memory overhead is strictly 
 *   bounded by the implicit recursion call stack depth (O(log N) for balanced 
 *   trees, O(N) for degenerate/skewed trees).
 * ============================================================================
 */
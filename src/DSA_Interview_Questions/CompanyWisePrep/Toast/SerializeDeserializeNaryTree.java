package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SerializeDeserializeNaryTree {

    // Definition for an N-ary Tree Node
    public static class Node {
        public int val;
        public List<Node> children;

        public Node() {
            this.children = new ArrayList<>();
        }

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
     * Encodes an N-ary tree to a single string using Pre-Order DFS with Child-Count Encoding.
     *
     * @param root The root node of the N-ary tree.
     * @return Serialized string representation.
     */
    public static String serialize(Node root) {
        if (root == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        buildString(root, sb);
        return sb.toString();
    }

    private static void buildString(Node node, StringBuilder sb) {
        if (node == null) {
            return;
        }

        int childCount = (node.children == null) ? 0 : node.children.size();
        
        // Format: <val>,<childCount>,
        sb.append(node.val).append(",").append(childCount).append(",");

        // Recursively encode child subtrees
        if (node.children != null) {
            for (Node child : node.children) {
                buildString(child, sb);
            }
        }
    }

    /**
     * Decodes your encoded string data back to an N-ary tree.
     *
     * @param data Serialized string representation.
     * @return Reconstructed N-ary tree root.
     */
    public static Node deserialize(String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }

        String[] tokens = data.split(",");
        Queue<String> queue = new LinkedList<>(Arrays.asList(tokens));

        return buildTree(queue);
    }

    private static Node buildTree(Queue<String> queue) {
        if (queue.isEmpty()) {
            return null;
        }

        int val = Integer.parseInt(queue.poll());
        int childCount = Integer.parseInt(queue.poll());

        Node node = new Node(val, new ArrayList<>());

        // Recursively build 'childCount' subtrees
        for (int i = 0; i < childCount; i++) {
            node.children.add(buildTree(queue));
        }

        return node;
    }

    // --- Main Method with Executable IntelliJ Test Cases ---
    public static void main(String[] args) {
        // Test Case 1: Prompt Example (Single Root Node)
        Node root1 = new Node(1);
        runTest("Test 1 (Single Root Node)", root1);

        // Test Case 2: Multi-level N-ary Tree
        //          1
        //       /  |  \
        //      2   3   4
        //         / \
        //        5   6
        Node n5 = new Node(5);
        Node n6 = new Node(6);
        Node n2 = new Node(2);
        Node n3 = new Node(3, Arrays.asList(n5, n6));
        Node n4 = new Node(4);
        Node root2 = new Node(1, Arrays.asList(n2, n3, n4));
        runTest("Test 2 (Multi-level N-ary Tree)", root2);

        // Test Case 3: Empty Tree (null)
        runTest("Test 3 (Null/Empty Tree)", null);

        // Test Case 4: Degenerate Linear Tree (1 -> 2 -> 3)
        Node n3_linear = new Node(3);
        Node n2_linear = new Node(2, List.of(n3_linear));
        Node root4 = new Node(1, List.of(n2_linear));
        runTest("Test 4 (Degenerate Linear Tree)", root4);
    }

    private static void runTest(String testName, Node original) {
        String serialized = serialize(original);
        Node deserialized = deserialize(serialized);

        boolean isEqual = isSameTree(original, deserialized);
        if (isEqual) {
            System.out.println(testName + " PASSED");
            System.out.println("   Serialized Output: \"" + serialized + "\"");
        } else {
            System.out.println(testName + " FAILED");
            System.out.println("   Serialized String: \"" + serialized + "\"");
        }
    }

    /**
     * Helper method to structurally compare two N-ary trees.
     */
    private static boolean isSameTree(Node n1, Node n2) {
        if (n1 == null && n2 == null) return true;
        if (n1 == null || n2 == null) return false;
        if (n1.val != n2.val) return false;

        int size1 = (n1.children == null) ? 0 : n1.children.size();
        int size2 = (n2.children == null) ? 0 : n2.children.size();

        if (size1 != size2) return false;

        for (int i = 0; i < size1; i++) {
            if (!isSameTree(n1.children.get(i), n2.children.get(i))) {
                return false;
            }
        }

        return true;
    }
}

/*
 * ============================================================================
 * COMPLEXITY ANALYSIS:
 * ============================================================================
 * Time Complexity: O(N)
 *   Where N is the total number of nodes in the N-ary tree.
 *   - `serialize` visits every node once and appends 2 primitive tokens per node.
 *   - `deserialize` processes 2N tokens from the Queue, spending O(1) per node.
 *
 * Space Complexity: O(N) Auxiliary Space
 *   - `serialize` consumes memory for the StringBuilder and recursion stack O(H).
 *   - `deserialize` consumes memory for token array/queue and call stack O(H).
 * ============================================================================
 */
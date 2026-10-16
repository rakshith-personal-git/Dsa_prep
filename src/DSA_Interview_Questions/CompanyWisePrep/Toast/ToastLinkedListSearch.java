package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.*;

public class ToastLinkedListSearch {

    // --- Data Model ---
    public static class ListNode {
        public int val;
        public ListNode next;
        public ListNode child; // Optional nested linked list

        public ListNode(int val) {
            this.val = val;
            this.next = null;
            this.child = null;
        }

        public ListNode(int val, ListNode next, ListNode child) {
            this.val = val;
            this.next = next;
            this.child = child;
        }
    }

    // --- Search Result Wrapper ---
    public static class NodeSearchResult {
        private ListNode node;
        private int listIndex;
        private int depth;

        public NodeSearchResult(ListNode node, int listIndex, int depth) {
            this.node = node;
            this.listIndex = listIndex;
            this.depth = depth;
        }

        @Override
        public String toString() {
            return "NodeSearchResult{val=" + (node != null ? node.val : "null") +
                    ", listIndex=" + listIndex +
                    ", depth=" + depth + '}';
        }
    }


    /**
     * Recursively searches for the first occurrence of target across a list of linked lists.
     *
     * @param heads List of top-level linked list head nodes.
     * @param target Integer value to find.
     * @return NodeSearchResult if found, or null if target does not exist.
     */
    public static NodeSearchResult findFirstOccurrence(List<ListNode> heads, int target) {
        if (heads == null || heads.isEmpty()) {
            return null;
        }

        // Iterate through top-level lists
        for (int i = 0; i < heads.size(); i++) {
            // Single-pass search starting at depth 0 for list index 'i'
            NodeSearchResult result = dfsSearch(heads.get(i), target, i, 0);
            if (result != null) {
                return result; // Instantly return on first match across all lists
            }
        }

        return null; // Not found
    }

    /**
     * Top-down DFS helper that propagates listIndex and depth downwards.
     */
    private static NodeSearchResult dfsSearch(ListNode node, int target, int listIndex, int currentDepth) {
        if (node == null) {
            return null;
        }

        // 1. Check current node match
        if (node.val == target) {
            return new NodeSearchResult(node, listIndex, currentDepth);
        }

        // 2. Search child list first (vertical precedence, depth + 1)
        NodeSearchResult childResult = dfsSearch(node.child, target, listIndex, currentDepth + 1);
        if (childResult != null) {
            return childResult;
        }

        // 3. Search next sibling list second (horizontal precedence, depth + 1)
        return dfsSearch(node.next, target, listIndex, currentDepth + 1);
    }

    // --- Main Method with Test Cases ---
    public static void main(String[] args) {
        /*
         * Constructing Test Lists:
         *
         * List 0: 10 -> 20 -> 30
         *                | (child)
         *               25 -> 27
         *
         * List 1: 40 -> 50
         *
         * List 2: 60 -> 20 -> 80   (Note: 20 appears again here, but List 0 occurrence should be returned)
         */

        // Child list for node 20 in List 0
        ListNode child27 = new ListNode(27);
        ListNode child25 = new ListNode(25, child27, null);

        // List 0
        ListNode node30 = new ListNode(30);
        ListNode node20 = new ListNode(20, node30, child25);
        ListNode head0 = new ListNode(10, node20, null);

        // List 1
        ListNode node50 = new ListNode(50);
        ListNode head1 = new ListNode(40, node50, null);

        // List 2
        ListNode node80 = new ListNode(80);
        ListNode node20_dup = new ListNode(20, node80, null);
        ListNode head2 = new ListNode(60, node20_dup, null);

        List<ListNode> heads = Arrays.asList(head0, head1, head2);

        // --- Tests ---

        // Test 1: Target at top level of List 0
        runTest("Test 1 (Head Node)", heads, 10, 0, 0);

        // Test 2: Target inside nested child list of List 0
        runTest("Test 2 (Nested Child Node)", heads, 25, 0, 2);

        // Test 3: Target in List 1
        runTest("Test 3 (Top Level List 1)", heads, 50, 1, 1);

        // Test 4: Target with duplicate value across lists (must return FIRST occurrence in List 0)
        runTest("Test 4 (First Occurrence of Duplicate)", heads, 20, 0, 1);

        // Test 5: Value not present
        runTest("Test 5 (Not Found)", heads, 999, -1, -1);
    }

    private static void runTest(String testName, List<ListNode> heads, int target, int expectedListIndex, int expectedDepth) {
        NodeSearchResult result = findFirstOccurrence(heads, target);
        if (expectedListIndex == -1) {
            if (result == null) {
                System.out.println(testName + " PASSED");
            } else {
                System.out.println(testName + " FAILED -> Expected null, Got: " + result);
            }
        } else {
            if (result != null && result.listIndex == expectedListIndex && result.depth == expectedDepth) {
                System.out.println(testName + " PASSED (" + result + ")");
            } else {
                System.out.println(testName + " FAILED -> Expected listIndex=" + expectedListIndex + ", depth=" + expectedDepth + ", Got: " + result);
            }
        }
    }
}

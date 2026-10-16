package DSA_Interview_Questions.CompanyWisePrep.Toast;

import DSA_Interview_Questions.LinkedList.ListNode;

import java.util.Arrays;
import java.util.List;

public class MultiLinkedListSearch {

    /**
     * Outer recursive function to iterate through the collection of linked list heads.
     */
    public static int[] findInListOfLinkedLists(List<ListNode> heads, int target) {
        return searchOuterListRecursive(heads, 0, target);
    }

    private static int[] searchOuterListRecursive(List<ListNode> heads, int listIndex, int target) {
        // Base Case: Inspected all linked lists
        if (heads == null || listIndex >= heads.size()) {
            return new int[]{-1, -1};
        }

        // Search inside the current linked list recursively
        int nodeIndex = searchSingleListRecursive(heads.get(listIndex), target);
        if (nodeIndex != -1) {
            return new int[]{listIndex, nodeIndex};
        }

        // Move to the next linked list head in the main list
        return searchOuterListRecursive(heads, listIndex + 1, target);
    }

    /**
     * Inner recursive function to search inside a single linked list.
     */
    private static int searchSingleListRecursive(ListNode head, int target) {
        if (head == null) return -1;
        if (head.val == target) return 0;

        int res = searchSingleListRecursive(head.next, target);
        return (res == -1) ? -1 : 1 + res;
    }

    private static int[] findInListOfLinkedLists2(List<ListNode> heads, int target) {
        if (heads == null || heads.isEmpty()) return new int[]{-1, -1};


        for (int i = 0; i < heads.size(); i++) {
            if (heads.get(i).val == target) {
                return new int[]{i, i};
            }
            int nodeIndex = searchSingleListRecursive2(heads.get(i), target);
            if (nodeIndex != -1) {
                return new int[]{i, nodeIndex };
            }
        }

        return new int[]{-1, -1};

    }

    private static int searchSingleListRecursive2(ListNode listNode, int target) {
        if (listNode == null) return -1;
        if (listNode.val == target) return 0;
        int res = searchSingleListRecursive2(listNode.next, target);
        return (res == -1) ? -1 : 1 + res;
    }

    public static void main(String[] args) {
        // List 0: 1 -> 2
        ListNode list0 = new ListNode(1);
        list0.next = new ListNode(2);

        // List 1: 5 -> 7 -> 9
        ListNode list1 = new ListNode(5);
        list1.next = new ListNode(7);
        list1.next.next = new ListNode(9);

        List<ListNode> allLists = Arrays.asList(list0, list1);

        int target = 7;
        int[] result = findInListOfLinkedLists(allLists, target);
        int[] result2 = findInListOfLinkedLists2(allLists, target);

        // Output: Target found at List index: 1, Node index: 1
        System.out.println("Target found at List index: " + result[0] + ", Node index: " + result[1]);
        System.out.println("Target found at List index: " + result2[0] + ", Node index: " + result2[1]);
    }
}

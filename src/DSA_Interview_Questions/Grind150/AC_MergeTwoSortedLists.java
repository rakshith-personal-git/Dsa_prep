package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.LinkedList.ListNode;

public class AC_MergeTwoSortedLists {
    /**
     * You are given the heads of two sorted linked lists list1 and list2.
     * Merge the two lists into one sorted list. The list should be made by splicing together the nodes of the first two lists.
     * Return the head of the merged linked list.
     * <p>
     * Example 1:
     * Input: list1 = [1,2,4], list2 = [1,3,4]
     * Output: [1,1,2,3,4,4]
     * <p>
     * Example 2:
     * Input: list1 = [], list2 = []
     * Output: []
     * <p>
     * Example 3:
     * Input: list1 = [], list2 = [0]
     * Output: [0]
     * <p>
     * <p>
     * Constraints:
     * The number of nodes in both lists is in the range [0, 50].
     * -100 <= Node.val <= 100
     * Both list1 and list2 are sorted in non-decreasing order.
     */

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(4);

        ListNode head2 = new ListNode(1);
        head2.next = new ListNode(3);
        head2.next.next = new ListNode(4);

        ListNode ans2 = mergeTwoLists(head, head2);
        ListNode.printLinkedList(ans2);

        System.out.println();

        ListNode head3 = new ListNode(1);
        head3.next = new ListNode(2);
        head3.next.next = new ListNode(4);
        ListNode head4 = new ListNode(1);
        head4.next = new ListNode(3);
        head4.next.next = new ListNode(4);

        ListNode ans = mergeTwoListsRecursive(head3, head4);
        ListNode.printLinkedList(ans);

    }

    private static ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);  // Initialize dummy node
        ListNode current = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val < list2.val) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
            // moves the current pointer forward to the newly attached node.
            // This is crucial because it prepares current to attach the next node from either list1 or list2 in the next iteration.
        }

        // Attach the remaining elements, if any
        //since by the time we come here 1 among 2 lists would have reached the end of its length, checking for only 1 and attaching it to ans
        if (list1 != null) {
            current.next = list1;
        } else {
            current.next = list2;
        }

        return dummy.next;  // Return the merged list starting from the first node
    }
    //The time complexity is O(n+m)
    //The space complexity is O(1)

    private static ListNode mergeTwoListsRecursive(ListNode list1, ListNode list2) {
        if (list1 == null) {
            return list2;
        } else if (list2 == null) {
            return list1;
        }

        if (list1.val <= list2.val) {
            list1.next = mergeTwoLists(list1.next, list2);
            return list1;
        } else {
            list2.next = mergeTwoLists(list1, list2.next);
            return list2;
        }
    }
    //The time complexity is O(n+m)
    //The space complexity is O(n+m)
}

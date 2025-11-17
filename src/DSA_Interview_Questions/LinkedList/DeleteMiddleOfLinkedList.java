package DSA_Interview_Questions.LinkedList;

public class DeleteMiddleOfLinkedList {
    /**
     * Given a Linked List, delete the middle node of the list and return the new head of the list.
     * For example, if the given list is 1->2->3->4->5, the returned linked list should be 1->2->4->5.
     * Note: If the given list contains 1 node, it should be deleted and new head must be returned.
     * If the given list contains even number of nodes, delete the second middle node among both the middle nodes.
     * For example, 1->2->3->4 shoud return 1->2->4.
     */

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);


        head = solve(head);

        // Print the reversed linked list
        while (head != null) {
            System.out.print(head.val + " ");
            head = head.next;
        }
    }

    private static ListNode solve(ListNode head) {
        if (head == null || head.next == null) {
            // There are 0 or 1 nodes, no middle node to delete
            return null;
        }

        ListNode slow = head;
        ListNode fast = head;
        ListNode prev = null;

        while (fast != null && fast.next != null) {
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }

        // Delete the middle node (slow) by updating pointers
        if (prev != null) {
            prev.next = slow.next;
        } else {
            // If there is no prev, it means the head itself is the middle node
            head = head.next;
        }

        return head;
    }
}

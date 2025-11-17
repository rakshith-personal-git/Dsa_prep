package DSA_Interview_Questions.LinkedList;

public class MiddleOfLinkedList {
    /**
     * Given the head of a singly linked list, return the middle node of the linked list.
     * If there are two middle nodes, return the second middle node.
     */



    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);


       ListNode middleNode = findMiddleNode(head);

        // Print the reversed linked list
        while (middleNode != null) {
            System.out.print(middleNode.val + " ");
            middleNode = middleNode.next;
        }
    }

    public static ListNode findMiddleNode(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        //using 2x fast rule
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    //TC is O(N) and SC is O(1)
}

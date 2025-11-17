package DSA_Interview_Questions.Grind150;


import DSA_Interview_Questions.LinkedList.ListNode;

public class AQ_ReverseLinkedList {
    /**
     * Given the head of a singly linked list, reverse the list in a single traversal, and return the reversed list
     */

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);


        ListNode reversedHead = reverseList(head);
        // Print the reversed linked list
        while (reversedHead != null) {
            System.out.print(reversedHead.val + " ");
            reversedHead = reversedHead.next;
        }
    }

    //iterative solutions
    public static ListNode reverseList(ListNode head) {
        ListNode previousNode = null; //basically new List node starting from reverse.
        ListNode currentNode = head;

        while (currentNode != null) {
            ListNode forwardNode = currentNode.next;
            currentNode.next = previousNode;

            //move previous and current nodes
            previousNode = currentNode;
            currentNode = forwardNode;
        }

        return previousNode;
    }
    //TC is O(N) and SC is O(1)



    //recursive solution
    public static ListNode reverseListRecursive(ListNode head) {
        if (head == null || head.next == null) {
            return head; // Base case: empty list or single node
        }
        ListNode newHead = reverseListRecursive(head.next); // Recur to the end
        head.next.next = head; // Reverse the link
        head.next = null;      // Sever old forward link
        return newHead;        // Return new head to all previous recursions
    }
    //TC is O(N) and SC is O(N)


}

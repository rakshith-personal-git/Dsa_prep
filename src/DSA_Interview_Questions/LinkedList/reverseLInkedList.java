package DSA_Interview_Questions.LinkedList;


public class reverseLInkedList {
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


}

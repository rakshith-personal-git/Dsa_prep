package DSA_Interview_Questions.LinkedList;

public class ReverseNodesInK_Group {

    /**
     * Given the head of a linked list, reverse the nodes of the list k at a time, and return the modified list.
     * k is a positive integer and is less than or equal to the length of the linked list.
     * If the number of nodes is not a multiple of k then left-out nodes, in the end, should remain as it is.
     * <p>
     * You may not alter the values in the list's nodes, only nodes themselves may be changed.
     * <p>
     * <p>
     * Example 1:
     * Input: head = [1,2,3,4,5], k = 2
     * Output: [2,1,4,3,5]
     * <p>
     * Example 2:
     * Input: head = [1,2,3,4,5], k = 3
     * Output: [3,2,1,4,5]
     * <p>
     * Constraints:
     * The number of nodes in the list is n.
     * 1 <= k <= n <= 5000
     * 0 <= Node.val <= 1000
     */

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);
        head.next.next.next.next.next = new ListNode(6);
        head.next.next.next.next.next.next = new ListNode(7);
        head.next.next.next.next.next.next.next = new ListNode(8);
        int k = 3;


        ListNode ansHead = reverseKGroup(head, k);
        while (ansHead != null) {
            System.out.print(ansHead.val + " ");
            ansHead = ansHead.next;
        }

    }

    private static ListNode reverseKGroup(ListNode head, int k) {

        //take a pointer node and put a dummy value and attach the actual head to it since we need this pointer to start pointing from head
        ListNode dummy = new ListNode(-1); // having this dummy listNode is really imp to have the proper linking between all nodes
        dummy.next = head;
        ListNode pointer = dummy;


        while (pointer != null) {
            //check if k nodes is available
            ListNode node = pointer;
            for (int i = 0; i < k && node != null; i++) {
                node = node.next;
            }
            if (node == null) {
                //there are less than k nodes available
                break;
            }

            //reverse K nodes from pointer
            ListNode previousNode = null;
            ListNode currentNode = pointer.next;
            for (int i = 0; i < k; i++) {
                ListNode forwardNode = currentNode.next;
                currentNode.next = previousNode;
                previousNode = currentNode;
                currentNode = forwardNode;
            }

            //connect the next part of the main list to reverse part
            ListNode tail = pointer.next;
            pointer.next = previousNode;
            tail.next = currentNode;
            pointer = tail;
        }

        return dummy.next;
    }

}

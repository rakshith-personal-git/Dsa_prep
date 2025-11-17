package DSA_Interview_Questions.LinkedList;

import static DSA_Interview_Questions.LinkedList.MiddleOfLinkedList.findMiddleNode;
import static DSA_Interview_Questions.LinkedList.reverseLInkedList.reverseList;

public class PalindromeLinkedList {

    /**
     * Given the head of a singly linked list, return true if it is a palindrome
     * or false otherwise.
     *
     * Constraints:
     * The number of nodes in the list is in the range [1, 105].
     * 0 <= Node.val <= 9
     */


    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(2);
        head.next.next.next.next = new ListNode(1);


        boolean ans = isPalindrome(head);

        System.out.println(ans);
    }

    private static boolean isPalindrome(ListNode head) {
        if (head == null) {
            return false;
        }

        ListNode middleNode = findMiddleNode(head);
        ListNode first;
        ListNode second ;

        if (middleNode.next == null) {
            if (head.next == null) {
                //basically for small inputs like [1]
                return true;
            }
            second = head.next; //for small inputs like [1,2], [1,1]
            first = head;
        } else {
            second = reverseList(middleNode);
            first = head;
        }


        while (first != null && second != null) {
            if (first.val != second.val) {
                return false;
            }
            first = first.next;
            second = second.next;
        }
        return true;
    }

    //TC is O(N) and SC is O(1)
}

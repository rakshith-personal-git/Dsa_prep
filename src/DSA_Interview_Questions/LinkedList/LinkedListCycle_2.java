package DSA_Interview_Questions.LinkedList;

public class LinkedListCycle_2 {
    /**
     * Given the head of a linked list, return the node where the cycle begins. If there is no cycle, return null.
     * There is a cycle in a linked list if there is some node in the list that can be reached again by continuously following the next pointer.
     * Internally, pos is used to denote the index of the node that tail's next pointer is connected to (0-indexed). It is -1 if there is no cycle.
     * Note that pos is not passed as a parameter.
     * Do not modify the linked list
     */

    public static void main(String[] args) {
        ListNode head = new ListNode(3);
        head.next = new ListNode(2);
        head.next.next = new ListNode(0);
        head.next.next.next = new ListNode(-4);
        head.next.next.next.next = head.next;

        ListNode firstNodeOfCycle = detectCycle(head);
        System.out.println(firstNodeOfCycle.val);
    }

    //first check whether cycle exists or not using loyd's Cycle-Finding Algorithm, once confirmed assign fast to head
    //and iterate through both slow and fast by 1 until both match
    private static ListNode detectCycle(ListNode head) {
        if(head == null || head.next == null) {
            return null;//has only 1 or no element which is not enough for cycle
        }
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                break;
            }
        }

        if (slow != fast) {
            //no cycle exist
            return null;
        }

        //reAssign fast node to head
        fast = head;
        while (slow != fast) {
            slow = slow.next;
            fast = fast.next;
        }

        return slow;
    }
}

package DSA_Interview_Questions.Heap;

import DSA_Interview_Questions.LinkedList.ListNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class MergeKSortedLists {
    /**
     * You are given an array of k linked-lists lists, each linked-list is sorted in ascending order.
     * Merge all the linked-lists into one sorted linked-list and return it.
     * Example 1:
     * Input: lists = [[1,4,5],[1,3,4],[2,6]]
     * Output: [1,1,2,3,4,4,5,6]
     * Explanation: The linked-lists are:
     * [
     * 1->4->5,
     * 1->3->4,
     * 2->6
     * ]
     * merging them into one sorted list:
     * 1->1->2->3->4->4->5->6
     * <p>
     * Example 2:
     * Input: lists = []
     * Output: []
     * <p>
     * Example 3:
     * Input: lists = [[]]
     * Output: []
     * <p>
     * Constraints:
     * k == lists.length
     * 0 <= k <= 104
     * 0 <= lists[i].length <= 500
     * -104 <= lists[i][j] <= 104
     * lists[i] is sorted in ascending order.
     * The sum of lists[i].length will not exceed 104.
     */

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(4);
        head.next.next = new ListNode(5);

        ListNode head2 = new ListNode(1);
        head2.next = new ListNode(3);
        head2.next.next = new ListNode(4);

        ListNode head3 = new ListNode(2);
        head3.next = new ListNode(6);

        ListNode[] lists = {head, head2, head3};

        ListNode answerHead = mergeKLists(lists);

        while (answerHead != null) {
            System.out.print(answerHead.val + " ");
            answerHead = answerHead.next;
        }

    }

    //this is used when instead of LinkedList if ArrayList is given.
//    class Pair implements Comparable<Pair> {
//        int value;
//        int listIndex;
//        int dataIndex;
//
//        Pair(int value, int listIndex, int dataIndex) {
//            this.value = value;
//            this.listIndex = listIndex;
//            this.dataIndex = dataIndex;
//        }
//
//
//        @Override
//        public int compareTo(Pair o) {
//            return this.value - o.value;
//        }
//    }

    private static ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }


        PriorityQueue<ListNode> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(a -> a.val)); //Comparator.comparingInt(a -> a.val) is ntg but (a,b) -> a.val - b.val

        for (ListNode head : lists) {
            if (head != null) {
                priorityQueue.add(head); //adding head of all 3 lists to priority queue
            }
        }

        // Creating a dummy node to start the merged list
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (priorityQueue.size() > 0) {
            ListNode smallest = priorityQueue.remove();

            //adding the smallest node to the answer
            current.next = smallest;
            current = current.next; //the current pointer is moved to the newly added node. This prepares current for the next iteration, where the next smallest node will be added to the merged list.

            if (smallest.next != null) {
                priorityQueue.add(smallest.next); //adding next element from the list of the extracted node
            }
        }
        return dummy.next;
    }

    //TC is O(N log K), and SC is O(K)
}

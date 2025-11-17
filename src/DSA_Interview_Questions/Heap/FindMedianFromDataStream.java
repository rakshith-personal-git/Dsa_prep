package DSA_Interview_Questions.Heap;

import java.util.Collections;
import java.util.PriorityQueue;

public class FindMedianFromDataStream {
    /**
     * The median is the middle value in the ordered integer list. If the list size is odd, consider the smaller median as the middle value.
     * You are required to write a medianFinder class using following functions
     * void addNum(int num) adds the integer num from the data stream to the data structure.
     * remove() -> shd remove and return the median value of elements so far.
     * findMedian -> shd return the median value of elements so far
     */


    public static void main(String[] args) {
        //input
        add(1);
        add(2);
        add(16);
        add(3);
        System.out.println(findMedian());
        add(9);
        System.out.println(findMedian());
        add(6);
        add(4);
        System.out.println(remove());
        System.out.println(findMedian());
    }


    //Approach is to store the half smaller elements in bucket/priorityQueue (maxPq) and another half greater elements in other bucket(minPq)
    // such that at any given point of time size d/f b/w these 2 PQs doesn't cross 1

    static PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder()); // max-heap for smaller half
    static PriorityQueue<Integer> minHeap = new PriorityQueue<>(); // min-heap for larger half
    //pq1 is maxPQ and pq2 is minPQ by default

    private static void add(int val) {
        if (maxHeap.size() == 0 && minHeap.size() == 0) {
            maxHeap.add(val);
        } else {
            if (val < maxHeap.peek()) {
                maxHeap.add(val);
            } else {
                minHeap.add(val);
            }
        }

        int diff = Math.abs(maxHeap.size() - minHeap.size());
        if (diff > 1) {
            if (maxHeap.size() > minHeap.size()) {
                minHeap.add(maxHeap.remove());
            } else {
                maxHeap.add(minHeap.remove());
            }
        }
    }

    private static int findMedian() {
        if (maxHeap.size() >= minHeap.size()) {
            return maxHeap.peek();
        } else {
            return minHeap.peek();
        }
    }


    private static int remove() {
        if (maxHeap.size() >= minHeap.size()) {
            return maxHeap.remove();
        } else {
            return minHeap.remove();
        }
    }

    //TC is O(logN) for add, O(1) for median, O(logN) for remove

}

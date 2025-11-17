package DSA_Interview_Questions.Grind150;

import java.util.Collections;
import java.util.PriorityQueue;

public class DN_FindMedianFromDataStream {
    /**
     * The median is the middle value in an ordered integer list. If the size of the list is even, there is no middle value,
     * and the median is the mean of the two middle values.
     * For example, for arr = [2,3,4], the median is 3.
     * For example, for arr = [2,3], the median is (2 + 3) / 2 = 2.5.
     * <p>
     * Implement the MedianFinder class:
     * MedianFinder() initializes the MedianFinder object.
     * void addNum(int num) adds the integer num from the data stream to the data structure.
     * double findMedian() returns the median of all elements so far. Answers within 10-5 of the actual answer will be accepted.
     * <p>
     * Example 1:
     * Input
     * ["MedianFinder", "addNum", "addNum", "findMedian", "addNum", "findMedian"]
     * [[], [1], [2], [], [3], []]
     * Output
     * [null, null, null, 1.5, null, 2.0]
     * <p>
     * Explanation
     * MedianFinder medianFinder = new MedianFinder();
     * medianFinder.addNum(1);    // arr = [1]
     * medianFinder.addNum(2);    // arr = [1, 2]
     * medianFinder.findMedian(); // return 1.5 (i.e., (1 + 2) / 2)
     * medianFinder.addNum(3);    // arr[1, 2, 3]
     * medianFinder.findMedian(); // return 2.0
     * <p>
     * Constraints:
     * -105 <= num <= 105
     * There will be at least one element in the data structure before calling findMedian.
     * At most 5 * 104 calls will be made to addNum and findMedian.
     */

    public static void main(String[] args) {
        MedianFinder medianFinder = new MedianFinder();
        medianFinder.addNum(1);    // arr = [1]
        medianFinder.addNum(2);    // arr = [1, 2]
        System.out.println(medianFinder.findMedian()); // return 1.5 (i.e., (1 + 2) / 2)
        medianFinder.addNum(3);    // arr[1, 2, 3]
        System.out.println(medianFinder.findMedian()); // return 2.0
    }

    static class MedianFinder {
        PriorityQueue<Integer> maxHeap;
        PriorityQueue<Integer> minHeap;

        public MedianFinder() {
            maxHeap = new PriorityQueue<>(Collections.reverseOrder());
            minHeap = new PriorityQueue<>();
        }

        public void addNum(int num) {
            if (maxHeap.size() == 0 && minHeap.size() == 0) {
                maxHeap.add(num);
            } else {
                if (num < maxHeap.peek()) {
                    maxHeap.add(num);
                } else {
                    minHeap.add(num);
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

        public double findMedian() {
            if (maxHeap.size() != minHeap.size()) {
                if (maxHeap.size() > minHeap.size()) {
                    return maxHeap.peek();
                } else {
                    return minHeap.peek();
                }
            } else {
                return (double) (maxHeap.peek() + minHeap.peek()) / 2;
            }
        }
    }
}

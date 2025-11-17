package DSA_Interview_Questions.Heap;

import java.util.Collections;
import java.util.PriorityQueue;

public class RunningMedian {
    /**
     *Given an array of integers, A denoting a stream of integers. New arrays of integer B and C are formed.
     * Each time an integer is encountered in a stream, append it at the end of B and append the median of array B at the C.
     * Find and return the array C.
     *
     * NOTE:
     * If the number of elements is N in B and N is odd, then consider the median as B[N/2] ( B must be in sorted order).
     * If the number of elements is N in B and N is even, then consider the median as B[N/2-1]. ( B must be in sorted order).
     *
     * Problem Constraints
     * 1 <= length of the array <= 100000
     * 1 <= A[i] <= 109
     *
     * Input Format
     * The only argument given is the integer array A.
     * Output Format
     * Return an integer array C, C[i] denotes the median of the first i elements.
     *
     * Example Input
     * Input 1:
     *  A = [1, 2, 5, 4, 3]
     * Output 1:
     *  [1, 1, 2, 2, 3]
     * Explanation 1:
     *  stream          median
     *  [1]             1
     *  [1, 2]          1
     *  [1, 2, 5]       2
     *  [1, 2, 5, 4]    2
     *  [1, 2, 5, 4, 3] 3
     *
     * Input 2:
     *  A = [5, 17, 100, 11]
     * Output 2:
     *  [5, 5, 17, 11]
     * Explanation 2:
     *  stream          median
     *  [5]              5
     *  [5, 17]          5
     *  [5, 17, 100]     17
     *  [5, 17, 100, 11] 11
     * */

    public static void main(String[] args) {
        int[] input = {1, 2, 5, 4, 3};
        int[] answer = solveRunningMedian(input);
        for(int i = 0; i < answer.length; i++) {
            System.out.printf(answer[i] + " ");
        }
    }

    //To solve this problem, you can use a min-heap and a max-heap to maintain the elements of the stream in sorted order.
    // The min-heap will store the larger half of the elements, and the max-heap will store the smaller half.
    // The root of the max-heap will always be the maximum element of the smaller half, and the root of the min-heap will always be the minimum element of the larger half.


    static PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder()); // max-heap for smaller half
    static PriorityQueue<Integer> minHeap = new PriorityQueue<>(); // min-heap for larger half
    private static int[] solveRunningMedian(int[] input) {
        int[] ans = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            add(input[i]);
            ans[i] = findMedian();
        }
        return ans;
    }

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
}

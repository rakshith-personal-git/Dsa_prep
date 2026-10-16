package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Solutions for LeetCode 253 / Toast Peak Active Resource Allocation (Meeting Rooms II).
 * 
 * Time Complexity:  O(N log N)
 * Space Complexity: O(N)
 */
public class MeetingRoomsII {

    // ========================================================
    // APPROACH 1: Min-Heap (Priority Queue of End Times)
    // ========================================================
    public static int minMeetingRoomsHeap(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // Sort intervals by start time ascending
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        // Min-heap stores end times of active meetings
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        // Add end time of the first meeting
        minHeap.offer(intervals[0][1]);

        for (int i = 1; i < intervals.length; i++) {
            // If earliest ending meeting has finished before/at current start time, reuse room
            if (intervals[i][0] >= minHeap.peek()) {
                minHeap.poll();
            }

            // Push current meeting's end time
            minHeap.offer(intervals[i][1]);
        }

        // Heap size represents minimum rooms required
        return minHeap.size();
    }


    // ========================================================
    // APPROACH 2: Two-Pointer Chronological Sweep (Optimal)
    // ========================================================
    public static int minMeetingRoomsTwoPointers(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        int n = intervals.length;
        int[] starts = new int[n];
        int[] ends = new int[n];

        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }

        Arrays.sort(starts);
        Arrays.sort(ends);

        int usedRooms = 0;
        int maxRooms = 0;

        int startPtr = 0;
        int endPtr = 0;

        while (startPtr < n) {
            if (starts[startPtr] < ends[endPtr]) {
                // A meeting started before the earliest meeting finished -> need a new room
                usedRooms++;
                startPtr++;
            } else {
                // A meeting finished -> room freed up
                usedRooms--;
                endPtr++;
            }
            maxRooms = Math.max(maxRooms, usedRooms);
        }

        return maxRooms;
    }

    // --- Main Method with Test Cases ---
    public static void main(String[] args) {
        // Test Case 1: Standard overlapping intervals
        int[][] test1 = {{0, 30}, {5, 10}, {15, 20}};
        runTest("Test Case 1 (Standard)", test1, 2);

        // Test Case 2: Consecutive non-overlapping intervals
        int[][] test2 = {{7, 10}, {2, 4}};
        runTest("Test Case 2 (Non-Overlapping)", test2, 1);

        // Test Case 3: Exact boundary touching ([5, 10] and [10, 15])
        int[][] test3 = {{1, 5}, {5, 10}, {10, 15}};
        runTest("Test Case 3 (Boundary Touch)", test3, 1);

        // Test Case 4: Fully nested intervals
        int[][] test4 = {{1, 10}, {2, 9}, {3, 8}, {4, 7}};
        runTest("Test Case 4 (Nested Intervals)", test4, 4);
    }

    private static void runTest(String testName, int[][] intervals, int expected) {
        int resHeap = minMeetingRoomsHeap(intervals);
        int resTwoPtr = minMeetingRoomsTwoPointers(intervals);

        if (resHeap == expected && resTwoPtr == expected) {
            System.out.println(testName + " PASSED -> Rooms Required: " + expected);
        } else {
            System.out.println(testName + " FAILED -> Expected: " + expected + 
                               ", Got Heap: " + resHeap + ", TwoPtr: " + resTwoPtr);
        }
    }
}
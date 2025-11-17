package DSA_Interview_Questions.Grind150;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static DSA_Interview_Questions.Heap.KClosestPointsToOrigin.printAns;
import static DSA_Interview_Questions.StackAndQueues.MergeIntervals.mergeUsingStack;

public class BO_InsertInterval {
    /**
     * You are given an array of non-overlapping intervals
     * where intervals[i] = [starti, endi] represent the start and the end of the ith interval
     * and intervals is sorted in ascending order by starti.
     * You are also given an interval newInterval = [start, end] that represents the start and end of another interval.
     * <p>
     * Insert newInterval into intervals such that intervals is still sorted in ascending order by starti
     * and intervals still does not have any overlapping intervals (merge overlapping intervals if necessary).
     * <p>
     * Return intervals after the insertion.
     * Note that you don't need to modify intervals in-place. You can make a new array and return it.
     * <p>
     * Example 1:
     * Input: intervals = [[1,3],[6,9]], newInterval = [2,5]
     * Output: [[1,5],[6,9]]
     * <p>
     * Example 2:
     * Input: intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
     * Output: [[1,2],[3,10],[12,16]]
     * Explanation: Because the new interval [4,8] overlaps with [3,5],[6,7],[8,10].
     * <p>
     * Constraints:
     * 0 <= intervals.length <= 104
     * intervals[i].length == 2
     * 0 <= starti <= endi <= 105
     * intervals is sorted by starti in ascending order.
     * newInterval.length == 2
     * 0 <= start <= end <= 105
     **/

    public static void main(String[] args) {
        int[][] intervals = {{1, 3}, {6, 9}};
        int[] newInterval = {2, 5};
        int[][] ans = insert(intervals, newInterval);
        int[][] ans2 = insert2(intervals, newInterval);
        printAns(ans);
        printAns(ans2);
    }


    private static int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();

        for (int[] interval : intervals) {
            //if newInterval is before this interval , then update interval as newInterval
            if (newInterval[1] < interval[0]) {
                result.add(newInterval);
                newInterval = interval;
            } else if (interval[1] < newInterval[0]) { //if interval is lesser than new Interval insert interval
                result.add(interval);
            }else {
                // if above conditions fail its an overlap since possibility of new interval existing in left & right of interval is checked
                //  update lowest of start & highest of end & not insert
                newInterval[0] = Math.min(newInterval[0], interval[0]);
                newInterval[1] = Math.max(newInterval[1], interval[1]);
            }
        }
        result.add(newInterval);
        return result.toArray(new int[result.size()][]);
    }

    public static int[][] insert2(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0;

        // Add all intervals ending before the new interval starts
        while (i < intervals.length && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i]);
            i++;
        }

        // Merge all overlapping intervals into one considering the new interval
        while (i < intervals.length && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        result.add(newInterval); // Add the merged interval

        // Add all remaining intervals
        while (i < intervals.length) {
            result.add(intervals[i]);
            i++;
        }

        return result.toArray(new int[result.size()][]);
    }
    //TC is O(N)
    //SC is O(N)

}

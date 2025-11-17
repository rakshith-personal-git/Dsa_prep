package DSA_Interview_Questions.Grind150;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

public class CH_MergeIntervals {

    /**
     * Given an array of intervals where intervals[i] = [starti, endi], merge all overlapping intervals, and return an array of the non-overlapping intervals that cover all the intervals in the input.
     * <p>
     * Example 1:
     * Input: intervals = [[1,3],[2,6],[8,10],[15,18]]
     * Output: [[1,6],[8,10],[15,18]]
     * Explanation: Since intervals [1,3] and [2,6] overlap, merge them into [1,6].
     * <p>
     * Example 2:
     * Input: intervals = [[1,4],[4,5]]
     * Output: [[1,5]]
     * Explanation: Intervals [1,4] and [4,5] are considered overlapping.
     * <p>
     * Constraints:
     * 1 <= intervals.length <= 104
     * intervals[i].length == 2
     * 0 <= starti <= endi <= 104
     */

    public static void main(String[] args) {
        int[][] intervals = {{1, 3}, {2, 6}, {8, 10}, {15, 18}};
        //int[][] intervals = {{1,4}, {4, 5}};
        int[][] ans = mergeUsingStack(intervals);
        int[][] ans2 = mergeUsingDeque(intervals);
        print(ans);
        System.out.println("");
        print(ans2);

    }

    private static int[][] mergeUsingDeque(int[][] intervals) {
        int n = intervals.length;
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));  // TC is O(NlogN) since inBuilt sort function

        Deque<int[]> deque = new LinkedList<>();
        deque.push(intervals[0]);

        // i = 1 since we have already pushed the 0th int[] array to the deque
        for (int i = 1; i < n; i++) {
            int[] currentTop = deque.peek();
            int[] nextElement = intervals[i];

            if (currentTop[1] >= nextElement[0]) {
                currentTop[1] = Math.max(currentTop[1], nextElement[1]);
            } else {
                deque.push(nextElement); // non-overlapping element
            }
        }

        int[][] ans = new int[deque.size()][2];
        // populating in reverse order since deque is LIFO
        for (int i = ans.length - 1; i >= 0; i--) {
            int[] topElement = deque.pop();
            ans[i][0] = topElement[0];
            ans[i][1] = topElement[1];
        }

        return ans;
    }

    public static int[][] mergeUsingStack(int[][] intervals) {
        int n = intervals.length;
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));  // TC is O(NlogN) since inBuilt sort function

        Stack<int[]> st = new Stack<>();
        st.push(intervals[0]);

        //i = 1 since we have already pushed 0th int[] array to stack
        for (int i = 1; i < n; i++) {
            int[] currentTop = st.peek();
            int[] nextElement = intervals[i];

            if (currentTop[1] >= nextElement[0]) {
                currentTop[1] = Math.max(currentTop[1], nextElement[1]);
            } else {
                st.push(nextElement); // non - overlapping element
            }
        }

        int[][] ans = new int[st.size()][2];
        //populating in reverse order since stack is FILO
        for (int i = ans.length - 1; i >= 0; i--) {
            int[] topElement = st.pop();
            ans[i][0] = topElement[0];
            ans[i][1] = topElement[1];
        }

        return ans;

    }


    public static void print(int[][] ans) {
        for (int[] arr : ans) {
            System.out.print("[");
            for (int i : arr) {
                System.out.print(i + ",");
            }
            System.out.print("]  ");
        }
    }
}

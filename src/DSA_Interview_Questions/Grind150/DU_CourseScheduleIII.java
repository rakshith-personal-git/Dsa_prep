package DSA_Interview_Questions.Grind150;

import java.util.Arrays;
import java.util.PriorityQueue;

public class DU_CourseScheduleIII {
    /**
     * There are n different online courses numbered from 1 to n. You are given an array courses where courses[i] = [durationi, lastDayi]
     * indicate that the ith course should be taken continuously for durationi days and must be finished before or on lastDayi.
     *
     * You will start on the 1st day and you cannot take two or more courses simultaneously.
     * Return the maximum number of courses that you can take.
     *
     * Example 1:
     * Input: courses = [[100,200],[200,1300],[1000,1250],[2000,3200]]
     * Output: 3
     * Explanation:
     * There are totally 4 courses, but you can take 3 courses at most:
     * First, take the 1st course, it costs 100 days so you will finish it on the 100th day, and ready to take the next course on the 101st day.
     * Second, take the 3rd course, it costs 1000 days so you will finish it on the 1100th day, and ready to take the next course on the 1101st day.
     * Third, take the 2nd course, it costs 200 days so you will finish it on the 1300th day.
     * The 4th course cannot be taken now, since you will finish it on the 3300th day, which exceeds the closed date.
     *
     * Example 2:
     * Input: courses = [[1,2]]
     * Output: 1
     *
     * Example 3:
     * Input: courses = [[3,2],[4,3]]
     * Output: 0
     *
     * Constraints:
     * 1 <= courses.length <= 104
     * 1 <= durationi, lastDayi <= 104
     * */

    public static void main(String[] args) {
        int[][] courses = {{100,200}, {200,1300}, {1000,1250}, {2000,3200}};
        int ans = scheduleCourse(courses);
        System.out.println(ans);
    }

    private static int scheduleCourse(int[][] courses) {
        // Sort the courses by their end days
        Arrays.sort(courses, (a, b) -> a[1] - b[1]);

        // Priority queue to simulate a max-heap using negative values
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
        int totalTime = 0;

        for (int[] course : courses) {
            int duration = course[0];
            int lastDay = course[1];

            if (totalTime + duration <= lastDay) {
                // If we can take this course without exceeding the deadline
                maxHeap.offer(duration);
                totalTime += duration;
            } else if (!maxHeap.isEmpty() && maxHeap.peek() > duration) {
                // If taking the current course exceeds the deadline, but it has a shorter duration
                // than the course with the longest duration we've taken so far
                totalTime += duration - maxHeap.poll();
                maxHeap.offer(duration);
            }
        }

        return maxHeap.size();
    }
}

package DSA_Interview_Questions.Grind150;

import java.util.Arrays;

public class DD_TaskScheduler {
    /**
     * You are given an array of CPU tasks, each represented by letters A to Z, and a cooling time, n.
     * Each cycle or interval allows the completion of one task. Tasks can be completed in any order, but there's a constraint:
     * identical tasks must be separated by at least n intervals due to cooling time.
     * Return the minimum number of intervals required to complete all tasks.
     *
     * Example 1:
     * Input: tasks = ["A","A","A","B","B","B"], n = 2
     * Output: 8
     * Explanation: A possible sequence is: A -> B -> idle -> A -> B -> idle -> A -> B.
     * After completing task A, you must wait two cycles before doing A again. The same applies to task B.
     * In the 3rd interval, neither A nor B can be done, so you idle. By the 4th cycle, you can do A again as 2 intervals have passed.
     *
     * Example 2:
     * Input: tasks = ["A","C","A","B","D","B"], n = 1
     * Output: 6
     * Explanation: A possible sequence is: A -> B -> C -> D -> A -> B.
     * With a cooling interval of 1, you can repeat a task after just one other task.
     *
     * Example 3:
     * Input: tasks = ["A","A","A", "B","B","B"], n = 3
     * Output: 10
     * Explanation: A possible sequence is: A -> B -> idle -> idle -> A -> B -> idle -> idle -> A -> B.
     * There are only two types of tasks, A and B, which need to be separated by 3 intervals. This leads to idling twice between repetitions of these tasks.
     *
     * Constraints:
     * 1 <= tasks.length <= 104
     * tasks[i] is an uppercase English letter.
     * 0 <= n <= 100
     * */

    public static void main(String[] args) {
        char[] tasks = {'A','A','A', 'B','B','B'};
        int n = 3;
        System.out.println(leastInterval(tasks, n));
        System.out.println(leastIntervalReadable(tasks, n));
    }

    private static int leastIntervalReadable(char[] tasks, int n) {
        int[] charFrequency = new int[26];
        for (char c : tasks) {
            charFrequency[c - 'A']++;
        }

        Arrays.sort(charFrequency);
        int maxFreq = charFrequency[25] - 1;
        int idleSlots = maxFreq * n;

        for (int i = 24; i >= 0; i--) {
            idleSlots -= Math.min(charFrequency[i], maxFreq);
        }

        if (idleSlots > 0) {
            return idleSlots + tasks.length;
        } else {
            return tasks.length;
        }

    }

    private static int leastInterval(char[] tasks, int n) {
        int[] frequency = new int[26];
        int maxFrequency = 0;
        int tasksWithMaxFrequency = 0;
        for (char c : tasks) {
            frequency[c - 'A']++;
            maxFrequency = Math.max(maxFrequency, frequency[c - 'A']);
        }

        for (int freq : frequency) {
            if (freq == maxFrequency) {
                tasksWithMaxFrequency++;
            }
        }

        int partCount = maxFrequency - 1; //The reason for subtracting 1 from max is that the maximum frequency task doesn't need to have intervals after it
        int partLength = n - (tasksWithMaxFrequency - 1); //same reason for subtracting 1
        int emptySlots = partCount * partLength;
        int availableTasks = tasks.length - (maxFrequency * tasksWithMaxFrequency);
        int idlesRequired = Math.max(0, emptySlots - availableTasks);
        return tasks.length + idlesRequired;
    }


}

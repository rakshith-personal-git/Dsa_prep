package DSA_Interview_Questions.Grind150;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;

public class DQ_MaximumProfitInJobScheduling {
    /**
     * We have n jobs, where every job is scheduled to be done from startTime[i] to endTime[i], obtaining a profit of profit[i].
     * You're given the startTime, endTime and profit arrays, return the maximum profit you can take such that there are no two jobs in the subset with overlapping time range.
     * If you choose a job that ends at time X you will be able to start another job that starts at time X.
     * <p>
     * Example 1:
     * Input: startTime = [1,2,3,3], endTime = [3,4,5,6], profit = [50,10,40,70]
     * Output: 120
     * Explanation: The subset chosen is the first and fourth job.
     * Time range [1-3]+[3-6] , we get profit of 120 = 50 + 70.
     * <p>
     * Example 2:
     * Input: startTime = [1,2,3,4,6], endTime = [3,5,10,6,9], profit = [20,20,100,70,60]
     * Output: 150
     * Explanation: The subset chosen is the first, fourth and fifth job.
     * Profit obtained 150 = 20 + 70 + 60.
     * <p>
     * Example 3:
     * Input: startTime = [1,1,1], endTime = [2,3,4], profit = [5,6,4]
     * Output: 6
     * <p>
     * Constraints:
     * 1 <= startTime.length == endTime.length == profit.length <= 5 * 104
     * 1 <= startTime[i] < endTime[i] <= 109
     * 1 <= profit[i] <= 104
     */
    public static void main(String[] args) {
//        int[] startTime = {1, 2, 3, 3};
//        int[] endTime = {3, 4, 5, 6};
//        int[] profit = {50, 10, 40, 70};
        int[] startTime = {1, 2, 3, 4, 6};
        int[] endTime = {3, 5, 10, 6, 9};
        int[] profit = {20, 20, 100, 70, 60};
        System.out.println(jobScheduling(startTime, endTime, profit));
    }

    private static int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        int n = profit.length;

        // Create a 2D array 'jobs' to store information about each job
        int[][] jobs = new int[n][3];

        for (int i = 0; i < n; i++) {
            // Populate 'jobs' with start times, end times, and profits
            jobs[i] = new int[]{startTime[i], endTime[i], profit[i]};
        }

        Arrays.sort(jobs, Comparator.comparingInt(a -> a[1])); //sorting based on endTime... can be also written as (a, b) -> a[1] - b[1]

        TreeMap<Integer, Integer> map = new TreeMap<>(); // Use a TreeMap to store the dynamic programming states (end time -> maximum profit)

        map.put(0, 0); // Initialize the TreeMap with a key-value pair representing no jobs scheduled

        for (int[] job : jobs) {
            //Calculate the current profit by adding the current job's profit to the maximum profit until the end time of the previous job
            int value = job[2] + map.floorEntry(job[0]).getValue();

            // Check if the current profit is greater than the maximum profit obtained so far
            if (value > map.lastEntry().getValue()) {
                map.put(job[1], value);
            }
        }

        return map.lastEntry().getValue(); // Return the maximum profit achievable by considering all jobs
    }
}

package DSA_Interview_Questions.Grind150;

import java.util.Arrays;
import java.util.*;

public class DZ_TeamFormation {
    //https://leetcode.com/discuss/post/5177883/fivetran-online-assessment-team-formatio-25qu/

    /**
     * You have an array score[] of length n.
     * You must pick exactly team_size employees.
     * For each pick:
     * Look at the first k available employees from the left and the last k available employees from the right (if fewer than k remain on a side, take all on that side).
     * From the union of these two groups, choose the employee with the highest score; if there is a tie, choose the one with the lower index in the current array (i.e., the leftmost).
     * Remove that employee from the list.
     * Return the sum of the selected scores.
     * Constraints: n up to 1e5, score[i] up to 1e9, so we need about O(n log k) or O(n log n) time and O(n) memory.
     **/

    // Pair class to store score and index
    private static class Employee {
        int score;
        int index;

        public Employee(int score, int index) {
            this.score = score;
            this.index = index;
        }
    }

    public static long teamFormation(int[] score, int teamSize, int k) {
        int n = score.length;

        Comparator<Employee> comparator = (a, b) -> {
            if(a.score != b.score) {
                return b.score - a.score;
            } else {
                return a.index - b.index;
            }
        };

        PriorityQueue<Employee> leftPq = new PriorityQueue<>(comparator);
        PriorityQueue<Employee> rightPq = new PriorityQueue<>(comparator);

        int l = 0, r = n-1;
        long sum = 0;

        //left window
        int count = 0;
        while (count < k && l <= r) {
            leftPq.offer(new Employee(score[l], l));
            l++;
            count++;
        }

        count = 0;
        while (count < k && l <= r) {
            rightPq.offer(new Employee(score[r], r));
            r--;
            count++;
        }


        for (int picked = 0; picked < teamSize; picked++) {
            Employee employeeChosen;
            boolean fromLeft;

            if(leftPq.isEmpty() && rightPq.isEmpty()) {
                break;
            } else if (rightPq.isEmpty()) {
                employeeChosen = leftPq.poll();
                fromLeft = true;
            } else if (leftPq.isEmpty()) {
                employeeChosen = rightPq.poll();
                fromLeft = false;
            } else {
                Employee leftTop = leftPq.peek();
                Employee rightTop = rightPq.peek();

                // IMPORTANT: leftTop is better if cmp(leftTop, rightTop) <= 0
                if (comparator.compare(leftTop, rightTop) <= 0) {
                    employeeChosen = leftPq.poll();
                    fromLeft = true;
                } else {
                    employeeChosen = rightPq.poll();
                    fromLeft = false;
                }
            }

            sum += employeeChosen.score;

            // Replenish from the selected side
            if (fromLeft) {
                if (l <= r) {
                    leftPq.offer(new Employee(score[l], l));
                    l++;
                }
            } else {
                if (l <= r) {
                    rightPq.offer(new Employee(score[r], r));
                    r--;
                }
            }
        }
        return sum;
    }


    // Simple helper to run one test
    private static void runTest(int[] score, int teamSize, int k, long expected) {
        long actual = teamFormation(score, teamSize, k);
        System.out.println("score = " + Arrays.toString(score)
                + ", teamSize = " + teamSize + ", k = " + k);
        System.out.println("Expected: " + expected + ", Actual: " + actual);
        System.out.println(actual == expected ? "PASS" : "FAIL");
        System.out.println("----");
    }

    public static void main(String[] args) {
        // Test cases from screenshots / statement

        // Example: [10, 20, 10, 15, 5, 30, 20], teamSize=2, k=3 => 50
        runTest(new int[]{10, 20, 10, 15, 5, 30, 20}, 2, 3, 50);

        // Sample Case 0 from images:
        // score = [17, 12, 10, 2, 7, 2, 11, 20, 8], team_size = 3, k = 4 => 49
        runTest(new int[]{17, 12, 10, 2, 7, 2, 11, 20, 8}, 3, 4, 49);

        // Another described sample:
        // score = [17, 12, 10, 2, 7, 2, 11, 20, 8], team_size = 3, k = 4 => 49 again
        runTest(new int[]{17, 12, 10, 2, 7, 2, 11, 20, 8}, 3, 4, 49);

        // Case: n == teamSize; should sum all
        runTest(new int[]{6, 18, 8, 14, 10, 12, 18, 9}, 8, 3, 95);

        // Case: team_size < n, k = 1
        // score = [10, 20, 10, 15, 5], teamSize = 2, k = 1
        // Picks 10 (from left) vs 5 (from right) -> 10, list [20,10,15,5];
        // then 20 vs 5 -> 20; total 30
        runTest(new int[]{10, 20, 10, 15, 5}, 2, 1, 30);

        // Case: k >= n
        // Everything is in the window; picking top 3
        runTest(new int[]{5, 1, 9, 9, 2}, 3, 10, 9 + 9 + 5);

        // Case: many equal scores
        runTest(new int[]{5, 5, 5, 5}, 2, 2, 10);
    }

}

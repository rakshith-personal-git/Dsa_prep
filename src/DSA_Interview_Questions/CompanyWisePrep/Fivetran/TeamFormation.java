package DSA_Interview_Questions.CompanyWisePrep.Fivetran;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * ============================================================================
 * PROBLEM STATEMENT: Team Formation
 * ============================================================================
 * You are given an array of employee scores, 'score', an integer 'team_size',
 * and an integer 'k'. Select 'team_size' employees based on their evaluation 
 * scores according to the following selection rules:
 *
 * Rules:
 * 1. Select the employee with the highest score from either the first 'k'
 *    employees or the last 'k' employees in the remaining score list.
 * 2. If multiple employees have the same highest score, select the one with 
 *    the lowest original index.
 * 3. If there are fewer than 'k' employees in total remaining, consider the 
 *    entire remaining list for selection.
 * 4. Remove the selected employee from the list.
 * 5. Repeat until a team of size 'team_size' is formed.
 *
 * Returns:
 *   long: The total sum of the scores of all selected team members.
 *
 * Constraints:
 *   1 <= n <= 10^5
 *   1 <= score[i] <= 10^9
 *   1 <= team_size <= n
 *   1 <= k <= n
 *
 * ============================================================================
 * APPROACH & SDE-3 DESIGN RATIONALE
 * ============================================================================
 * Approach: Two Max-Heaps (Priority Queues)
 *
 * 1. Maintain two Max-Heaps:
 *    - 'pqLeft': Stores up to 'k' candidate elements from the left boundary.
 *    - 'pqRight': Stores up to 'k' candidate elements from the right boundary.
 *    - Each candidate is stored as an array: int[]{scoreValue, originalIndex}.
 *
 * 2. Pointer Management:
 *    - 'left' pointer starts at 0, moving rightward.
 *    - 'right' pointer starts at n - 1, moving leftward.
 *
 * 3. Selection Loop ('team_size' iterations):
 *    - Peek at the maximum elements from 'pqLeft' and 'pqRight'.
 *    - Pick the one with the strictly higher score.
 *    - In case of a tie in score, pick from 'pqLeft' because elements in 
 *      'pqLeft' strictly came from lower indices than 'pqRight'.
 *    - After picking, replenish the chosen queue with the next element from
 *      the remaining array (if 'left <= right').
 *
 * Complexity:
 *   - Time Complexity: O(team_size * log(k))
 *     Each of the 'team_size' picks involves logarithmic heap insertions/polls
 *     on heaps of size at most 'k'.
 *   - Space Complexity: O(k)
 *     At any point, the two heaps store at most 2 * k elements.
 * ============================================================================
 */
public class TeamFormation {

    /**
     * Complete the 'teamFormation' function below.
     */
    public static long teamFormation(List<Integer> score, int team_size, int k) {
        if (score == null || score.isEmpty() || team_size <= 0) {
            return 0L;
        }

        int n = score.size();

        // Max-heaps ordering by score descending; tie-break by index ascending.
        // Element structure: int[]{scoreValue, originalIndex}
        PriorityQueue<int[]> pqLeft = new PriorityQueue<>((a, b) -> {
            if (a[0] != b[0]) {
                return Integer.compare(b[0], a[0]); // Max score first
            }
            return Integer.compare(a[1], b[1]);    // Min index first
        });

        PriorityQueue<int[]> pqRight = new PriorityQueue<>((a, b) -> {
            if (a[0] != b[0]) {
                return Integer.compare(b[0], a[0]); // Max score first
            }
            return Integer.compare(a[1], b[1]);    // Min index first
        });

        int left = 0;
        int right = n - 1;

        // Populate initial left pool (up to k elements)
        while (left <= right && pqLeft.size() < k) {
            pqLeft.add(new int[]{score.get(left), left});
            left++;
        }

        // Populate initial right pool (up to k elements, non-overlapping)
        while (left <= right && pqRight.size() < k) {
            pqRight.add(new int[]{score.get(right), right});
            right--;
        }

        long totalScoreSum = 0L;

        // Select team_size employees iteratively
        for (int i = 0; i < team_size; i++) {
            if (pqLeft.isEmpty()) {
                int[] selected = pqRight.poll();
                totalScoreSum += selected[0];
                if (left <= right) {
                    pqRight.add(new int[]{score.get(right), right});
                    right--;
                }
            } else if (pqRight.isEmpty()) {
                int[] selected = pqLeft.poll();
                totalScoreSum += selected[0];
                if (left <= right) {
                    pqLeft.add(new int[]{score.get(left), left});
                    left++;
                }
            } else {
                int[] topLeft = pqLeft.peek();
                int[] topRight = pqRight.peek();

                // Compare maximum scores from both candidates
                if (topLeft[0] > topRight[0]) {
                    totalScoreSum += pqLeft.poll()[0];
                    if (left <= right) {
                        pqLeft.add(new int[]{score.get(left), left});
                        left++;
                    }
                } else if (topRight[0] > topLeft[0]) {
                    totalScoreSum += pqRight.poll()[0];
                    if (left <= right) {
                        pqRight.add(new int[]{score.get(right), right});
                        right--;
                    }
                } else {
                    // Tie in score: pqLeft elements always have lower indices
                    totalScoreSum += pqLeft.poll()[0];
                    if (left <= right) {
                        pqLeft.add(new int[]{score.get(left), left});
                        left++;
                    }
                }
            }
        }

        return totalScoreSum;
    }

    // ========================================================================
    // MAIN METHOD & TEST SUITE
    // ========================================================================
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("          RUNNING TEAM FORMATION TESTS            ");
        System.out.println("==================================================\n");

        runTestCase(
                "Example 1",
                Arrays.asList(10, 20, 10, 15, 5, 30, 20),
                2,
                3,
                50L
        );

        runTestCase(
                "Sample Case 0",
                Arrays.asList(17, 12, 10, 2, 7, 2, 11, 20, 8),
                3,
                4,
                49L
        );

        runTestCase(
                "Sample Case 1 (All elements selected)",
                Arrays.asList(6, 18, 8, 14, 10, 12, 18, 9),
                8,
                3,
                95L
        );

        runTestCase(
                "Sample Case 2 (k = 1)",
                Arrays.asList(18, 5, 15, 18, 11, 15, 9, 7),
                5,
                1,
                60L
        );

        runTestCase(
                "Edge Case: team_size == 1",
                Arrays.asList(1, 2, 3, 4, 5),
                1,
                2,
                5L
        );
    }

    private static void runTestCase(String name, List<Integer> score, int teamSize, int k, long expected) {
        long actual = teamFormation(score, teamSize, k);
        boolean passed = (actual == expected);

        System.out.printf("[%s] %s\n", passed ? "PASS" : "FAIL", name);
        System.out.println("  Input Score List : " + score);
        System.out.println("  team_size        : " + teamSize);
        System.out.println("  k                : " + k);
        System.out.println("  Expected Output  : " + expected);
        System.out.println("  Actual Output    : " + actual);
        System.out.println("--------------------------------------------------\n");
    }
}
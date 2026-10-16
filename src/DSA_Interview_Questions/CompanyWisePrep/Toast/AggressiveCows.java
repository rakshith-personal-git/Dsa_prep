package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.Arrays;

public class AggressiveCows {
    //LeetCode Equivalent
    //LeetCode 1552: Magnetic Force Between Two Balls (Medium)

    /**
     * Finds the maximum possible minimum distance between any two cows.
     *
     * @param stalls Array of stall coordinates.
     * @param cows Number of cows to place.
     * @return Maximum minimum distance integer.
     */
    public static int maxDistance(int[] stalls, int cows) {
        if (stalls == null || stalls.length < cows || cows < 2) {
            return 0;
        }

        // Step 1: Sort stalls in ascending order to enable greedy placement
        Arrays.sort(stalls);

        int n = stalls.length;
        int low = 1; // Minimum possible distance
        int high = stalls[n - 1] - stalls[0]; // Maximum possible distance between first and last stall
        int result = 0;

        // Step 2: Binary Search on Answer Range [low, high]
        while (low <= high) {
            int mid = low + (high - low) / 2;

            // Check if it is feasible to place all 'cows' with at least 'mid' distance
            if (canPlaceCows(stalls, cows, mid)) {
                result = mid;     // Record valid candidate distance
                low = mid + 1;    // Try searching for a larger valid minimum distance
            } else {
                high = mid - 1;   // Distance too large; try a smaller distance
            }
        }

        return result;
    }

    /**
     * Helper method to greedily check if 'cows' can be placed with at least 'dist' gap.
     */
    private static boolean canPlaceCows(int[] stalls, int cows, int dist) {
        int count = 1; // Place 1st cow at the first stall (stalls[0])
        int lastPosition = stalls[0];

        for (int i = 1; i < stalls.length; i++) {
            if (stalls[i] - lastPosition >= dist) {
                count++;
                lastPosition = stalls[i];

                // Early exit if all cows have been successfully placed
                if (count == cows) {
                    return true;
                }
            }
        }

        return count >= cows;
    }

    // --- Main Method with Executable IntelliJ Test Cases ---
    public static void main(String[] args) {
        // Test Case 1: Prompt Example (stalls = [0,4,3,7,10,9], cows = 3 -> Expected: 4)
        int[] stalls1 = {0, 4, 3, 7, 10, 9};
        runTest("Test 1 (Prompt Example)", stalls1, 3, 4);

        // Test Case 2: Classic SPOJ Example (stalls = [1, 2, 8, 4, 9], cows = 3 -> Expected: 3)
        int[] stalls2 = {1, 2, 8, 4, 9};
        runTest("Test 2 (Classic SPOJ Case)", stalls2, 3, 3);

        // Test Case 3: 2 Cows with Large Distance (stalls = [1, 100], cows = 2 -> Expected: 99)
        int[] stalls3 = {1, 100};
        runTest("Test 3 (2 Cows Max Range)", stalls3, 2, 99);

        // Test Case 4: Cows equal to number of stalls (stalls = [1, 2, 3, 4], cows = 4 -> Expected: 1)
        int[] stalls4 = {1, 2, 3, 4};
        runTest("Test 4 (Cows equal Stalls)", stalls4, 4, 1);
    }

    private static void runTest(String testName, int[] stalls, int cows, int expected) {
        int result = maxDistance(stalls, cows);
        if (result == expected) {
            System.out.println(testName + " PASSED -> Output: " + result);
        } else {
            System.out.println(testName + " FAILED -> Expected: " + expected + ", Got: " + result);
        }
    }
}

/*
 * ============================================================================
 * COMPLEXITY ANALYSIS:
 * ============================================================================
 * Time Complexity: O(N log N + N log D)
 *   Where N is the number of stalls and D = (stalls[N-1] - stalls[0]) is the 
 *   range of stall coordinates.
 *   - Sorting the stalls array takes O(N log N) time.
 *   - Binary search range runs log2(D) iterations. Each iteration executes 
 *     canPlaceCows which takes O(N) linear time.
 *
 * Space Complexity: O(1) Auxiliary Space
 *   Only primitive scalar variables (low, high, mid, result, count, lastPosition) 
 *   are used. Sorting in Java dual-pivot Dual-Pivot Quicksort uses O(log N) 
 *   stack space.
 * ============================================================================
 */
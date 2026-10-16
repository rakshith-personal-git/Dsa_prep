package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildingsWithOceanView {

    /**
     * Finds indices of buildings that have an unobstructed ocean view (ocean is to the right).
     *
     * @param heights Array of building heights.
     * @return Array of building indices with an ocean view, sorted in ascending order.
     */
    public static int[] findBuildings(int[] heights) {
        if (heights == null || heights.length == 0) {
            return new int[0];
        }

        int n = heights.length;
        List<Integer> viewIndices = new ArrayList<>();
        int maxHeight = -1;

        // Step 1: Traverse backwards from right to left (ocean is at index n)
        for (int i = n - 1; i >= 0; i--) {
            // A building has a view if it is strictly taller than any building to its right
            if (heights[i] > maxHeight) {
                viewIndices.add(i);
                maxHeight = heights[i]; // Update running maximum height obstruction
            }
        }

        // Step 2: Convert descending indices list to ascending output array
        int size = viewIndices.size();
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = viewIndices.get(size - 1 - i);
        }

        return result;
    }

    // --- Main Method with Executable IntelliJ Test Cases ---
    public static void main(String[] args) {
        // Test Case 1: Strictly increasing heights (Prompt Example)
        int[] test1 = {1, 2, 3, 4};
        runTest("Test 1 (Strictly Increasing)", test1, new int[]{3});

        // Test Case 2: Mixed heights (Standard Case)
        int[] test2 = {4, 2, 3, 1};
        runTest("Test 2 (Mixed Heights)", test2, new int[]{0, 2, 3});

        // Test Case 3: Strictly decreasing heights (All have ocean view)
        int[] test3 = {4, 3, 2, 1};
        runTest("Test 3 (Strictly Decreasing)", test3, new int[]{0, 1, 2, 3});

        // Test Case 4: Equal heights (Equal height blocks view of buildings to left)
        int[] test4 = {2, 2, 2, 2};
        runTest("Test 4 (Equal Heights)", test4, new int[]{3});

        // Test Case 5: Single Building
        int[] test5 = {10};
        runTest("Test 5 (Single Building)", test5, new int[]{0});
    }

    private static void runTest(String testName, int[] heights, int[] expected) {
        int[] result = findBuildings(heights);
        if (Arrays.equals(result, expected)) {
            System.out.println(testName + " PASSED -> Output: " + Arrays.toString(result));
        } else {
            System.out.println(testName + " FAILED -> Expected: " + Arrays.toString(expected) + ", Got: " + Arrays.toString(result));
        }
    }
}

/*
 * ============================================================================
 * COMPLEXITY ANALYSIS:
 * ============================================================================
 * Time Complexity: O(N)
 *   Where N is the number of buildings in the heights array. A single right-to-left
 *   pass visits all N elements once. Reversing the output list takes O(K) where
 *   K <= N. Overall runtime is linear O(N).
 *
 * Space Complexity: O(1) Auxiliary Space (or O(N) including output array)
 *   Only primitive scalar variables (maxHeight, loop pointers) are allocated 
 *   during execution. The result array scales with the number of valid 
 *   buildings K <= N.
 * ============================================================================
 */
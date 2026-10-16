package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.Arrays;

/**
 * Solution for Toast OA: Count Decreasing Triplets of Officer Heights.
 *
 * <p>Strategy: Fix the middle element j. Count taller elements on the left 
 * and shorter elements on the right using a Fenwick Tree (Binary Indexed Tree) 
 * with Coordinate Compression.
 *
 * <p>Time Complexity: O(N log N)
 * Space Complexity: O(N)
 */
public class ToastDecreasingTriplets {

    // Fenwick Tree (Binary Indexed Tree) implementation for range queries and point updates
    private static class FenwickTree {
        private final int size;
        private final int[] tree;

        public FenwickTree(int size) {
            this.size = size;
            this.tree = new int[size + 1];
        }

        public void add(int index, int delta) {
            for (; index <= size; index += index & -index) {
                tree[index] += delta;
            }
        }

        public int query(int index) {
            int sum = 0;
            for (; index > 0; index -= index & -index) {
                sum += tree[index];
            }
            return sum;
        }

        public void clear() {
            Arrays.fill(tree, 0);
        }
    }

    /**
     * Calculates total number of valid decreasing triplets (Height[i] > Height[j] > Height[k] for i < j < k).
     *
     * @param heights Array of distinct officer heights.
     * @return Total count of valid triplets as long.
     */
    public static long countDecreasingTriplets(int[] heights) {
        if (heights == null || heights.length < 3) {
            return 0L;
        }

        int n = heights.length;

        // Step 1: Coordinate Compression
        // Create sorted copy of distinct heights to map each value to a 1-based rank [1, n]
        int[] sortedHeights = heights.clone();
        Arrays.sort(sortedHeights);

        int[] ranks = new int[n];
        for (int i = 0; i < n; i++) {
            // Arrays.binarySearch returns 0-based index; add 1 for 1-based Fenwick Tree rank
            ranks[i] = Arrays.binarySearch(sortedHeights, heights[i]) + 1;
        }

        FenwickTree bit = new FenwickTree(n);
        int[] greaterLeft = new int[n];
        int[] smallerRight = new int[n];

        // Step 2: Left-to-Right Pass -> Populate greaterLeft[j]
        for (int j = 0; j < n; j++) {
            int rank = ranks[j];
            int elementsOnLeft = j;
            int elementsLessOrEqual = bit.query(rank);
            
            // Taller elements on left = total elements seen so far minus elements <= rank
            greaterLeft[j] = elementsOnLeft - elementsLessOrEqual;

            // Add current element rank to Fenwick Tree
            bit.add(rank, 1);
        }

        // Step 3: Right-to-Left Pass -> Populate smallerRight[j]
        bit.clear();
        for (int j = n - 1; j >= 0; j--) {
            int rank = ranks[j];
            
            // Shorter elements on right = elements with rank < current rank seen so far
            smallerRight[j] = bit.query(rank - 1);

            // Add current element rank to Fenwick Tree
            bit.add(rank, 1);
        }

        // Step 4: Aggregate valid combinations centered at each middle officer j
        long totalTriplets = 0L;
        for (int j = 1; j < n - 1; j++) {
            totalTriplets += (long) greaterLeft[j] * smallerRight[j];
        }

        return totalTriplets;
    }

    //easier approach :- inspired from leetCode 1395. Count Number of Teams
    public static long countDecreasingTriplets2(int[] heights) {
        if (heights == null || heights.length < 3) {
            return 0L;
        }

        int  n = heights.length;
        long result = 0L;

        for (int j = 1; j < n-1; j++) {
            long greaterLeft = 0, smallerRight = 0;
            for (int i = 0; i < j; i++) {
                if  (heights[i] > heights[j]) {
                    greaterLeft++;
                }
            }

            for (int i = j + 1; i < n; i++) {
                if (heights[i] < heights[j]) {
                    smallerRight++;
                }
            }

            result += greaterLeft * smallerRight;
        }
        return result;
    }

    // --- Main Method with Test Cases ---
    public static void main(String[] args) {
        // Sample Test Case from Post 6695493
        int[] sampleInput = {2, 6, 3, 5, 1};
        runTest("Sample Test Case", sampleInput, 2L);

        // Test Case 2: Strictly decreasing array (All triplets valid: C(5, 3) = 10)
        int[] test2 = {5, 4, 3, 2, 1};
        runTest("Test Case 2 (Strictly Decreasing)", test2, 10L);

        // Test Case 3: Strictly increasing array (No valid triplets)
        int[] test3 = {1, 2, 3, 4, 5};
        runTest("Test Case 3 (Strictly Increasing)", test3, 0L);

        // Test Case 4: Array with fewer than 3 elements
        int[] test4 = {10, 5};
        runTest("Test Case 4 (Small Array)", test4, 0L);
    }

    private static void runTest(String testName, int[] heights, long expected) {
        long result = countDecreasingTriplets(heights);
        if (result == expected) {
            System.out.println(testName + " PASSED -> Output: " + result);
        } else {
            System.out.println(testName + " FAILED -> Expected: " + expected + ", Got: " + result);
        }
    }
}
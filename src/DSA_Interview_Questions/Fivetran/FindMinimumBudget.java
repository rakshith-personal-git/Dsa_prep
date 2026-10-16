package DSA_Interview_Questions.Fivetran;

import java.util.Arrays;

/**
 * Minimum Initial Budget to Produce All Products
 * 
 * STRATEGY: Greedy Choice Property (Equivalent to LC 1665)
 * - Sort products by "Slack" (worst[i] - expected[i]) in DESCENDING order.
 * - Perform a single linear pass simulation to accumulate minimum starting budget.
 */
public class FindMinimumBudget {

    /**
     * Calculates the minimum initial budget required to complete all products.
     * 
     * TIME COMPLEXITY:  O(N log N) dominated by sorting.
     * SPACE COMPLEXITY: O(N) for storing product pair array.
     */
    public static long minimumBudget(int[] worst, int[] expected) {
        if (worst == null || expected == null || worst.length == 0) {
            return 0;
        }

        int n = worst.length;
        int[][] produce = new int[n][2];

        for (int i = 0; i < n; i++) {
            produce[i][0] = worst[i];
            produce[i][1] = expected[i];
        }

        // CRITICAL FIX: Sort by (worst[i] - expected[i]) in DESCENDING order
        Arrays.sort(produce, (a, b) -> Integer.compare((b[0] - b[1]), (a[0] - a[1])));

        long minRequiredBudget = 0;
        long currentBudget = 0;

        // Single pass simulation
        for (int[] product : produce) {
            int worstReq = product[0];
            int expectedCost = product[1];

            // If current budget falls short of worst-case requirement, top up initial budget
            if (currentBudget < worstReq) {
                minRequiredBudget += (worstReq - currentBudget);
                currentBudget = worstReq; // Budget is now topped up to worstReq
            }

            // Deduct actual production cost
            currentBudget -= expectedCost;
        }

        return minRequiredBudget;
    }

    public static void main(String[] args) {
        // Sample input 1[cite: 3]
        int[] worst1 = {9, 7, 6};
        int[] expected1 = {1, 2, 5};
        System.out.println("Test 1: " + minimumBudget(worst1, expected1) + " (Expected: 9)"); //[cite: 3]

        // Sample input 2[cite: 3]
        int[] worst2 = {5, 4};
        int[] expected2 = {3, 3};
        System.out.println("Test 2: " + minimumBudget(worst2, expected2) + " (Expected: 7)"); //[cite: 3]

        // Sample input 3[cite: 3]
        int[] worst3 = {4, 4, 4};
        int[] expected3 = {1, 2, 3};
        System.out.println("Test 3: " + minimumBudget(worst3, expected3) + " (Expected: 7)"); //[cite: 3]

        // Counterexample Test Case (Fails on naive sort by worst[i] alone)[cite: 3]
        int[] worstCounter = {15, 16, 13};
        int[] expectedCounter = {1, 9, 4};
        System.out.println("Counterexample Test: " + minimumBudget(worstCounter, expectedCounter) + " (Expected: 21)");
    }
}
package DSA_Interview_Questions.Fivetran;


import java.util.Arrays;
import java.util.Comparator;

/**
 Becky Corp needs to produce n products.
 For each product i:

 worst[i] = worst‑case cost required before starting this product (you must have at least this much budget available to begin it)

 expected[i] = actual cost you will pay when you produce it, which is deducted from your current budget

 You may produce the products in any order.
 You start with an initial budget B and want to be able to finish all products.

 Find the minimum integer budget B such that there exists some order of producing all products where:

 Before producing each product i, your current budget ≥ worst[i]

 After producing it, your budget decreases by expected[i]

 You never go negative and eventually produce all products

 Return this minimum budget.

 Constraints:
 1≤n≤10^5
 1≤expected[i]≤worst[i]≤10^9


 Function signature ideas: long minimumBudget(int[] worst, int[] expected)

 Sample input 1
 n = 3
 worst  = [9, 7, 6]
 expect = [1, 2, 5]

 Sample output 1
 9

 Sample input 2
 n = 2
 worst  = [5, 4]
 expect = [3, 3]

 Sample output 2
 7

 Sample input 3
 n = 3
 worst  = [4, 4, 4]
 expect = [1, 2, 3]

 Sample output 3
 7

 **/
public class FindMinimumBudget {

    public static long minimumBudget(int[] worst, int[] expected) {
        int n = worst.length;

        long left = 0;
        long right = (long) 1e18; // Large enough upper bound

        int[][] produce = new int[n][2];

        for (int i = 0; i < n; i++) {
            produce[i][0] = worst[i];
            produce[i][1] = expected[i];
        }

        // Sort by worst requirement in DESCENDING order
        Arrays.sort(produce, (a, b) -> b[0] - a[0]);

        long ans = right;
        while (left <= right) {
            long mid = left + (right - left) / 2;
            if (canProduce(produce, mid)) {
                ans = mid;
                right = mid - 1; // Try to find smaller budget
            } else {
                left = mid + 1; // Need more budget
            }
        }

        return ans;
    }

    private static boolean canProduce(int[][] produce, long budget) {
        long currentBudget = budget;
        for (int i = 0; i < produce.length; i++) {
            int worst = produce[i][0];
            int expected = produce[i][1];
            
            // Check if we have enough budget to start this product
            if (currentBudget < worst) {
                return false;
            }
            
            // Deduct the expected cost
            currentBudget -= expected;
        }
        return true;
    }

    public static void main(String[] args) {
        // Sample input 1
        int[] worst1 = {9, 7, 6};
        int[] expected1 = {1, 2, 5};
        System.out.println("Test 1: " + minimumBudget(worst1, expected1) + " (Expected: 9)");

        // Sample input 2
        int[] worst2 = {5, 4};
        int[] expected2 = {3, 3};
        System.out.println("Test 2: " + minimumBudget(worst2, expected2) + " (Expected: 7)");

        // Sample input 3
        int[] worst3 = {4, 4, 4};
        int[] expected3 = {1, 2, 3};
        System.out.println("Test 3: " + minimumBudget(worst3, expected3) + " (Expected: 7)");
    }
}

package DSA_Interview_Questions.CompanyWisePrep.Fivetran;

import java.util.Arrays;

/**
 * Assigned Parking / Minimum Fuel to Arrange Cars Side-by-Side
 * 
 * STRATEGY: Independent Projection + Median Transformation
 * - Minimizes Y distance by choosing median of Y coordinates.
 * - Minimizes X distance by sorting X and applying transformation A[i] = xSorted[i] - i.
 * - Uses 64-bit 'long' to prevent 32-bit integer overflow.
 */
public class AssignedParking {

    /**
     * Calculates the minimum fuel cost to arrange cars in a contiguous row.
     * 
     * TIME COMPLEXITY:  O(N log N) dominated by sorting operations.
     * SPACE COMPLEXITY: O(N) for coordinate clone arrays.
     */
    public static long minFuel(int[] x, int[] y) {
        if (x == null || y == null || x.length == 0) {
            return 0;
        }

        int n = x.length;

        // Part 1: Calculate Y Movement Cost (Target line = Median of Y)
        int[] ySorted = y.clone();
        Arrays.sort(ySorted);
        int medianY = ySorted[n / 2];

        long yMovementCost = 0; // MUST be long to avoid 32-bit overflow
        for (int i = 0; i < n; i++) {
            yMovementCost += Math.abs((long) y[i] - medianY);
        }

        // Part 2: Calculate X Movement Cost
        // Step A: Sort initial X coordinates to prevent path crossing
        int[] xSorted = x.clone();
        Arrays.sort(xSorted);

        // Step B: Transform to A[i] = xSorted[i] - i
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = xSorted[i] - i;
        }

        // Step C: Optimal starting position P is Median of array A
        Arrays.sort(a);
        int medianA = a[n / 2];

        long xMovementCost = 0; // MUST be long to avoid 32-bit overflow
        for (int i = 0; i < n; i++) {
            xMovementCost += Math.abs((long) a[i] - medianA);
        }

        return xMovementCost + yMovementCost;
    }
    
    public static void main(String[] args) {
        // Sample Case 1: x = [1, 4], y = [1, 4]
        int[] x1 = {1, 4};
        int[] y1 = {1, 4};
        System.out.println("Test 1 Result: " + minFuel(x1, y1) + " (Expected: 5)"); //

        // Sample Case 2: Custom multi-point case
        int[] x2 = {4, 6, 4, -4, 1};
        int[] y2 = {-1, 1, -5, -4, 5};
        System.out.println("Test 2 Result: " + minFuel(x2, y2) + " (Expected: 23)"); //

        // Sample Case 3: x = [1, 5], y = [1, 5]
        int[] x3 = {1, 5};
        int[] y3 = {1, 5};
        System.out.println("Test 3 Result: " + minFuel(x3, y3) + " (Expected: 7)"); //
    }
}
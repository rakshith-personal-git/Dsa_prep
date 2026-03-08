package DSA_Interview_Questions.Fivetran;

import java.util.Arrays;

public class AssignedParking {

    /**
     * Question —
     * There are n cars located on a 2-dimensional plane at positions (x[i], y[i]) where 0 ≤ i ≤ n.
     * They need to be parked in a straight line parallel to the x-axis with no spaces between them.
     * The fuel consumed to move a car is abs(x[finish] — x[start]) + abs(y[finish] — y[start]).
     * Determine the minimum fuel cost to arrange the cars side-by-side in a row parallel to the x-axis.
     * <p>
     * Example
     * x = [1, 4]
     * y = [1, 4]
     * <p>
     * One optimal solution is:
     * The car initially at position (1, 1) moves to (3, 1) for a cost of abs(3–1) + abs(1–1) = 2 + 0 = 2.
     * The car initially at position (4, 4) moves to (4, 1) for a cost of 0 + 3 = 3.
     * The total fuel consumed is 2 + 3 = 5.
     * <p>
     * Function Description
     * Complete the function minFuel in the editor below.
     * <p>
     * minFuel has the following parameter(s):
     * int x[n]: the x coordinates
     * int y[n]: the y coordinates
     * <p>
     * Returns
     * int: the minimum fuel required to move all of the cars
     * <p>
     * Constraints
     * 1 ≤ n ≤ 10⁵
     * -10⁹ ≤ x[i], y[i] ≤ 10⁹
     * Input Format For Custom Testing
     * <p>
     * The first line contains an integer, n, the number of elements in x.
     * Each line i of the n subsequent lines (where 0 ≤ i < n) contains an integer x[i].
     * <p>
     * The next line contains the same integer, n, the number of elements in y.
     * Each line i of the n subsequent lines (where 0 ≤ i < n) contains an integer, y[i].
     * <p>
     * Sample Case 0
     * Sample Input For Custom Testing
     * <p>
     * STDIN     FUNCTION
     * -----     --------
     * 2     →   n = 2
     * 1     →   x = [1, 5]
     * 5
     * 2     →   n = 2
     * 1     →   y = [1, 5]
     * 5
     * <p>
     * Sample Output
     * 7
     * Explanation
     * Initially, the cars are at points (1, 1) and (5, 5).
     * The car at (1, 1) moves to (4, 1) and the car at (5, 5) moves to (5, 1).
     * The fuel used is abs(4–1) + abs(1–1) + abs(5–1) + abs(5–5) = 3 + 0 + 4 + 0 = 7.
     * <p>
     * Sample Case 1
     * Sample Input For Custom Testing
     * <p>
     * STDIN     FUNCTION
     * -----     --------
     * 5     →  n = 5
     * 4     →  x = [4, 6, 4, -4, 1]
     * 6
     * 4
     * -4
     * 1
     * 5     →  n = 5
     * -1    →  y = [-1, 1, -5, -4, 5]
     * 1
     * -5
     * -4
     * 5
     * <p>
     * Sample Output
     * 23
     * <p>
     * Explanation
     * Initially, the cars are at points (4, -1), (6, 1), (4, -5), (-4, -4), and (1, 5).
     * One optimal arrangement is shown above.
     * Point (-4, -4) moves to (2 , -1) = 6 + 3 = 9
     * Point (1, 5) moves to (1 , -1) = 0 + 6 = 6
     * Point (4, -1) does not move = 0 + 0 = 0
     * Point (4, -5) moves to (3 , -1) = 1 + 4 = 5
     * Point (6, 1) moves to (5 , -1) = 1 + 2 = 3
     **/

    public static int minFuel(int[] x, int[] y) {
        int n = x.length;

        // Y: all cars must end on same row. Optimal y is median (minimizes sum of vertical distances).
        int medianY = getMedian(y);
        int yMovementCost = 0;
        for (int i = 0; i < n; i++) {
            yMovementCost += Math.abs(y[i] - medianY);
        }

        // X: cars end at contiguous positions p, p+1, ..., p+n-1.
        // Optimal assignment: assign ith smallest x to position p+i → minimize sum |x_sorted[i] - (p+i)|.
        // So minimize sum_i |(x_sorted[i] - i) - p| → p = median of (x_sorted[i] - i).
        int[] xSorted = x.clone(); //other way of cloning Arrays.copyOf(x, n);
        Arrays.sort(xSorted);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = xSorted[i] - i;
        }
        int medianA = getMedian(a);
        int xMovementCost = 0;
        for (int i = 0; i < n; i++) {
            xMovementCost += Math.abs(a[i] - medianA);
        }

        return xMovementCost + yMovementCost;
    }

    private static int getMedian(int[] arr) {
        int[] sorted = arr.clone();
        Arrays.sort(sorted);
        int median = sorted[sorted.length / 2];
        return median;
    }

    // Helper method to find median

    public static void main(String[] args) {
        int[] x = {1, 4};
        int[] y = {1, 4};
        System.out.println(minFuel(x, y));  // Output: 5

        int[] x2 = {4, 6, 4, -4, 1};
        int[] y2 = {-1, 1, -5, -4, 5};
        System.out.println(minFuel(x2, y2));  // Output: 23

        int[] x3 = {1, 5};
        int[] y3 = {1, 5};
        System.out.println(minFuel(x3, y3));  // Output: 7
    }
}

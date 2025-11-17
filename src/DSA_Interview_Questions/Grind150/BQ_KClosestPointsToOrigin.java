package DSA_Interview_Questions.Grind150;

import java.util.PriorityQueue;

public class BQ_KClosestPointsToOrigin {
    /**
     * Given an array of points where points[i] = [xi, yi] represents a point on the X-Y plane and an integer k, return the k closest points to the origin (0, 0).
     * The distance between two points on the X-Y plane is the Euclidean distance (i.e., √(x1 - x2)2 + (y1 - y2)2).
     * You may return the answer in any order. The answer is guaranteed to be unique (except for the order that it is in).
     * <p>
     * Example 1:
     * Input: points = [[1,3],[-2,2]], k = 1
     * Output: [[-2,2]]
     * Explanation:
     * The distance between (1, 3) and the origin is sqrt(10).
     * The distance between (-2, 2) and the origin is sqrt(8).
     * Since sqrt(8) < sqrt(10), (-2, 2) is closer to the origin.
     * We only want the closest k = 1 points from the origin, so the answer is just [[-2,2]].
     * <p>
     * Example 2:
     * Input: points = [[3,3],[5,-1],[-2,4]], k = 2
     * Output: [[3,3],[-2,4]]
     * Explanation: The answer [[-2,4],[3,3]] would also be accepted.
     */

    public static void main(String[] args) {
        int[][] points = {{1, 3}, {-2, 2}};
        int k = 1;
        int[][] ans = kClosest(points, k);
        printAns(ans);
    }

    // since in the formula (x1, y1) = (0, 0)... we can reduce the formula to √x2^2 + y2^2 and we can skip the square root and just keep it as (x2^2 + y2^2)
    // hence to compare we need to do a -b i.e (a, b) -> a[0]^2 + a[1]^2 - (b[0]^2 + b[1]^2)
    private static int[][] kClosest(int[][] points, int k) {
        PriorityQueue<int[]> priorityQueue = new PriorityQueue<>((p1, p2) -> p2[0] * p2[0] + p2[1] * p2[1] - p1[0] * p1[0] - p1[1] * p1[1]);

        for (int i = 0; i < points.length; i++) {
            priorityQueue.add(points[i]);
            if (priorityQueue.size() > k) {
                priorityQueue.remove();
            }
        }

        int[][] ans = new int[k][2];

        for (int i = 0; i < k; i++) {
            ans[i] = priorityQueue.remove();
        }
        return ans;
    }

    //TC is O(nlogk)
    //SC is O(k)

    public static void printAns(int[][] array) {
        System.out.print("[");
        for (int i = 0; i < array.length; i++) {
            System.out.print("[");
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j]);
                if (j < array[i].length - 1) {
                    System.out.print(",");
                }
            }
            System.out.print("]");
            if (i < array.length - 1) {
                System.out.print(",");
            }
        }
        System.out.println("]");
    }

}

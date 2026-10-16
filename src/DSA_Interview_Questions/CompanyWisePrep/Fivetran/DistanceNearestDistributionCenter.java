package DSA_Interview_Questions.CompanyWisePrep.Fivetran;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class DistanceNearestDistributionCenter {
    /**
     * Problem: Distance to Nearest Distribution Center
     * You are given an $M \times N$ 2D grid representing a logistics
     * region:
     * 0: Distribution Center (Source)
     * 1: Customer Warehouse
     * -1: Blocked Obstacle (impassable mountain/water)
     * Calculate the minimum distance for each Customer Warehouse (1) to reach its nearest Distribution Center (0).
     * Movement is allowed in 4 cardinal directions (Up, Down, Left, Right) with a step cost of 1.
     * If a warehouse cannot reach any distribution center due to obstacles, set its distance to -1.
     * Return the updated $M \times N$ matrix.Constraints: $1 \le M, N \le 1000$.
     * Matrix contains at least one 0.
     *
     * Sample Input:Plaintextgrid = [
     *   [ 0,  1, -1],
     *   [ 1,  1,  1],
     *   [-1,  1,  0]
     * ]
     * Sample Output:Plaintext[
     *   [0, 1, -1],
     *   [1, 2,  1],
     *   [-1, 1, 0]
     * ]
     * @param args
     */

    private static final int[][] DIRS = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    public static int[][] findDistance(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return new int[0][0];
        }

        int m = grid.length;
        int n = grid[0].length;
        int[][] dist = new int[m][n];
        Queue<int[]> queue = new ArrayDeque<>();

        // Step 1: Initialize dist matrix and seed multi-source queue
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0) {
                    dist[i][j] = 0;
                    queue.offer(new int[]{i, j}); // Seed distribution centers (distance 0)
                } else if (grid[i][j] == 1) {
                    dist[i][j] = Integer.MAX_VALUE; // Unvisited customer warehouse
                } else {
                    dist[i][j] = -1; // Obstacle
                }
            }
        }

        // Step 2: Multi-Source BFS
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0];
            int c = cur[1];

            for (int[] d : DIRS) {
                int nr = r + d[0];
                int nc = c + d[1];

                // Boundary check & skip obstacles
                if (nr < 0 || nr >= m || nc < 0 || nc >= n || dist[nr][nc] == -1 || dist[nr][nc] == Integer.MAX_VALUE) {
                    continue;
                }

                dist[nr][nc] = dist[r][c] + 1;
                queue.offer(new int[]{nr, nc});

            }
        }

        // Step 3: Cleanup unreachable customer warehouses (Integer.MAX_VALUE -> -1)
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (dist[i][j] == Integer.MAX_VALUE) {
                    dist[i][j] = -1;
                }
            }
        }

        return dist;
    }

    public static void main(String[] args) {
        int[][] grid = {
                { 0,  1, -1},
                { 1,  1,  1},
                {-1,  1,  0}
        };

        int[][] res = findDistance(grid);
        System.out.println(Arrays.deepToString(res));
        // Expected Output: [[0, 1, -1], [1, 2, 1], [-1, 1, 0]]
    }


}

package DSA_Interview_Questions.Grind150;

import java.util.LinkedList;
import java.util.Queue;

public class CD_RottingOranges {
    /**
     * You are given an m x n grid where each cell can have one of three values:
     * <p>
     * 0 representing an empty cell,
     * 1 representing a fresh orange, or
     * 2 representing a rotten orange.
     * Every minute, any fresh orange that is 4-directionally adjacent to a rotten orange becomes rotten.
     * <p>
     * Return the minimum number of minutes that must elapse until no cell has a fresh orange. If this is impossible, return -1.
     * <p>
     * Example 1:
     * Input: grid = [[2,1,1],[1,1,0],[0,1,1]]
     * Output: 4
     * <p>
     * Example 2:
     * Input: grid = [[2,1,1],[0,1,1],[1,0,1]]
     * Output: -1
     * Explanation: The orange in the bottom left corner (row 2, column 0) is never rotten, because rotting only happens 4-directionally.
     * <p>
     * Example 3:
     * Input: grid = [[0,2]]
     * Output: 0
     * Explanation: Since there are already no fresh oranges at minute 0, the answer is just 0.
     * <p>
     * Constraints:
     * m == grid.length
     * n == grid[i].length
     * 1 <= m, n <= 10
     * grid[i][j] is 0, 1, or 2.
     */

    public static void main(String[] args) {
        int[][] grid = {
                {2, 1, 1},
                {1, 1, 0},
                {0, 1, 1}};

        int[][] grid2 = {
                {2, 1, 1},
                {1, 1, 0},
                {0, 1, 1}};

        int ans = orangesRottingUsingBFS(grid);
        System.out.println(ans);
        ans = orangesRottingUsingDFS(grid2);
        System.out.println(ans);
    }

    /**
     * we utilize BFS to explore the grid. We start by identifying all initially rotten oranges and proceed to
     * simulate the rotting process by traversing adjacent fresh oranges iteratively.
     * We maintain a queue to manage the order of exploration and record the time taken for each orange to rot.
     * Once all fresh oranges are affected, we return the maximum time taken among them.
     */
    private static int orangesRottingUsingBFS(int[][] grid) {
        LinkedList<int[]> queue = new LinkedList<>(); //using int[] since we need to store and pass 3 values... row, col and time

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == 2) {
                    int[] temp = {row, col, 0};
                    queue.add(temp);
                }
            }
        }

        int ans = 0;
        while (!queue.isEmpty()) {
            int[] remove = queue.removeFirst();
            int row = remove[0];
            int col = remove[1];
            int time = remove[2];
            ans = time;

            if (row + 1 < grid.length && grid[row + 1][col] == 1) {
                int[] temp = {row + 1, col, time + 1};
                queue.add(temp);
                grid[row + 1][col] = 2; //this orange became rotten
            }

            if (col + 1 < grid[row].length && grid[row][col + 1] == 1) {
                int[] temp = {row, col + 1, time + 1};
                queue.add(temp);
                grid[row][col + 1] = 2;
            }

            if (row - 1 >= 0 && grid[row - 1][col] == 1) {
                int[] temp = {row - 1, col, time + 1};
                queue.add(temp);
                grid[row - 1][col] = 2;
            }

            if (col - 1 >= 0 && grid[row][col - 1] == 1) {
                int[] temp = {row, col - 1, time + 1};
                queue.add(temp);
                grid[row][col - 1] = 2;
            }
        }

        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == 1) {
                    return -1;
                }
            }
        }


        return ans;
    }


    /**
     * We recursively traverse the grid from initially rotten oranges to adjacent fresh oranges, marking the time it takes for each orange to rot.
     * We maintain a minute counter for each cell, ensuring that we track the minutes accurately as the rotting process propagates.
     * Finally, we determine the maximum minute count among all oranges and return the time taken for all oranges to rot.
     */
    private static int orangesRottingUsingDFS(int[][] grid) {
        if (grid.length == 0) {
            return -1;
        }

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == 2) {
                    rotAdjacentCell(grid, row, col, 2);
                }
            }
        }

        int minutes = 2;
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == 1) return -1;
                minutes = Math.max(minutes, cell);
            }
        }

        return minutes - 2;
    }

    private static void rotAdjacentCell(int[][] grid, int row, int col, int time) {
        if (row < 0 || row >= grid.length
                || col < 0 || col >= grid[0].length
                || grid[row][col] == 0
                || (1 < grid[row][col] && grid[row][col] < time) /* this orange is already rotten by another rotten orange */) {
            return;
        }

        grid[row][col] = time;
        rotAdjacentCell(grid, row + 1, col, time + 1);
        rotAdjacentCell(grid, row - 1, col, time + 1);
        rotAdjacentCell(grid, row, col + 1, time + 1);
        rotAdjacentCell(grid, row, col - 1, time + 1);
    }

    //- Time complexity:
    //(BFS): O(n⋅m),
    //(DFS): O(n⋅m),

    //- Space complexity:
    //(BFS): O(n⋅m),
    //(DFS): O(1),


    //for practise
    private static int orangesRottingUsingBFS2(int[][] grid) {
        Queue<int[]> queue = new LinkedList<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 2) {
                    queue.add(new int[]{i, j, 0});
                }
            }
        }

        int ans = 0;
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int row = cell[0];
            int col = cell[1];
            int time = cell[2];
            ans = time;


            if (row + 1 < grid.length && grid[row + 1][col] == 1) {
                queue.add(new int[]{row + 1, col, time + 1});
                grid[row + 1][col] = 2;
            }

            if (row - 1 >= 0 && grid[row - 1][col] == 1) {
                queue.add(new int[]{row - 1, col, time + 1});
                grid[row - 1][col] = 2;
            }

            if (col - 1 >= 0 && grid[row][col - 1] == 1) {
                queue.add(new int[]{row, col - 1, time + 1});
                grid[row][col - 1] = 2;
            }

            if (col + 1 < grid[0].length && grid[row][col + 1] == 1) {
                queue.add(new int[]{row, col + 1, time + 1});
                grid[row][col + 1] = 2;
            }
        }

        for (int[] row : grid) {
            for (int i : row) {
                if (i == 1) {
                    return -1;
                }
            }
        }
        return ans;
    }


}

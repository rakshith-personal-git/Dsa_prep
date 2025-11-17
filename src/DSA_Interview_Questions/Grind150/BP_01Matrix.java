package DSA_Interview_Questions.Grind150;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

import static DSA_Interview_Questions.StackAndQueues.MergeIntervals.print;

public class BP_01Matrix {
    /**
     * Given an m x n binary matrix mat, return the distance of the nearest 0 for each cell.
     * The distance between two adjacent cells is 1.
     * <p>
     * Example 1:
     * Input: mat = [[0,0,0],[0,1,0],[0,0,0]]
     * Output: [[0,0,0],[0,1,0],[0,0,0]]
     * <p>
     * Example 2:
     * Input: mat = [[0,0,0],[0,1,0],[1,1,1]]
     * Output: [[0,0,0],[0,1,0],[1,2,1]]
     * <p>
     * Constraints:
     * m == mat.length
     * n == mat[i].length
     * 1 <= m, n <= 104
     * 1 <= m * n <= 104
     * mat[i][j] is either 0 or 1.
     * There is at least one 0 in mat.
     */

    public static void main(String[] args) {
        int[][] mat = {{0, 0, 0}, {0, 1, 0}, {1, 1, 1}};
        int[][] ans = updateMatrix(mat);
        print(ans);
        System.out.println();
        ans = updateMatrix2(mat);
        print(ans);
    }


    //for explanation visit this site https://leetcode.com/problems/01-matrix/solutions/3920422/beats-100-java-solution-2-approaches-fully-expalined-clean-and-clear-code-dp-bfs/
    // imp logic is

    /**
     * Let's think about how BFS works. From a source node, we first visit all nodes at a distance of 1.
     * Next, we visit all nodes at a distance of 2, then 3, and so on.
     * We can say a node at a distance of x from the source belongs to "level x".
     * So the source is at level 0, the neighbors of the source are at level 1, the neighbors of those nodes are at level 2, and so on.
     * We are used to starting BFS from only one source node, i.e. level 0 only has one node.
     * But there is nothing stopping us from having multiple nodes in level 0. If we start with multiple nodes in level 0,
     * then the nodes in level 1 will be all the neighbors of the nodes in level 0.
     * The nodes in level 2 will be all the neighbors of the nodes in level 1, and so on - the logic is identical.
     * The following animation illustrates this idea (cells are labeled by their level):
     * As you can see, we don't need to visit any node more than once, which drastically improves our time complexity.
     */

    private static int[][] updateMatrix(int[][] mat) {
        int m = mat.length;
        int n = mat[0].length;
        int[][] directions = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        Queue<int[]> queue = new ArrayDeque<>(); //1st value -> row, 2nd is col and 3rd is step count


        int[][] matrix = new int[m][n];
        boolean[][] visited = new boolean[m][n];
        // Enqueue all 0s and mark the seen
        for (int row = 0; row < m; row++) {
            for (int col = 0; col < n; col++) {
                matrix[row][col] = mat[row][col];
                if (mat[row][col] == 0) {
                    queue.add(new int[]{row, col, 0});
                    visited[row][col] = true;
                }
            }
        }

        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int row = cell[0];
            int column = cell[1];
            int steps = cell[2];

            for (int[] direction : directions) {
                int nextRow = row + direction[0];
                int nextCol = column + direction[1];
                if (valid(nextRow, nextCol, m, n) && !visited[nextRow][nextCol]) {
                    visited[nextRow][nextCol] = true;
                    queue.add(new int[]{nextRow, nextCol, steps + 1});
                    matrix[nextRow][nextCol] = steps + 1;
                }
            }
        }

        return matrix;
    }

    private static boolean valid(int nextRow, int nextCol, int rowCount, int colCount) {
        if (nextRow >= 0 && nextRow < rowCount && nextCol >= 0 && nextCol < colCount) {
            return true;
        } else {
            return false;
        }
    }

    //TC is O(m×n)[for loop] + O(m×n)[while loop] = O(m×n)
    //SC is O(m×n)[matrix]+ O(m×n)[visited]+ O(m×n) = O(m×n)

    //just practise
    private static int[][] updateMatrix2(int[][] mat) {
       int m = mat.length;
       int n = mat[0].length;

       Queue<int[]> queue = new LinkedList<>(); //each int[] will have 3 values... row, col and stepCount respectively

        int[][] matrix = new int[m][n];
        boolean[][] visited = new boolean[m][n];
        // Enqueue all 0s and mark the seen
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = mat[i][j];
                if (mat[i][j] == 0) {
                    int[] cellDetails = {i, j, 0};
                    queue.add(cellDetails);
                    visited[i][j] = true;
                }
            }
        }

        while (!queue.isEmpty()){
            int[] cell = queue.poll();
            int row = cell[0];
            int column = cell[1];
            int steps = cell[2];

            //up
            if (valid(row + 1, column, m, n) && !visited[row + 1][column]) {
                visited[row + 1][column] = true;
                queue.add(new int[]{row + 1, column, steps + 1});
                matrix[row + 1][column] = steps + 1;
            }

            //down
            if (valid(row - 1, column, m, n) && !visited[row - 1][column]) {
                visited[row - 1][column] = true;
                queue.add(new int[]{row - 1, column, steps + 1});
                matrix[row - 1][column] = steps + 1;
            }

            //left
            if (valid(row, column - 1, m, n) && !visited[row][column - 1]) {
                visited[row][column - 1] = true;
                queue.add(new int[]{row, column - 1, steps + 1});
                matrix[row][column - 1] = steps + 1;
            }

            //right
            if (valid(row, column + 1, m, n) && !visited[row][column + 1]) {
                visited[row][column + 1] = true;
                queue.add(new int[]{row, column + 1, steps + 1});
                matrix[row][column + 1] = steps + 1;
            }

        }

        return matrix;

    }
}

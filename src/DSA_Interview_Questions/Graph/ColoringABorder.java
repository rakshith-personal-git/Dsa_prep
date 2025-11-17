package DSA_Interview_Questions.Graph;

public class ColoringABorder {
    /**
     * You are given an m x n integer matrix grid, and three integers row, col, and color.
     * Each value in the grid represents the color of the grid square at that location.
     * Two squares are called adjacent if they are next to each other in any of the 4 directions.
     * Two squares belong to the same connected component if they have the same color and they are adjacent.
     * The border of a connected component is all the squares in the connected component that are either adjacent to (at least) a square not in the component, or on the boundary of the grid (the first or last row or column).
     * You should color the border of the connected component that contains the square grid[row][col] with color.
     * Return the final grid.
     * <p>
     * Example 1:
     * Input: grid = [[1,1],[1,2]], row = 0, col = 0, color = 3
     * Output: [[3,3],[3,2]]
     * <p>
     * Example 2:
     * Input: grid = [[1,2,2],[2,3,2]], row = 0, col = 1, color = 3
     * Output: [[1,3,3],[2,3,3]]
     * <p>
     * Example 3:
     * Input: grid = [[1,1,1],[1,1,1],[1,1,1]], row = 1, col = 1, color = 2
     * Output: [[2,2,2],[2,1,2],[2,2,2]]
     * <p>
     * Constraints:
     * m == grid.length
     * n == grid[i].length
     * 1 <= m, n <= 50
     * 1 <= grid[i][j], color <= 1000
     * 0 <= row < m
     * 0 <= col < n
     */

    public static void main(String[] args) {
        //int[][] grid = {{2, 3, 4, 3, 1}, {4, 1, 1, 1, 2}, {2, 1, 1, 1, 2}, {5, 1, 1, 1, 2}, {2, 6, 4, 5, 1}};
        //int row = 1, col = 3, color = 3;

        int[][] grid = {{1, 2, 1, 2, 1, 2}, {2, 2, 2, 2, 1, 2}, {1, 2, 2, 2, 1, 2}};
        print(grid);
        System.out.println();
        int row = 1, col = 3, color = 1;
        int[][] ans = colorBorder(grid, row, col, color);
        print(ans);
    }


    private static int[][] colorBorder(int[][] grid, int row, int col, int color) {
        int oc = grid[row][col];
        dfs(grid, row, col, oc);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] < 0) {
                    grid[i][j] = color;
                }
            }
        }
        return grid;
    }


    private static boolean isBorderCell(int[][] grid, int row, int col, int oc) {
        return row > 0 && col > 0 && row < grid.length - 1 && col < grid[0].length - 1 &&
                Math.abs(grid[row - 1][col]) == oc &&
                Math.abs(grid[row + 1][col]) == oc &&
                Math.abs(grid[row][col - 1]) == oc &&
                Math.abs(grid[row][col + 1]) == oc;

    }

    private static void dfs(int[][] grid, int row, int col, int oc) {
        if (row < 0 || col < 0 || row >= grid.length || col >= grid[0].length || grid[row][col] != oc) {
            return;
        }

        grid[row][col] = -oc; //marking all the elements of component with a -ve value

        //top
        dfs(grid, row - 1, col, oc);
        //left
        dfs(grid, row, col - 1, oc);
        //down
        dfs(grid, row + 1, col, oc);
        //right
        dfs(grid, row, col + 1, oc);

        if (row - 1 >= 0 && col - 1 >= 0 && row + 1 < grid.length && col + 1 < grid[0].length &&
                Math.abs(grid[row - 1][col]) == oc &&
                Math.abs(grid[row + 1][col]) == oc &&
                Math.abs(grid[row][col - 1]) == oc &&
                Math.abs(grid[row][col + 1]) == oc) {
            grid[row][col] = oc;
            //this element is not a boundary element hence setting it to its original colour and not assigning -ve value to it
        }

    }


    //TC is O(N^2) , SC is O(N^2)
    //also refer much faster solution in leetCode for this

    private static void print(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            // Iterate through columns
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            // Move to the next line after each row
            System.out.println();
        }
    }
}

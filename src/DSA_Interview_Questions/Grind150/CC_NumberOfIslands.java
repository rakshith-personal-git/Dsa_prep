package DSA_Interview_Questions.Grind150;

public class CC_NumberOfIslands {
    /**
     * Given an m x n 2D binary grid grid which represents a map of '1's (land) and '0's (water), return the number of islands.
     * An island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically.
     * You may assume all four edges of the grid are all surrounded by water.
     * <p>
     * <p>
     * Example 1:
     * Input: grid = [
     * ["1","1","1","1","0"],
     * ["1","1","0","1","0"],
     * ["1","1","0","0","0"],
     * ["0","0","0","0","0"]
     * ]
     * Output: 1
     * <p>
     * Example 2:
     * Input: grid = [
     * ["1","1","0","0","0"],
     * ["1","1","0","0","0"],
     * ["0","0","1","0","0"],
     * ["0","0","0","1","1"]
     * ]
     * Output: 3
     * <p>
     * Constraints:
     * m == grid.length
     * n == grid[i].length
     * 1 <= m, n <= 300
     * grid[i][j] is '0' or '1'.
     */

    public static void main(String[] args) {
        char[][] grid = {
                {'1', '1', '1', '1', '0'},
                {'1', '1', '0', '1', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '0', '0', '0'}};
        char[][] grid2 = {
                {'1', '1', '0', '0', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '1', '0', '0'},
                {'0', '0', '0', '1', '1'}};
        char[][] grid3 = {
                {'1', '1', '1'},
                {'0', '1', '0'},
                {'1', '1', '1'}};

        int ans = numIslands(grid3);
        System.out.println(ans);
    }

    private static int numIslands(char[][] grid) {
        int ans = 0;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == '1') {
                    dfs(grid, row, col);
                    ans++;
                }
            }
        }
        return ans;
    }

    private static void dfs(char[][] grid, int row, int col) {
        grid[row][col] = '0';

        if (row + 1 < grid.length && grid[row + 1][col] == '1') {
            dfs(grid, row + 1, col);
        }
        if (col + 1 < grid[row].length && grid[row][col + 1] == '1') {
            dfs(grid, row, col + 1);
        }
        if (row - 1 >= 0 && grid[row - 1][col] == '1') {
            dfs(grid, row - 1, col);
        }
        if (col - 1 >= 0 && grid[row][col - 1] == '1') {
            dfs(grid, row, col - 1);
        }
    }
}

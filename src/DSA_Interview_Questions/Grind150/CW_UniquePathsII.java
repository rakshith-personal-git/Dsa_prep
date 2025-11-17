package DSA_Interview_Questions.Grind150;

public class CW_UniquePathsII {
    /**
     * You are given an m x n integer array grid. There is a robot initially located at the top-left corner (i.e., grid[0][0]).
     * The robot tries to move to the bottom-right corner (i.e., grid[m - 1][n - 1]). The robot can only move either down or right at any point in time.
     * An obstacle and space are marked as 1 or 0 respectively in grid. A path that the robot takes cannot include any square that is an obstacle.
     * <p>
     * Return the number of possible unique paths that the robot can take to reach the bottom-right corner.
     * The testcases are generated so that the answer will be less than or equal to 2 * 109.
     * <p>
     * Example 1:
     * Input: obstacleGrid = [[0,0,0],[0,1,0],[0,0,0]]
     * Output: 2
     * Explanation: There is one obstacle in the middle of the 3x3 grid above.
     * There are two ways to reach the bottom-right corner:
     * 1. Right -> Right -> Down -> Down
     * 2. Down -> Down -> Right -> Right
     * <p>
     * Example 2:
     * Input: obstacleGrid = [[0,1],[0,0]]
     * Output: 1
     * <p>
     * Constraints:
     * m == obstacleGrid.length
     * n == obstacleGrid[i].length
     * 1 <= m, n <= 100
     * obstacleGrid[i][j] is 0 or 1.
     */

    public static void main(String[] args) {
        int[][] obstacleGrid = {{0, 0, 0}, {0, 1, 0}, {0, 0, 0}};
        System.out.println(uniquePathsWithObstacles(obstacleGrid));
    }

    private static int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int m = obstacleGrid.length;
        int n = obstacleGrid[0].length;

        // Create a 2D array to store the number of unique paths to reach each cell
        int[][] dp = new int[m][n];

        // Base case: set the number of paths to reach the starting cell
        dp[0][0] = obstacleGrid[0][0] == 0 ? 1 : 0;

        // Initialize the first row and first column
        for (int i = 1; i < m; i++) {
            dp[i][0] = obstacleGrid[i][0] == 0 ? dp[i - 1][0] : 0;
        }
        for (int j = 1; j < n; j++) {
            dp[0][j] = obstacleGrid[0][j] == 0 ? dp[0][j - 1] : 0;
        }

        // Fill in the rest of the dp array
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (obstacleGrid[i][j] == 0) {
                    dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
                } else {
                    dp[i][j] = 0; // If there's an obstacle, no path can reach this cell
                }
            }
        }

        return dp[m - 1][n - 1];
    }
}

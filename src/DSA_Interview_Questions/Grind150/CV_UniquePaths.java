package DSA_Interview_Questions.Grind150;

public class CV_UniquePaths {
    /**
     * There is a robot on an m x n grid. The robot is initially located at the top-left corner (i.e., grid[0][0]).
     * The robot tries to move to the bottom-right corner (i.e., grid[m - 1][n - 1]). The robot can only move either down or right at any point in time.
     * Given the two integers m and n, return the number of possible unique paths that the robot can take to reach the bottom-right corner.
     * The test cases are generated so that the answer will be less than or equal to 2 * 109.
     * <p>
     * Example 1:
     * Input: m = 3, n = 7
     * Output: 28
     * <p>
     * Example 2:
     * Input: m = 3, n = 2
     * Output: 3
     * Explanation: From the top-left corner, there are a total of 3 ways to reach the bottom-right corner:
     * 1. Right -> Down -> Down
     * 2. Down -> Down -> Right
     * 3. Down -> Right -> Down
     * <p>
     * Constraints:
     * 1 <= m, n <= 100
     */

    public static void main(String[] args) {
        int m = 3;
        int n = 7;
        System.out.println(uniquePathsUsingTabulation(m, n));
        System.out.println(uniquePathsUsingDfs(m, n));
    }


    private static int uniquePathsUsingTabulation(int m, int n) {
        if (m == 0 || n == 0) {
            return 0;
        }
        if (m == 1 || n == 1) {
            return 1;
        }
        int[][] dp = new int[m][n];

        //top row
        for (int col = 0; col < dp[0].length; col++) {
            dp[0][col] = 1;
        }

        //left column
        for (int row = 0; row < dp.length; row++) {
            dp[row][0] = 1;
        }

        //now fill up the table using the logic that robot can only move either down or right
        for (int row = 1; row < m; row++) {
            for (int col = 1; col < n; col++) {
                dp[row][col] = dp[row - 1][col] + dp[row][col - 1];
            }
        }

        return dp[m - 1][n - 1];
    }


    private static int uniquePathsUsingDfs(int m, int n) {
        int[][] dp = new int[m][n];
        return backTrack(0, 0, m, n, dp);
    }

    private static int backTrack(int path1, int path2, int m, int n, int[][] dp) {
        if (path1 + 1 == m && path2 + 1 == n) {
            return 1;
        }
        if (path1 == m) {
            return 0;
        }
        if (path2 == n) {
            return 0;
        }

        if (dp[path1][path2] != 0) {
            //already set
            return dp[path1][path2];
        }

        int goingDown = backTrack(path1 + 1, path2, m, n, dp);
        int goingRight = backTrack(path1, path2 + 1, m, n, dp);
        dp[path1][path2] = goingDown + goingRight;
        return dp[path1][path2];
    }
}

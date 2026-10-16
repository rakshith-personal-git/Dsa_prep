package DSA_Interview_Questions.DP;

public class LongestCommonSubsequence {

    /**
     * Given two strings text1 and text2, return the length of their longest common subsequence. If there is no common subsequence, return 0.
     * A subsequence of a string is a new string generated from the original string with some characters (can be none) deleted without changing the relative order of the remaining characters.
     * <p>
     * For example, "ace" is a subsequence of "abcde".
     * A common subsequence of two strings is a subsequence that is common to both strings.
     * <p>
     * Example 1:
     * Input: text1 = "abcde", text2 = "ace"
     * Output: 3
     * Explanation: The longest common subsequence is "ace" and its length is 3.
     * <p>
     * Example 2:
     * Input: text1 = "abc", text2 = "abc"
     * Output: 3
     * Explanation: The longest common subsequence is "abc" and its length is 3.
     * <p>
     * Example 3:
     * Input: text1 = "abc", text2 = "def"
     * Output: 0
     * Explanation: There is no such common subsequence, so the result is 0.
     * <p>
     * Constraints:
     * 1 <= text1.length, text2.length <= 1000
     * text1 and text2 consist of only lowercase English characters.
     */

    public static void main(String[] args) {
        String text1 = "abbcdgf";
        String text2 = "bbadcgf";
        int ans = longestCommonSubsequence(text1, text2);
        int ans2 = longestCommonSubsequence2(text1, text2);
        System.out.println(ans);
        System.out.println(ans2);
    }

    private static int longestCommonSubsequence(String text1, String text2) {
        int[][] dp = new int[text1.length() + 1][text2.length() + 1];

        for (int i = dp.length - 2; i >= 0; i--) {
            for (int j = dp[i].length - 2; j >= 0; j--) {
                if (text1.charAt(i) == text2.charAt(j)) {
                    dp[i][j] = 1 + dp[i+1][j+1];
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j + 1]);
                }
            }
        }
        return dp[0][0];
    }

    /**
     * TIME COMPLEXITY:  O(M * N) where M = text1.length(), N = text2.length().
     * SPACE COMPLEXITY: O(M * N) for the 2D DP grid.
     */
    private static int longestCommonSubsequence2(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return 0;
        }

        int m = text1.length();
        int n = text2.length();

        // 1-indexed DP table to cleanly handle empty string base cases
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            char char1 = text1.charAt(i - 1);

            for (int j = 1; j <= n; j++) {
                char char2 = text2.charAt(j - 1);

                if (char1 == char2) {
                    // Match: Add 1 to diagonal result
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                } else {
                    // Mismatch: Max of excluding char1 vs excluding char2
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }
}

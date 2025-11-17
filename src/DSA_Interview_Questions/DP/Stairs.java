package DSA_Interview_Questions.DP;

import java.math.BigInteger;

public class Stairs {
    /**
     * You are climbing a staircase and it takes A steps to reach the top.
     * Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?
     * Return the number of distinct ways modulo 1000000007
     *
     * Problem Constraints
     * 1 <= A <= 105
     *
     * Example Input
     * Input 1:
     *  A = 2
     * Input 2:
     *  A = 3
     *
     * Example Output
     * Output 1:
     *  2
     * Output 2:
     *  3
     *
     * Explanation 1:
     *  Distinct ways to reach top: [1, 1], [2].
     * Explanation 2:
     *  Distinct ways to reach top: [1 1 1], [1 2], [2 1].
     * */

    public static void main(String[] args) {
        int a = 55007;
        int ans = climbStairs(a);
        System.out.println(ans);
    }

    private static int climbStairs(int n) {
        if(n == 1 || n== 2) {
            return n;
        }
        final int MOD = 1000000007;
        int[] dp = new int[n + 1];
        dp[1] = 1;
        dp[2] = 2;
        for (int i = 3; i < dp.length; i++) {
            dp[i] = (dp[i-1] + dp[i-2]) % MOD;;
        }
        return dp[dp.length - 1];
    }
}

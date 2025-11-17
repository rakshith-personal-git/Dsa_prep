package DSA_Interview_Questions.Grind150;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BY_CoinChange {
    /**
     * You are given an integer array coins representing coins of different denominations and an integer amount representing a total amount of money.
     * Return the fewest number of coins that you need to make up that amount.
     * If that amount of money cannot be made up by any combination of the coins, return -1.
     * <p>
     * You may assume that you have an infinite number of each kind of coin.
     * <p>
     * Example 1:
     * Input: coins = [1,2,5], amount = 11
     * Output: 3
     * Explanation: 11 = 5 + 5 + 1
     * <p>
     * Example 2:
     * Input: coins = [2], amount = 3
     * Output: -1
     * <p>
     * Example 3:
     * Input: coins = [1], amount = 0
     * Output: 0
     * <p>
     * Constraints:
     * 1 <= coins.length <= 12
     * 1 <= coins[i] <= 2^31 - 1
     * 0 <= amount <= 104
     */

    public static void main(String[] args) {
        int[] coins = {1,2,5,10};
        int amount = 18;
        int ans = coinChange(coins, amount);
        System.out.println(ans);
    }

    private static int coinChange(int[] coins, int amount) {
        int max = amount + 1; //this could be Integer.Max also , but this makes understanding easier
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, max);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];

    }

    //Time Complexity: O(n * m), where n is the amount and m is the number of different coin denominations
    //Space Complexity: O(n), where n is the amount.
}

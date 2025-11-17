package DSA_Interview_Questions.Grind150;

public class AD_BestTimeToBuyAndSellStock {
    /**
     * You are given an array prices where prices[i] is the price of a given stock on the ith day.
     * You want to maximize your profit by choosing a single day to buy one stock and choosing a different day in the future to sell that stock.
     * Return the maximum profit you can achieve from this transaction. If you cannot achieve any profit, return 0.
     * <p>
     * Example 1:
     * Input: prices = [7,1,5,3,6,4]
     * Output: 5
     * Explanation: Buy on day 2 (price = 1) and sell on day 5 (price = 6), profit = 6-1 = 5.
     * Note that buying on day 2 and selling on day 1 is not allowed because you must buy before you sell.
     * <p>
     * Example 2:
     * Input: prices = [7,6,4,3,1]
     * Output: 0
     * Explanation: In this case, no transactions are done and the max profit = 0.
     * <p>
     * Constraints:
     * 1 <= prices.length <= 105
     * 0 <= prices[i] <= 104
     */

    public static void main(String[] args) {
        int[] prices = {7, 1, 5, 3, 6, 4};
        int ans = maxProfit(prices);
        System.out.println(ans);
        prices = new int[]{7, 6, 4, 3, 1};
        int ans2 = maxProfit(prices);
        System.out.println(ans2);


    }

    private static int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maximumProfit = 0;
        for (int i = 0; i < prices.length; i++) {
            int currentPrice = prices[i];

            if (currentPrice < minPrice) {
                minPrice = currentPrice;
            } else if (currentPrice - minPrice > maximumProfit) {
                maximumProfit = currentPrice - minPrice;
            }
        }
        return maximumProfit;
    }
}

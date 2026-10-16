package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.Arrays;
import java.util.List;

public class ToastStockMaxProfit {
    /**
     * Finds the maximum profit with a minimum 5-second gap between buy and sell.
     *
     * @param prices A list of stock prices where each index represents 1 second.
     * @return The maximum profit, or 0 if no profit is possible.
     */
    public static long getMaxProfit(List<Long> prices) {
        if (prices == null || prices.size() < 6) {
            return 0L; // Need at least 6 elements (indices 0 to 5) to have a 5-second gap
        }

        long maxProfit = 0L;
        long minPrice = Long.MAX_VALUE;

        // Start checking sell times from index 5 onwards
        for (int i = 5; i < prices.size(); i++) {
            // The price at (i - 5) is now eligible to be bought
            minPrice = Math.min(minPrice, prices.get(i - 5));

            // Calculate profit if we sell at index i using the best buy price up to (i - 5)
            long currentProfit = prices.get(i) - minPrice;
            if (currentProfit > maxProfit) {
                maxProfit = currentProfit;
            }
        }

        return maxProfit;
    }

    public static void main(String[] args) {
        List<Long> test1 = Arrays.asList(1L, 2L, 3L, 4L, 5L, 10L);
        runTest("Test Case 1", test1, 9L);

        List<Long> test2 = Arrays.asList(2L, 20L, 1L, 3L, 4L, 5L, 6L);
        runTest("Test Case 2", test2, 4L);

        List<Long> test3 = Arrays.asList(10L, 9L, 8L, 7L, 6L, 5L, 4L);
        runTest("Test Case 3", test3, 0L);

        List<Long> test4 = Arrays.asList(1L, 5L, 10L, 15L, 20L);
        runTest("Test Case 4", test4, 0L);

        // Corrected Expected Answer: 10L (Buy at 10 (t=0), Sell at 20 (t=7))
        List<Long> test5 = Arrays.asList(10L, 11L, 12L, 13L, 14L, 15L, 1L, 20L);
        runTest("Test Case 5", test5, 10L);
    }

    private static void runTest(String testName, List<Long> prices, long expected) {
        long result = getMaxProfit(prices);
        if (result == expected) {
            System.out.println(testName + " PASSED");
        } else {
            System.out.println(testName + " FAILED -> Expected: " + expected + ", Got: " + result);
        }
    }
}
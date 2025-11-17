package DSA_Interview_Questions.Grind150;

import java.util.Arrays;

public class DK_KokoEatingBananas {
    /**
     * Koko loves to eat bananas. There are n piles of bananas, the ith pile has piles[i] bananas. The guards have gone and will come back in h hours.
     * Koko can decide her bananas-per-hour eating speed of k. Each hour, she chooses some pile of bananas and eats k bananas from that pile.
     * If the pile has less than k bananas, she eats all of them instead and will not eat any more bananas during this hour.
     * Koko likes to eat slowly but still wants to finish eating all the bananas before the guards return.
     * Return the minimum integer k such that she can eat all the bananas within h hours.
     * <p>
     * Example 1:
     * Input: piles = [3,6,7,11], h = 8
     * Output: 4
     * <p>
     * Example 2:
     * Input: piles = [30,11,23,4,20], h = 5
     * Output: 30
     * <p>
     * Example 3:
     * Input: piles = [30,11,23,4,20], h = 6
     * Output: 23
     * <p>
     * Constraints:
     * 1 <= piles.length <= 104
     * piles.length <= h <= 109
     * 1 <= piles[i] <= 109
     */

    public static void main(String[] args) {
        int[] piles = {3, 6, 7, 11};
        int h = 8;
        System.out.println(minEatingSpeed(piles, h));
    }

    private static int minEatingSpeed(int[] piles, int h) {
        if (piles.length == 0) {
            return 0;
        }
        int left = 1;
        int right = Arrays.stream(piles).max().getAsInt();
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (feasible(mid, piles, h)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    private static boolean feasible(int speed, int[] piles, int h) {
        int totalHours = 0;
        for (int pile : piles) {
            int div = pile/speed;
            totalHours += div;
            if (pile % speed != 0) {
                totalHours++; //i.e if the pile is not completely divisible then adding 1 to ensure that we account for any remaining bananas that cannot be evenly divided by the eating speed.
            }
        }
        if (totalHours > h) {
            return false;
        } else {
            return true;
        }
    }
}

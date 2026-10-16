package DSA_Interview_Questions.Grind150;

import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 523: Continuous Subarray Sum
 *
 * STRATEGY: Prefix Sum + Modulo HashMap
 * - Property: If (PrefixSum[j] - PrefixSum[i]) % k == 0, then PrefixSum[j] % k == PrefixSum[i] % k.
 * - Store the EARLIEST index of each remainder in a HashMap.
 * - If same remainder is seen at index j where (j - earliest_i) >= 2, return true.
 *
 * COMPLEXITY:
 * - Time Complexity:  O(N) - single pass over the array.
 * - Space Complexity: O(min(N, k)) - HashMap stores at most k distinct remainders.
 */
public class EI_ContinuousSubarraySum {

    public static boolean checkSubarraySum(int[] nums, int k) {
        if (nums == null || nums.length < 2) {
            return false;
        }

        // Map stores: <Remainder, Earliest Index>
        Map<Integer, Integer> remainderMap = new HashMap<>();

        // Base case: remainder 0 at dummy index -1 handles subarrays starting at index 0
        remainderMap.put(0, -1);

        int runningSum = 0;

        for (int i = 0; i < nums.length; i++) {
            runningSum += nums[i];

            int remainder = runningSum % k;

            // If remainder already exists in map
            if (remainderMap.containsKey(remainder)) {
                int prevIndex = remainderMap.get(remainder);
                // Check if the subarray length is at least 2
                if (i - prevIndex >= 2) {
                    return true;
                }
            } else {
                // Store ONLY the first occurrence of this remainder
                remainderMap.put(remainder, i);
            }
        }

        return false;
    }

    // Main method for testing in local IntelliJ terminal
    public static void main(String[] args) {
        // Test Case 1
        int[] nums1 = {23, 2, 4, 6, 7};
        int k1 = 6;
        System.out.println("Test 1 Result: " + checkSubarraySum(nums1, k1));
        // Expected: true ([2, 4] sums to 6)

        // Test Case 2
        int[] nums2 = {23, 2, 6, 4, 7};
        int k2 = 6;
        System.out.println("Test 2 Result: " + checkSubarraySum(nums2, k2));
        // Expected: true ([23, 2, 6, 4, 7] sums to 42)

        // Test Case 3
        int[] nums3 = {23, 2, 6, 4, 7};
        int k3 = 13;
        System.out.println("Test 3 Result: " + checkSubarraySum(nums3, k3));
        // Expected: false

        // Test Case 4 (Zeros Edge Case)
        int[] nums4 = {5, 0, 0};
        int k4 = 3;
        System.out.println("Test 4 Result: " + checkSubarraySum(nums4, k4));
        // Expected: true ([0, 0] sums to 0, which is 0 * 3)
    }
}
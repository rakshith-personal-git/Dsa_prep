package DSA_Interview_Questions.CompanyWisePrep.Fivetran;

/**
 * Maximize Minimum Value After K Decrements (Binary Search Strategy)
 * 
 * STRATEGY: Binary Search on Answer Space
 * - Search space: [minVal - ceil(K/N), minVal]
 * - Predicate canAchieve(M): Checks if sum(A[i] - M) >= K.
 */
public class MaximizeMinimumAfterKDecrementsBS {
    //Q -> https://prachub.com/coding-questions/maximize-minimum-after-k-decrements 
    /**
     * Finds the maximum possible minimum element using Binary Search.
     * 
     * TIME COMPLEXITY:  O(N * log(K / N)) where log(Range) <= 47 iterations for K = 10^14.
     * SPACE COMPLEXITY: O(1) constant auxiliary space.
     */
    public static long maxMinAfterKDecrementsBS(int[] arr, long k) {
        if (arr == null || arr.length == 0) {
            return 0;
        }

        int n = arr.length;

        // Step 1: Find initial minimum element
        long minVal = arr[0];
        for (int num : arr) {
            if (num < minVal) {
                minVal = num;
            }
        }

        // Step 2: Define binary search bounds
        // Worst-case low bound: minVal - ceil(K / N)
        long low = minVal - (k + n - 1) / n;
        long high = minVal;
        long ans = low;

        // Step 3: Binary Search on Answer Space
        while (low <= high) {
            long mid = low + (high - low) / 2;

            if (canAchieve(arr, k, mid)) {
                ans = mid;       // Candidate found! Store and try to maximize
                low = mid + 1;   // Search right half
            } else {
                high = mid - 1;  // Target too high; search left half
            }
        }

        return ans;
    }

    /**
     * Helper Predicate: Checks if array capacity sum(num - target) >= k.
     */
    private static boolean canAchieve(int[] arr, long k, long target) {
        long totalCapacity = 0;

        for (int num : arr) {
            totalCapacity += (num - target);

            // Optimization: Early exit if capacity already satisfies K
            if (totalCapacity >= k) {
                return true;
            }
        }

        return totalCapacity >= k;
    }

    public static void main(String[] args) {
        // Test Case 1
        int[] arr1 = {2, 4, 3};
        long k1 = 3;
        System.out.println("BS Test 1 Result: " + maxMinAfterKDecrementsBS(arr1, k1) + " (Expected: 2)");

        // Test Case 2
        int[] arr2 = {2, 4, 3};
        long k2 = 5;
        System.out.println("BS Test 2 Result: " + maxMinAfterKDecrementsBS(arr2, k2) + " (Expected: 1)");

        // Test Case 3: Large equal array
        int[] arr3 = {10, 10, 10, 10, 10};
        long k3 = 10;
        System.out.println("BS Test 3 Result: " + maxMinAfterKDecrementsBS(arr3, k3) + " (Expected: 8)");

        // Test Case 4: Single element
        int[] arr4 = {50};
        long k4 = 15;
        System.out.println("BS Test 4 Result: " + maxMinAfterKDecrementsBS(arr4, k4) + " (Expected: 35)");
    }
}
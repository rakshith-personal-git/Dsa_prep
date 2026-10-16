package DSA_Interview_Questions.CompanyWisePrep.Toast;

public class TwoSortedListsTargetSum {

    /**
     * Given two sorted lists of integers and a target sum,
     * check whether one number exists from each list such that their sum equals the target.
     *
     * @param list1 First sorted array of integers (ascending order).
     * @param list2 Second sorted array of integers (ascending order).
     * @param target Desired sum.
     * @return true if a valid pair exists, false otherwise.
     */
    public static boolean hasTargetSum(int[] list1, int[] list2, int target) {
        // Defensive early return for null or empty inputs
        if (list1 == null || list2 == null || list1.length == 0 || list2.length == 0) {
            return false;
        }

        int p1 = 0;                  // Start of list1 (smallest element)
        int p2 = list2.length - 1;   // End of list2 (largest element)

        while (p1 < list1.length && p2 >= 0) {
            // Using long for currentSum to prevent potential integer overflow
            long currentSum = (long) list1[p1] + list2[p2];

            if (currentSum == target) {
                return true;
            } else if (currentSum < target) {
                p1++; // Move right in list1 to get a larger sum
            } else {
                p2--; // Move left in list2 to get a smaller sum
            }
        }

        return false; // No pair found
    }

    // --- Main Method with Test Cases ---
    public static void main(String[] args) {
        // Test Case 1: Standard positive case (4 + 5 = 9)
        int[] l1_1 = {1, 4, 6, 10};
        int[] l2_1 = {2, 3, 5, 8};
        runTest("Test Case 1 (Standard Pair)", l1_1, l2_1, 9, true);

        // Test Case 2: Target not achievable
        int[] l1_2 = {1, 2, 3};
        int[] l2_2 = {10, 20, 30};
        runTest("Test Case 2 (No Pair Exists)", l1_2, l2_2, 15, false);

        // Test Case 3: Negative numbers involved (-5 + 15 = 10)
        int[] l1_3 = {-10, -5, 0, 3};
        int[] l2_3 = {2, 7, 15, 20};
        runTest("Test Case 3 (Negative Numbers)", l1_3, l2_3, 10, true);

        // Test Case 4: Single element arrays
        int[] l1_4 = {5};
        int[] l2_4 = {5};
        runTest("Test Case 4 (Single Elements)", l1_4, l2_4, 10, true);

        // Test Case 5: Empty input array
        int[] l1_5 = {};
        int[] l2_5 = {1, 2, 3};
        runTest("Test Case 5 (Empty Array)", l1_5, l2_5, 5, false);
    }

    private static void runTest(String testName, int[] list1, int[] list2, int target, boolean expected) {
        boolean result = hasTargetSum(list1, list2, target);
        if (result == expected) {
            System.out.println(testName + " PASSED");
        } else {
            System.out.println(testName + " FAILED -> Expected: " + expected + ", Got: " + result);
        }
    }
}
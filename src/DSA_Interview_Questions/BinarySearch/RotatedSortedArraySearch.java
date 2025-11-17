package DSA_Interview_Questions.BinarySearch;

public class RotatedSortedArraySearch {

    /**
     * Given a sorted array of integers A of size N and an integer B,
     * where array A is rotated at some pivot unknown beforehand.
     * For example, the array [0, 1, 2, 4, 5, 6, 7] might become [4, 5, 6, 7, 0, 1, 2].
     * <p>
     * Your task is to search for the target value B in the array. If found, return its index; otherwise, return -1.
     * You can assume that no duplicates exist in the array.
     * NOTE: You are expected to solve this problem with a time complexity of O(log(N)).
     * <p>
     * <p>
     * Problem Constraints
     * 1 <= N <= 1000000
     * 1 <= A[i] <= 109
     * All elements in A are Distinct.
     * <p>
     * Input Format
     * The First argument given is the integer array A.
     * The Second argument given is the integer B.
     * <p>
     * Output Format
     * Return index of B in array A, otherwise return -1
     * <p>
     * Example Input
     * Input 1:
     * A = [4, 5, 6, 7, 0, 1, 2, 3]
     * B = 4
     * <p>
     * Input 2:
     * A : [ 9, 10, 3, 5, 6, 8 ]
     * B : 5
     * <p>
     * Example Output
     * Output 1:
     * 0
     * <p>
     * Output 2:
     * 3
     * <p>
     * Example Explanation
     * Explanation 1:
     * Target 4 is found at index 0 in A.
     * <p>
     * Explanation 2:
     * Target 5 is found at index 3 in A.
     **/

    public static void main(String[] args) {
        int[] arr = {4, 5, 6, 7, 0, 1, 2, 3};
        int target = 4;

        int index = search(arr, target); //uses binary search 3 times, still TC is O(logN) only
        int index1 = searchOptimised(arr, target); //uses binary search only once
        int index2 = searchOptimisedToZeroMilliSec(arr, target);
        System.out.println(index + " or " + index1 +  "  or " + index2);
    }


    private static int search(int[] arr, int target) {
        int midIndex = findMid(arr);
        int requiredIndex;

        requiredIndex = searchInGivenSection(arr, 0, midIndex - 1, target);
        if (requiredIndex == -1) {
            requiredIndex = searchInGivenSection(arr, midIndex, arr.length - 1, target);
        }
        return requiredIndex;
    }

    private static int searchInGivenSection(int[] arr, int left, int right, int target) {
        //doing binary search here
        while (left <= right) {
            int mid = (left + right) / 2;
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return -1;
    }

    private static int findMid(int[] arr) {
        int left = 0;
        int right = arr.length - 1;
        while (left < right) {
            int mid = (left + right) / 2;
            if (arr[mid] < arr[right]) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }


    private static int searchOptimised(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = (left + right) / 2;

            if (arr[mid] == target) {
                return mid;
            }

            if (arr[left] <= arr[mid]) {
                if (arr[left] <= target && target < arr[mid]) {
                    //using rotated midIndex logic here only
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                if (arr[mid] < target && target <= arr[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }

        return -1;
    }

    private static int searchOptimisedToZeroMilliSec(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left < right) {

            int mid = left + (right - left) / 2;

            if (arr[mid] > arr[right]) {
                left = mid + 1;
            } else
                right = mid;


        }
        int pivot = left;

        left = 0;
        right = arr.length - 1;

        while (left <= right) {

            int mid = left + (right - left) / 2;
            int midPivot = (mid + pivot) % arr.length;

            if (arr[midPivot] < target) {
                left = mid + 1;
            } else if (arr[midPivot] > target) {
                right = mid - 1;
            } else {
                return midPivot;
            }

        }

        return -1;

    }

}

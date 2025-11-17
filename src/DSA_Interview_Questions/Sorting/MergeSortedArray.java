package DSA_Interview_Questions.Sorting;

import java.util.Arrays;

public class MergeSortedArray {
    /**
     * Merge 2 sorted arrays without using inBuilt function(O(nlogn))
     */

    public static void main(String[] args) {
        int[] arr1 = {1,2,3,4,5,6};
        int[] arr2 = {3,5,6,8,9};
        int[] ans = mergeTwoSorted(arr1, arr2);
        Arrays.stream(ans).forEach(i -> {
            System.out.print(i);
            System.out.print(" ");
        });
    }

    public static int[] mergeTwoSorted(int[] arr1, int[] arr2) {
        int[] ans = new int[arr1.length + arr2.length];
        int k = 0;//for ans
        int i = 0;//for arr1
        int j = 0; //for arr2

        while (i < arr1.length && j < arr2.length) {
            if (arr1[i] <= arr2[j]) {
                ans[k] = arr1[i];
                i++;
                k++;
            } else {
                ans[k] = arr2[j];
                j++;
                k++;
            }
        }

        while (i < arr1.length) {
            ans[k] = arr1[i];
            i++;
            k++;
        }
        while (j < arr2.length) {
            ans[k] = arr2[j];
            j++;
            k++;
        }
        return ans;
    }

    //TC is O(n + m) where n and m are lengths of arr1 and arr2
}

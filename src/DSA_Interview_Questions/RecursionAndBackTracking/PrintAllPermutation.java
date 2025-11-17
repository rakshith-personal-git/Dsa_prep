package DSA_Interview_Questions.RecursionAndBackTracking;

import java.util.ArrayList;

public class PrintAllPermutation {

    /**
     * Given an array of size N which contains distinct integers, print all possible permutations
     **/

    public static void main(String[] args) {
        int[] arr = {1, 2, 3};
        printPermutation(arr, 0, new boolean[arr.length], new ArrayList<>());
    }

    private static void printPermutation(int[] arr, int position, boolean[] selected, ArrayList<Object> ans) {
        if (position == arr.length) {
            System.out.println(ans);
            return;
        }

        for (int i = 0; i < arr.length; i++) {
            if (!selected[i]) {
                selected[i] = true;
                ans.add(arr[i]);
                printPermutation(arr, position + 1, selected, ans);
                selected[i] = false; // resetting the selected flag, since all possible permutation for this(ith) index would have been covered by the time we reach here
                ans.remove(ans.size() - 1);
            }
        }

    }
}

package DSA_Interview_Questions.Graph;

public class MinSwapToSortAnArray {
    /**
     * Given an array of size N which contains as all the no's from 0 to N-1, find the no. of swaps required to sort the array
     * <p>
     * Example 1:
     * Input: [0, 1, 2, 3, 4]
     * Output: {} means empty path
     * Explanation: Array s already in ascending order so we don't need to swap anything.
     * <p>
     * Example 2:
     * Input: [0, 3, 4, 1, 2]
     * Output: 2
     * Explanation [0, 3, 4, 1, 2] -> [0, 1, 4, 3, 2] -> [0, 1, 2, 3, 4]
     */

    //Solution1: (If you just want to print the number of steps)
    //-> 1. You can use BFS. At each level swap with every other element and process it.
    //-> 2. Length of a cycle -1 gives you the min swaps required. Do this for every element.
    //
    //Solution2: (If you want to keep track of path)
    //--> DFS is the better option.

    //using BFS now
    public static void main(String[] args) {

        int[] arr = {0, 3, 4, 1, 2};

        int ans = minSwaps(arr);
        System.out.println(ans);
    }

    private static int minSwaps(int[] arr) {
        boolean[] visited = new boolean[arr.length];
        int ans = 0;

        for (int i = 0; i < arr.length; i++) {

            if (!visited[i]) {
                visited[i] = true;
                int j = i;
                int len = 1; // length of cycle;

                while (arr[j] != i) {
                    j = arr[j];
                    visited[j] = true;
                    len++;
                }
                ans += len - 1;// Calculate the number of swaps needed to fix the cycle -> using the logic of for n-length cycle -> n-1 swaps is required

            }
        }
        return ans;
    }
}

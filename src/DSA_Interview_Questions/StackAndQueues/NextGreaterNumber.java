package DSA_Interview_Questions.StackAndQueues;

import java.util.Arrays;
import java.util.Stack;

public class NextGreaterNumber {
    /**
     * Given an array, find the next greater element in it such that for every number x, y is the 1st greater number to its right
     * if it doesn't exist return -1
     */

    public static void main(String[] args) {
        //int[] arr = {7, 6, 3, 8, 2, 11, 30, 5, 25};
        int[] arr = {4, 5, 2, 25, 3, 7, 1};
        arr = nextGreaterElement(arr);
        Arrays.stream(arr).forEach(i -> {
            System.out.print(i);
            System.out.print(" ");
        });
    }

    private static int[] nextGreaterElement(int[] arr) {
        Stack<Integer> stack = new Stack<>();
        int[] ans = new int[arr.length];

        for (int i = arr.length - 1; i >= 0; i--) {
            //poping all smaller elements
            while (stack.size() > 0 && stack.peek() < arr[i]) {
                stack.pop();
            }

            if (stack.isEmpty()) {
                ans[i] = -1; // no greater element towards the right
            } else {
                ans[i] = stack.peek(); //1st greater element towards the right
            }

            //adding the current element in the stack
            stack.push(arr[i]);
        }
        return ans;
    }
}

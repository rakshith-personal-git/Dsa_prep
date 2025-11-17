package DSA_Interview_Questions.StackAndQueues;

import java.util.Arrays;
import java.util.Stack;

public class NextGreaterNumber2 {
    /**
     * Given a circular integer array nums (i.e., the next element of nums[nums.length - 1] is nums[0]), return the next greater number for every element in nums.
     * The next greater number of a number x is the first greater number to its traversing-order next in the array, which means you could search circularly to find its next greater number. If it doesn't exist, return -1 for this number.
     * <p>
     * Example 1:
     * Input: nums = [1,2,1]
     * Output: [2,-1,2]
     * Explanation: The first 1's next greater number is 2;
     * The number 2 can't find next greater number.
     * The second 1's next greater number needs to search circularly, which is also 2.
     * <p>
     * Example 2:
     * Input: nums = [1,2,3,4,3]
     * Output: [2,3,4,-1,4]
     * <p>
     * Constraints:
     * 1 <= nums.length <= 104
     * -109 <= nums[i] <= 109
     */

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 3};
        int[] ans = nextGreaterElements(nums);
        Arrays.stream(ans).forEach(i -> {
            System.out.print(i);
            System.out.print(" ");
        });
    }

    /**
     * Approach:
     * We start by initializing a stack and a vector to store the answer. We loop through the array from the end,
     * twice the size of the array, to simulate a circular array. For each element, we compare it with the top element in the stack.
     * If the top element in the stack is smaller than the current element, we pop it from the stack until the top element is greater than the current element or the stack becomes empty.
     * If the stack is not empty, we store the top element as the next greater element for the current element, otherwise, we store -1. Finally, we push the current element into the stack.
     */
    private static int[] nextGreaterElements(int[] nums) {
        int n = nums.length;
        Stack<Integer> stack = new Stack<>();
        int[] ans = new int[n];
        Arrays.fill(ans, -1);

        for (int i = 2 * n - 1; i >= 0; i--) {
            while (stack.size() > 0 && stack.peek() <= nums[i % n]) {
                stack.pop();
            }
            if (i < n) {
                if (stack.isEmpty()) {
                    ans[i] = -1; // no greater element towards the right
                } else {
                    ans[i] = stack.peek(); //1st greater element towards the right
                }
            }
            //adding the current element in the stack
            stack.push(nums[i % n]);
        }
        return ans;
    }
}

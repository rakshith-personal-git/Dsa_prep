package DSA_Interview_Questions.Grind150;

import java.util.Stack;

public class DS_LargestRectangleInHistogram {
    /**
     * Given an array of integers heights representing the histogram's bar height where the width of each bar is 1,
     * return the area of the largest rectangle in the histogram.
     * <p>
     * Example 1:
     * Input: heights = [2,1,5,6,2,3]
     * Output: 10
     * Explanation: The above is a histogram where width of each bar is 1.
     * The largest rectangle is shown in the red area, which has an area = 10 units.
     * <p>
     * Example 2:
     * Input: heights = [2,4]
     * Output: 4
     * <p>
     * Constraints:
     * 1 <= heights.length <= 105
     * 0 <= heights[i] <= 104
     */

    public static void main(String[] args) {
        int[] heights = {2, 1, 5, 6, 2, 3};
        System.out.println(largestRectangleArea(heights));
        System.out.println(largestRectangleAreaOptimised(heights));
    }

    private static int largestRectangleAreaOptimised(int[] heights) {
        int n = heights.length;
        if (n == 0) {
            return 0;
        }
        int[] left = new int[n]; //left boundary
        int[] right = new int[n]; //right boundary

        left[0] = -1;
        right[n - 1] = n;

        for (int i = 1; i < n; i++) {
            int prev = i - 1;
            while (prev >= 0 && heights[prev] >= heights[i]) {
                prev = left[prev];
            }
            left[i] = prev;
            //sets left[i] to the index of the nearest smaller element to the left of heights[i].
            //If no such smaller element exists, setting left[i] to -1.
        }

        for (int i = n - 2; i >= 0; i--) {
            int prev = i + 1;
            while (prev < n && heights[prev] >= heights[i]) {
                prev = right[prev];
            }
            right[i] = prev;
            //sets right[i] to the index of the nearest smaller element to the right of heights[i].
            //If no such smaller element exists, it sets right[i] to n, the length of the heights array.
        }

        // once we have these two arrays fill we need width & area
        int maxArea = 0;
        for (int i = 0; i < n; i++) {
            int width = right[i] - left[i] - 1;
            maxArea = Math.max(maxArea, heights[i] * width);
        }
        return maxArea;
    }


    private static int largestRectangleArea(int[] heights) {
        int n = heights.length;
        int maxArea = 0;
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i <= n; i++) {
            int currentHeight = i == n ? 0 : heights[i];

            // check if currentHeight becomes greater than height[top] element of stack. we do a push because it's an increasing sequence
            // otherwise we do pop and find area, so for that we write a while loop

            while (!stack.isEmpty() && currentHeight < heights[stack.peek()]) {
                int top = stack.pop();
                int width = stack.isEmpty() ? i : i - stack.peek() - 1; // width differ based on if stack is empty or not empty after we pop the element
                int area = heights[top] * width;
                maxArea = Math.max(maxArea, area);
            }
            // if it doesn't enter in while loop, it means it's an increasing sequence & we need to push index
            stack.push(i);
        }
        return maxArea;
    }


}

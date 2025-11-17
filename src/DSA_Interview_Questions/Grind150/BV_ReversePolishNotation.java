package DSA_Interview_Questions.Grind150;

import java.util.Objects;
import java.util.Stack;

public class BV_ReversePolishNotation {
    /**
     * You are given an array of strings tokens that represents an arithmetic expression in a Reverse Polish Notation.
     * Evaluate the expression. Return an integer that represents the value of the expression.
     * <p>
     * Note that:
     * The valid operators are '+', '-', '*', and '/'.
     * Each operand may be an integer or another expression.
     * The division between two integers always truncates toward zero.
     * There will not be any division by zero.
     * The input represents a valid arithmetic expression in a reverse polish notation.
     * The answer and all the intermediate calculations can be represented in a 32-bit integer.
     * <p>
     * Example 1:
     * Input: tokens = ["2","1","+","3","*"]
     * Output: 9
     * Explanation: ((2 + 1) * 3) = 9
     * <p>
     * Example 2:
     * Input: tokens = ["4","13","5","/","+"]
     * Output: 6
     * Explanation: (4 + (13 / 5)) = 6
     * <p>
     * Example 3:
     * Input: tokens = ["10","6","9","3","+","-11","*","/","*","17","+","5","+"]
     * Output: 22
     * Explanation: ((10 * (6 / ((9 + 3) * -11))) + 17) + 5
     * = ((10 * (6 / (12 * -11))) + 17) + 5
     * = ((10 * (6 / -132)) + 17) + 5
     * = ((10 * 0) + 17) + 5
     * = (0 + 17) + 5
     * = 17 + 5
     * = 22
     * <p>
     * Constraints:
     * 1 <= tokens.length <= 104
     * tokens[i] is either an operator: "+", "-", "*", or "/", or an integer in the range [-200, 200].
     */

    public static void main(String[] args) {
        String[] tokens = {"10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"};
        int ans = evalRPN(tokens);
        System.out.println(ans);
    }

    private static int evalRPN(String[] tokens) {
        Stack<Integer> stack = new Stack<>();
        for (String token : tokens) {
            if (Objects.equals(token, "+")) {
                int num1 = stack.pop();
                int num2 = stack.pop();
                stack.push( num2 + num1);
            } else if (Objects.equals(token, "-")) {
                int num1 = stack.pop();
                int num2 = stack.pop();
                stack.push( num2 - num1);
            } else if (Objects.equals(token, "*")) {
                int num1 = stack.pop();
                int num2 = stack.pop();
                stack.push( num2 * num1);
            } else if (Objects.equals(token, "/")) {
                int num1 = stack.pop();
                int num2 = stack.pop();
                stack.push( num2 / num1);
            } else {
                stack.push(Integer.valueOf(token));
            }
        }
        return stack.peek();
    }

    //Time Complexity: O(n) - Each token is processed once.
    //Space Complexity: O(n) - The stack can contain at most n/2 operands.
}

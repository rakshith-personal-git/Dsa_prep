package DSA_Interview_Questions.Grind150;

import java.util.Stack;

public class DP_BasicCalculator {
    /**
     * Given a string s representing a valid expression, implement a basic calculator to evaluate it, and return the result of the evaluation.
     * Note: You are not allowed to use any built-in function which evaluates strings as mathematical expressions, such as eval().
     * <p>
     * Example 1:
     * Input: s = "1 + 1"
     * Output: 2
     * <p>
     * Example 2:
     * Input: s = " 2-1 + 2 "
     * Output: 3
     * <p>
     * Example 3:
     * Input: s = "(1+(4+5+2)-3)+(6+8)"
     * Output: 23
     * <p>
     * Constraints:
     * 1 <= s.length <= 3 * 105
     * s consists of digits, '+', '-', '(', ')', and ' '.
     * s represents a valid expression.
     * '+' is not used as a unary operation (i.e., "+1" and "+(2 + 3)" is invalid).
     * '-' could be used as a unary operation (i.e., "-1" and "-(2 + 3)" is valid).
     * There will be no two consecutive operators in the input.
     * Every number and running calculation will fit in a signed 32-bit integer.
     */

    public static void main(String[] args) {
//        String s = "(1+(4+5+2)-3)+(6+8)";
        String s = "2147483647";
        System.out.println(calculate(s));
    }

    private static int calculate(String s) {
        Stack<Integer> stack = new Stack<>();
        int result = 0;
        int number = 0;
        int sign = 1;
        char[] characterList = s.toCharArray();
        for (char c : characterList) {


            if (Character.isDigit(c)) {
                number = number * 10 + (c - '0');
            } else {
                switch (c) {
                    case '+':
                        result += sign * number;
                        number = 0;
                        sign = 1;
                        break;
                    case '-':
                        result += sign * number;
                        number = 0;
                        sign = -1;
                        break;
                    case '(':
                        //push the result first and then the sign
                        stack.push(result);
                        stack.push(sign);
                        //reset the sign and result for the value in the parenthesis
                        result = 0;
                        sign = 1;
                        break;
                    case ')':
                        result += sign * number;
                        number = 0;
                        result *= stack.pop(); //sign before the parenthesis
                        result += stack.pop(); //result calculated before the parenthesis
                        break;
                }
            }
        }
        if (number != 0) {
            result += sign * number;
        }
        return result;
    }
}

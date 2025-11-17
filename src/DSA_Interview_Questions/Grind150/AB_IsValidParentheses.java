package DSA_Interview_Questions.Grind150;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AB_IsValidParentheses {

    /**
     * Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.
     * An input string is valid if:
     * Open brackets must be closed by the same type of brackets.
     * Open brackets must be closed in the correct order.
     * Every close bracket has a corresponding open bracket of the same type.
     * <p>
     * Example 1:
     * Input: s = "()"
     * Output: true
     * <p>
     * Example 2:
     * Input: s = "()[]{}"
     * Output: true
     * <p>
     * Example 3:
     * Input: s = "(]"
     * Output: false
     * <p>
     * Constraints:
     * 1 <= s.length <= 104
     * s consists of parentheses only '()[]{}'.
     */


    public static void main(String[] args) {
        String s = "()({}[])";
        boolean b = isValid(s);
        System.out.println("answer " + b);
    }


    public static boolean isValid(String s) {
        char openParentheses = '(';
        char closeParentheses = ')';
        char openFlowerBracket = '{';
        char closeFlowerBracket = '}';
        char openSquaredBracket = '[';
        char closeSquaredBracket = ']';

        char[] charArray = s.toCharArray();
        if (charArray.length == 0 || charArray.length % 2 != 0) {
            return false;
        } else {
            int lastChar = s.indexOf(charArray.length - 1);
            int firstChar = s.indexOf(0);
            if (lastChar == openParentheses || lastChar == openSquaredBracket || lastChar == openFlowerBracket ||
                    firstChar == closeParentheses || firstChar == closeFlowerBracket || firstChar == closeSquaredBracket) {
                return false;
            }
        }

        Map<Character, Character> matchingPairs = new HashMap<>();
        matchingPairs.put(')', '(');
        matchingPairs.put('}', '{');
        matchingPairs.put(']', '[');

        Deque<Character> deque = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (matchingPairs.containsValue(c)) { //basically if it's an opening bracket, push the corresponding closing bracket to deque
                deque.push(c);
            } else if (matchingPairs.containsKey(c)) { //if it's a closing bracket, then the next pop in deque shd match it
                if (deque.isEmpty() || !Objects.equals(deque.pop(), matchingPairs.get(c))) {
                    return false;
                }
            } else {
                return false; // If there are any invalid characters
            }
        }

        return deque.isEmpty();
    }
    //TC is O(n) and SC is O(n)
}
package DSA_Interview_Questions.Strings;

import java.util.ArrayDeque;
import java.util.Deque;

public class IsValidParentheses {
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

        if (s.toCharArray().length == 0 || s.toCharArray().length % 2 != 0) {
            return false;
        } else {
            int lastCharIndex = s.indexOf(s.toCharArray().length - 1);
            int firstCharIndex = s.indexOf(0);
            if (lastCharIndex == openParentheses || lastCharIndex == openSquaredBracket || lastCharIndex == openFlowerBracket ||
                    firstCharIndex == closeParentheses || firstCharIndex == closeFlowerBracket || firstCharIndex == closeSquaredBracket) {
                return false;
            }
        }
        Deque<Character> deque = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == openParentheses) {
                deque.push(closeParentheses);
            } else if (c == openFlowerBracket) {
                deque.push(closeFlowerBracket);
            } else if (c == openSquaredBracket) {
                deque.push(closeSquaredBracket);
            } else if (deque.isEmpty() || deque.pop() != c) {
                return false;
            }
        }
        return deque.isEmpty();
    }
    //TC is O(n) and SC is O(n)
}
package DSA_Interview_Questions.Grind150;

import java.util.Stack;

public class AZ_BackspaceStringCompare {
    /***
     * Given two strings s and t, return true if they are equal when both are typed into empty text editors. '#' means a backspace character.
     * Note that after backspacing an empty text, the text will continue empty.
     *
     * Example 1:
     * Input: s = "ab#c", t = "ad#c"
     * Output: true
     * Explanation: Both s and t become "ac".
     *
     * Example 2:
     * Input: s = "ab##", t = "c#d#"
     * Output: true
     * Explanation: Both s and t become "".
     *
     * Example 3:
     * Input: s = "a#c", t = "b"
     * Output: false
     * Explanation: s becomes "c" while t becomes "b".
     *
     * Constraints:
     * 1 <= s.length, t.length <= 200
     * s and t only contain lowercase letters and '#' characters.
     *
     * Follow up: Can you solve it in O(n) time and O(1) space?
     * */

    public static void main(String[] args) {
        String  s = "ab##";
        String t = "c#d#";
        boolean ans = backspaceCompare(s, t);
        System.out.println(ans);
        ans = backspaceCompare2(s, t);
        System.out.println(ans);
    }

    private static boolean backspaceCompare(String s, String t) {
        Stack<Character> ss = new Stack();
        Stack<Character> tt = new Stack();

        for(char ch : s.toCharArray()) {
            if(ch == '#') {
                if(!ss.isEmpty()) {
                    ss.pop();
                }
            } else {
                ss.push(ch);
            }
        }

        for(char ch : t.toCharArray()) {
            if(ch == '#') {
                if(!tt.isEmpty()) {
                    tt.pop();
                }
            } else {
                tt.push(ch);
            }
        }

        StringBuilder stringBuilderForS = new StringBuilder();
        while(!ss.isEmpty()) {
            stringBuilderForS.append(ss.pop());
        }

        StringBuilder stringBuilderForT = new StringBuilder();
        while(!tt.isEmpty()) {
            stringBuilderForT.append(tt.pop());
        }

        return stringBuilderForS.toString().contentEquals(stringBuilderForT);
    }

    //more optimal way using StringBuilder
    public static boolean backspaceCompare2(String s, String t) {
        return generateStringOfOnlyChar(s).equals(generateStringOfOnlyChar(t));
    }

    private static String generateStringOfOnlyChar(String s){
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c != '#') {
                stringBuilder.append(c);
            } else if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
        }
        return stringBuilder.toString();
    }
}

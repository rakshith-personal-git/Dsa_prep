package DSA_Interview_Questions.Grind150;

import java.util.HashMap;
import java.util.Map;

public class AY_RomanToInteger {
    /**
     * Roman numerals are represented by seven different symbols: I, V, X, L, C, D and M.
     * Symbol       Value
     * I             1
     * V             5
     * X             10
     * L             50
     * C             100
     * D             500
     * M             1000
     * <p>
     * For example, 2 is written as II in Roman numeral, just two ones added together. 12 is written as XII, which is simply X + II. The number 27 is written as XXVII, which is XX + V + II.
     * Roman numerals are usually written largest to smallest from left to right.
     * However, the numeral for four is not IIII. Instead, the number four is written as IV. Because the one is before the five we subtract it making four.
     * The same principle applies to the number nine, which is written as IX. There are six instances where subtraction is used:
     * <p>
     * I can be placed before V (5) and X (10) to make 4 and 9.
     * X can be placed before L (50) and C (100) to make 40 and 90.
     * C can be placed before D (500) and M (1000) to make 400 and 900.
     * Given a roman numeral, convert it to an integer.
     * <p>
     * Example 1:
     * Input: s = "III"
     * Output: 3
     * Explanation: III = 3.
     * <p>
     * Example 2:
     * Input: s = "LVIII"
     * Output: 58
     * Explanation: L = 50, V= 5, III = 3.
     * Example 3:
     * <p>
     * Input: s = "MCMXCIV"
     * Output: 1994
     * Explanation: M = 1000, CM = 900, XC = 90 and IV = 4.
     * <p>
     * Constraints:
     * 1 <= s.length <= 15
     * s contains only the characters ('I', 'V', 'X', 'L', 'C', 'D', 'M').
     * It is guaranteed that s is a valid roman numeral in the range [1, 3999].
     */

    public static void main(String[] args) {
        String s = "MCMXCIV";
        int ans = romanToInt(s);
        System.out.println(ans);
        ans = romanToInt2(s);
        System.out.println(ans);


    }

    //   * I can be placed before V (5) and X (10) to make 4 and 9.
    //     * X can be placed before L (50) and C (100) to make 40 and 90.
    //     * C can be placed before D (500) and M (1000) to make 400 and 900.
    private static int romanToInt(String s) {
        int ans = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            char c = s.charAt(i);
            if (i == 0) {
                ans += getIntForRoman(s.charAt(0));
                continue;
            }
            if (c == 'V' || c == 'X') {
                if (s.charAt(i - 1) == 'I') {
                    ans += (getIntForRoman(c) - 1);
                    i--;
                } else {
                    ans += getIntForRoman(c);
                }
            } else if (c == 'L' || c == 'C') {
                if (s.charAt(i - 1) == 'X') {
                    ans += (getIntForRoman(c) - 10);
                    i--;
                } else {
                    ans += getIntForRoman(c);
                }
            } else if (c == 'D' || c == 'M') {
                if (s.charAt(i - 1) == 'C') {
                    ans += (getIntForRoman(c) - 100);
                    i--;
                } else {
                    ans += getIntForRoman(c);
                }
            } else {
                ans += getIntForRoman(c);
            }
        }
        return ans;
    }


    private static int getIntForRoman(char c) {
        switch (c) {
            case 'I':
                return 1;
            case 'V':
                return 5;
            case 'X':
                return 10;
            case 'L':
                return 50;
            case 'C':
                return 100;
            case 'D':
                return 500;
            case 'M':
                return 1000;
        }
        return 0;
    }

    //another approach
    //The key intuition lies in the fact that in Roman numerals, when a smaller value appears before a larger value,
    // it represents subtraction, while when a smaller value appears after or equal to a larger value, it represents addition.
    public static int romanToInt2(String s) {
        Map<Character, Integer> m = new HashMap<>();

        m.put('I', 1);
        m.put('V', 5);
        m.put('X', 10);
        m.put('L', 50);
        m.put('C', 100);
        m.put('D', 500);
        m.put('M', 1000);

        int ans = 0;

        for (int i = 0; i < s.length(); i++) {
            if (i < s.length() - 1 && m.get(s.charAt(i)) < m.get(s.charAt(i + 1))) {
                ans -= m.get(s.charAt(i));
            } else {
                ans += m.get(s.charAt(i));
            }
        }

        return ans;
    }
}

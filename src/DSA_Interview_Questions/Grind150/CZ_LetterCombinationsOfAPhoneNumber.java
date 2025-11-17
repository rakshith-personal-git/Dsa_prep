package DSA_Interview_Questions.Grind150;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CZ_LetterCombinationsOfAPhoneNumber {
    /**
     * Given a string containing digits from 2-9 inclusive, return all possible letter combinations that the number could represent.
     * Return the answer in any order.
     * A mapping of digits to letters (just like on the telephone buttons) is given below. Note that 1 does not map to any letters.
     * <p>
     * Example 1:
     * Input: digits = "23"
     * Output: ["ad","ae","af","bd","be","bf","cd","ce","cf"]
     * <p>
     * Example 2:
     * Input: digits = ""
     * Output: []
     * <p>
     * Example 3:
     * Input: digits = "2"
     * Output: ["a","b","c"]
     * <p>
     * Constraints:
     * 0 <= digits.length <= 4
     * digits[i] is a digit in the range ['2', '9'].
     **/

    public static void main(String[] args) {
        String digits = "23";
        List<String> ans = letterCombinations(digits);
        ans.forEach(i -> System.out.println(i + " "));

    }

    private static List<String> letterCombinations(String digits) {
        if (digits == null || digits.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Character, String> map = new HashMap<>();
        map.put('2', "abc");
        map.put('3', "def");
        map.put('4', "ghi");
        map.put('5', "jkl");
        map.put('6', "mno");
        map.put('7', "pqrs");
        map.put('8', "tuv");
        map.put('9', "wxyz");


        List<String> result = new ArrayList<>();
        backtrack(result, map, 0, digits, new StringBuilder(""));
        //backtrack("", digits, map, result); //another way
        return result;
    }


    private static void backtrack(List<String> result, Map<Character, String> map, int currentIndex, String digits, StringBuilder currentCombination) {
        if (currentIndex == digits.length()) {
            result.add(currentCombination.toString());
            return;
        }
        String stringToConsider = map.get(digits.charAt(currentIndex));
        for (int j = 0; j < stringToConsider.length(); j++) {
            currentCombination.append(stringToConsider.charAt(j));
            backtrack(result, map, currentIndex + 1, digits, currentCombination);
            currentCombination.deleteCharAt(currentCombination.length() - 1);
        }
    }

    //another way
    private static void backtrack(String combination, String nextDigits, Map<Character, String> map, List<String> result) {
        if (nextDigits.isEmpty()) {
            result.add(combination);
        } else {
            String letters = map.get(nextDigits.charAt(0));
            for (char letter : letters.toCharArray()) {
                backtrack(combination + letter, nextDigits.substring(1), map, result);
            }
        }
    }


}

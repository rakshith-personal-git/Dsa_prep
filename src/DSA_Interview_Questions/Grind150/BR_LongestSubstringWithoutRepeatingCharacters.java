package DSA_Interview_Questions.Grind150;

import java.util.HashMap;
import java.util.Map;

public class BR_LongestSubstringWithoutRepeatingCharacters {

    /**
     *Given a string s, find the length of the longest substring without repeating characters.
     *
     * Example 1:
     * Input: s = "abcabcbb"
     * Output: 3
     * Explanation: The answer is "abc", with the length of 3.
     *
     * Example 2:
     * Input: s = "bbbbb"
     * Output: 1
     * Explanation: The answer is "b", with the length of 1.
     *
     * Example 3:
     * Input: s = "pwwkew"
     * Output: 3
     * Explanation: The answer is "wke", with the length of 3.
     * Notice that the answer must be a substring, "pwke" is a subsequence and not a substring.
     *
     * Constraints:
     * 0 <= s.length <= 5 * 104
     * s consists of English letters, digits, symbols and spaces.
     * */

    public static void main(String[] args) {
        String s = "dvdf";
        int ans = lengthOfLongestSubstring(s);
        System.out.println(ans);
    }

    // sliding window approach
    private static int lengthOfLongestSubstring(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        Map<Character, Integer> map = new HashMap<>();

        int startingIndex = 0;
        int maxLength = 0;

        for (int endingIndex = 0; endingIndex < s.length(); endingIndex ++) {
            char character = s.charAt(endingIndex);
            if (map.containsKey(character) && map.get(character) >= startingIndex) {
                startingIndex = map.get(character) + 1;
            }
            map.put(character, endingIndex);
            maxLength = Math.max(maxLength, endingIndex - startingIndex + 1);
        }

        return maxLength;
    }

    //TC is O(N)
    //SC is O(N)
}

package DSA_Interview_Questions.Grind150;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DB_FindAllAnagramsInAString {
    /**
     * Given two strings s and p, return an array of all the start indices of p's anagrams in s. You may return the answer in any order.
     * An Anagram is a word or phrase formed by rearranging the letters of a different word or phrase, typically using all the original letters exactly once.
     * <p>
     * Example 1:
     * Input: s = "cbaebabacd", p = "abc"
     * Output: [0,6]
     * Explanation:
     * The substring with start index = 0 is "cba", which is an anagram of "abc".
     * The substring with start index = 6 is "bac", which is an anagram of "abc".
     * <p>
     * Example 2:
     * Input: s = "abab", p = "ab"
     * Output: [0,1,2]
     * Explanation:
     * The substring with start index = 0 is "ab", which is an anagram of "ab".
     * The substring with start index = 1 is "ba", which is an anagram of "ab".
     * The substring with start index = 2 is "ab", which is an anagram of "ab".
     * <p>
     * Constraints:
     * 1 <= s.length, p.length <= 3 * 104
     * s and p consist of lowercase English letters.
     */

    public static void main(String[] args) {
        String s = "cbaebabacd";
        String p = "abc";
        List<Integer> ans = findAnagrams(s, p);
        ans.forEach(i -> System.out.printf(i + " "));

    }

    private static List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();

        if (s == null || s.length() == 0 || p == null || p.length() == 0 || s.length() < p.length()) {
            return result;
        }


        int[] frequencyP = new int[26];
        int[] frequencyS = new int[26];

        for (int i = 0; i < p.length(); i++) {
            frequencyP[p.charAt(i) - 'a']++;
            frequencyS[s.charAt(i) - 'a']++;
        }

        int start = 0;
        int end = p.length();

        if (Arrays.equals(frequencyS, frequencyP)){
            result.add(0);//since both s and p are matching and equal(without the order of characters)
        }

        while (end < s.length()) {

            frequencyS[s.charAt(start) - 'a']--;
            frequencyS[s.charAt(end)- 'a']++;

            start++;
            end++;

            if (Arrays.equals(frequencyS, frequencyP)){
                result.add(start);
            }
        }

        return result;


    }
}

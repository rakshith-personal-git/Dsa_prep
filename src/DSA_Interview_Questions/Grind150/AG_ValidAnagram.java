package DSA_Interview_Questions.Grind150;

import java.util.HashMap;
import java.util.Map;

public class AG_ValidAnagram {
    /***
     * Given two strings s and t, return true if t is an anagram of s, and false otherwise.
     * An Anagram is a word or phrase formed by rearranging the letters of a different word or phrase,
     * typically using all the original letters exactly once.
     *
     *
     *
     * Example 1:
     * Input: s = "anagram", t = "nagaram"
     * Output: true
     *
     * Example 2:
     * Input: s = "rat", t = "car"
     * Output: false
     *
     *
     * Constraints:
     * 1 <= s.length, t.length <= 5 * 104
     * s and t consist of lowercase English letters.
     *
     * Follow up: What if the inputs contain Unicode characters? How would you adapt your solution to such a case?
     * */

    public static void main(String[] args) {
        String s = "anagram";
        String t = "nagaram";
//        String s = "aacc";
//        String t = "ccac";
        boolean ans = isAnagram(s, t);
        boolean ans2 = isAnagram2(s, t); //optimal solution
        System.out.println(ans);
        System.out.println(ans2);
    }

    //simple solution using map
    private static boolean isAnagram(String s, String t) {
        if (s == null) {
            return t == null;
        } else {
            if (t == null) {
                return false;
            }
        }

        if (s.length() != t.length()) {
            return false;
        }

        Map<Character, Integer> map = new HashMap<>();
        for (char c : s.toCharArray()) {
            if (map.containsKey(c)) {
                map.put(c, map.get(c) + 1);
            } else {
                map.put(c, 1);
            }
        }

        for (char c : t.toCharArray()) {
            if (map.containsKey(c)) {
                map.put(c, map.get(c) - 1);
            } else {
                return false;
            }
        }

        for (int i : map.values()) {
            if (i != 0) {
                return false;
            }
        }
        return true;
    }



    //Best optimal solution by taking advantage of constraint that every letter is a lower case
    public static boolean isAnagram2(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        // Use an array to count character frequencies
        int[] charCount = new int[26];


        // Update frequency array for string s
        for (char c : s.toCharArray()) {
            charCount[c - 'a']++;
        }

        //we are subtracting from smaller 'a' since its value starts from 97 and all the characters given are lower case which is b/w 97 and 123 value


        // Update frequency array for string t
        for (char c : t.toCharArray()) {
            charCount[c - 'a']--;
        }

        // Check if all counts in the array are zero
        for (int count : charCount) {
            if (count != 0) {
                return false;
            }
        }

        return true;
    }
    //Summary
    //Time Complexity (TC): O(n), where n is the length of the input strings.
    //Space Complexity (SC): O(1), assuming a fixed alphabet size (e.g., only lowercase English letters so size of 26).
}

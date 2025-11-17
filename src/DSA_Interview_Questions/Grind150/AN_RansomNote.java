package DSA_Interview_Questions.Grind150;

import java.util.HashMap;
import java.util.Map;

public class AN_RansomNote {
    /***
     * Given two strings ransomNote and magazine, return true if ransomNote can be constructed by using the letters from magazine and false otherwise.
     * Each letter in magazine can only be used once in ransomNote.
     *
     * Example 1:
     * Input: ransomNote = "a", magazine = "b"
     * Output: false
     *
     * Example 2:
     * Input: ransomNote = "aa", magazine = "ab"
     * Output: false
     *
     * Example 3:
     * Input: ransomNote = "aa", magazine = "aab"
     * Output: true
     *
     * Constraints:
     * 1 <= ransomNote.length, magazine.length <= 105
     * ransomNote and magazine consist of lowercase English letters.
     * */

    public static void main(String[] args) {
        String ransomNote = "aa";
        String magazine = "aab";
        System.out.println(canConstruct(ransomNote, magazine));
        System.out.println(canConstructFaster(ransomNote, magazine));
    }


    private static boolean canConstruct(String ransomNote, String magazine) {
        Map<Character, Integer> map = new HashMap<>();
        for (char c : magazine.toCharArray()) {
            //checks if the key c is already present in the map. If it is, it increments the existing value by 1; otherwise, it puts a new entry with the key c and value 1.
            map.compute(c, (key, value) -> (value == null) ? 1 : value + 1);
        }

        for (char c : ransomNote.toCharArray()) {
            Integer count = map.get(c);
            if (count != null && count > 0) {
                map.put(c, count - 1);
            } else {
                return false;
            }
        }
        return true;
    }


    private static boolean canConstructFaster(String ransomNote, String magazine) {
        int[] array = new int[26];

        //since ransomNote and magazine consist of lowercase English letters. using their char value which is from 97 to 123
        for (char c : magazine.toCharArray()) {
            array[c - 'a']++;
        }

        for (char c : ransomNote.toCharArray()) {
            if (array[c - 'a'] != 0) {
                array[c - 'a']--;
            } else {
                return false;
            }
        }
        return true;
    }
}

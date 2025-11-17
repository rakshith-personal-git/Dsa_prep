package DSA_Interview_Questions.Grind150;

public class DG_MinimumWindowSubstring {
    /**
     * Given two strings s and t of lengths m and n respectively, return the minimum window substring of s such that every character in t (including duplicates)
     * is included in the window. If there is no such substring, return the empty string "".
     * The testcases will be generated such that the answer is unique.
     * <p>
     * Example 1:
     * Input: s = "ADOBECODEBANC", t = "ABC"
     * Output: "BANC"
     * Explanation: The minimum window substring "BANC" includes 'A', 'B', and 'C' from string t.
     * <p>
     * Example 2:
     * Input: s = "a", t = "a"
     * Output: "a"
     * Explanation: The entire string s is the minimum window.
     * <p>
     * Example 3:
     * Input: s = "a", t = "aa"
     * Output: ""
     * Explanation: Both 'a's from t must be included in the window.
     * Since the largest window of s only has one 'a', return empty string.
     * <p>
     * Constraints:
     * m == s.length
     * n == t.length
     * 1 <= m, n <= 105
     * s and t consist of uppercase and lowercase English letters.
     * <p>
     * Follow up: Could you find an algorithm that runs in O(m + n) time?
     */

    public static void main(String[] args) {
        String s = "ADOBECODEBANC";
        String t = "ABC";
        String ans = minWindow(s, t);
        System.out.println(ans);
    }

    private static String minWindow(String s, String t) {
        if (s == null || t == null || s.length() == 0 || t.length() == 0 || s.length() < t.length()) {
            return "";
        }

        int[] countOfCharsInT = new int[128];
        int count = t.length();
        int start = 0;
        int end = 0;
        int startIndex = 0;
        int minLength = Integer.MAX_VALUE;

        for (char c : t.toCharArray()) {
            countOfCharsInT[c]++;
        }

        char[] charInS = s.toCharArray();

        while (end < s.length()) {
            if (countOfCharsInT[charInS[end]] > 0) {
                count--;
            }
            countOfCharsInT[charInS[end]]--;
            end++;

            while (count == 0) {
                if (end - start < minLength) {
                    startIndex = start;
                    minLength = end - start;
                }
                if (countOfCharsInT[charInS[start]] == 0) {
                    count++;
                }
                countOfCharsInT[charInS[start]]++;
                start++;
            }
        }

        if (minLength == Integer.MAX_VALUE) {
            return "";
        } else {
            return s.substring(startIndex, startIndex + minLength);
        }
    }


    /**Approach
     Initialize a character array map of size 128 to store the frequency of characters in string t.
     Initialize variables count, start, end, minLen, and startIndex.
     Iterate through each character in string t and update the character frequency in the map.
     Use two pointers (start and end) to slide the window and find the minimum window that contains all characters from string t.
     Increment end and decrease the frequency in map for each character encountered until all characters from t are present in the window.
     When all characters from t are present, update minLen and startIndex based on the current window size and starting index.
     Increment start and increase the frequency in map until the window no longer contains all characters from t.
     After the iteration, the minimum window is found, and the result is a substring of s starting from startIndex with length minLen.
     Complexity
     Time complexity: O(n)O(n)O(n), where n is the length of string s.
     Space complexity: O(1)O(1)O(1), as the map array has a constant size (128).
     * */
}

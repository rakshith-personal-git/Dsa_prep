package DSA_Interview_Questions.Grind150;

public class CT_LongestPalindromicSubstring {
    /**
     * Given a string s, return the longest palindromic substring in s.
     * <p>
     * Example 1:
     * Input: s = "babad"
     * Output: "bab"
     * Explanation: "aba" is also a valid answer.
     * <p>
     * Example 2:
     * Input: s = "cbbd"
     * Output: "bb"
     * <p>
     * Constraints:
     * 1 <= s.length <= 1000
     * s consist of only digits and English letters.
     */

    public static void main(String[] args) {
        String s = "babad";
        String ans = longestPalindrome(s);
        System.out.println(ans);
    }

    private static String longestPalindrome(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }

        int start = 0;
        int end = 0;

        for (int i = 0; i < s.length(); i++) {
            int odd = expandAroundCentre(s, i, i);
            int even = expandAroundCentre(s, i, i + 1);
            int maxLength = Math.max(odd, even);

            if (maxLength > end - start) {
                start = i - (maxLength - 1) / 2;
                end = i + maxLength / 2;
            }
        }
        return s.substring(start, end + 1);
    }

    private static int expandAroundCentre(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }

    //Say When i = 2 left = 0 and right = 4, we found 5 as a max length, but we don't know 5 is the max length in the current iteration,
    // so we try to move to the next place to find longer palindrome, even if we don't find it in the end.
    //That's why, left and right pointer always overrun and stop at max length in current iteration + 1, so we need to subtract -1 from right - left.
    //But still you don't get it because we have two pointers expanding at the same time? you think we should subtract -2?
    //This is calculation of index number, so index number usually starts from 0 not 1, so right - left includes -1 already

}

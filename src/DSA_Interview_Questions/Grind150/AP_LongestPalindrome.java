package DSA_Interview_Questions.Grind150;

public class AP_LongestPalindrome {
    /**
     * Given a string s which consists of lowercase or uppercase letters, return the length of the longest palindrome that can be built with those letters.
     * Letters are case sensitive, for example, "Aa" is not considered a palindrome here.
     * <p>
     * Example 1:
     * Input: s = "abccccdd"
     * Output: 7
     * Explanation: One longest palindrome that can be built is "dccaccd", whose length is 7.
     * <p>
     * Example 2:
     * Input: s = "a"
     * Output: 1
     * Explanation: The longest palindrome that can be built is "a", whose length is 1.
     * <p>
     * Constraints:
     * 1 <= s.length <= 2000
     * s consists of lowercase and/or uppercase English letters only.
     */

    public static void main(String[] args) {
        String s = "abccccdd";
        int ans = longestPalindrome(s);
        System.out.println(ans);
    }

    //ASCII values of lowercase letters are from 97 to 122, while ASCII values of uppercase letters are from 65 to 90.
    private static int longestPalindrome(String s) {
        int[] charArray = new int[122];
        for (char c : s.toCharArray()) {
            charArray[c]++;
        }

        int result = 0;
        for (int count : charArray) {
            if (count == 0) {
                continue;
            }
            result += count / 2 * 2; //if it's a even count then that equal count of it can be used on either sides of palindrome, hence multiplying it by 2;
            if (result % 2 == 0 && count % 2 == 1) {
                //if the count is odd, then this char can be used as the middle element in the palindrome
                result += 1;
            }
        }

        return result;
    }
}

package DSA_Interview_Questions.Grind150;

import java.util.ArrayList;
import java.util.List;

public class CU_PalindromePartitioning {
    /**
     * Given a string s, partition s such that every substring of the partition is a palindrome
     * Return all possible palindrome partitioning of s.
     *
     * Example 1:
     * Input: s = "aab"
     * Output: [["a","a","b"],["aa","b"]]
     *
     * Example 2:
     * Input: s = "a"
     * Output: [["a"]]
     *
     * Constraints:
     * 1 <= s.length <= 16
     * s contains only lowercase English letters.
     * */

    public static void main(String[] args) {
        String s = "aab";
        List<List<String>> ans = partition(s);
        for (List<String> account : ans) {
            System.out.println(account);
        }
    }

    private static List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), s, 0);
        return result;
    }

    private static void backtrack(List<List<String>> result, List<String> tempList, String s, int start) {
        if (start == s.length()) {
            result.add(new ArrayList<>(tempList));
        } else {
            for (int i = start; i < s.length(); i++) {
                if (isPalindrome(s, start, i)) {
                    tempList.add(s.substring(start, i + 1));
                    backtrack(result, tempList, s, i + 1);
                    tempList.remove(tempList.size() - 1);
                }
            }
        }

    }

    private static boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}

package DSA_Interview_Questions.Grind150;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CM_WordBreak {
    /**
     * Given a string s and a dictionary of strings wordDict,
     * return true if s can be segmented into a space-separated sequence of one or more dictionary words.
     * Note that the same word in the dictionary may be reused multiple times in the segmentation.
     * <p>
     * Example 1:
     * Input: s = "leetcode", wordDict = ["leet","code"]
     * Output: true
     * Explanation: Return true because "leetcode" can be segmented as "leet code".
     * <p>
     * Example 2:
     * Input: s = "applepenapple", wordDict = ["apple","pen"]
     * Output: true
     * Explanation: Return true because "applepenapple" can be segmented as "apple pen apple".
     * Note that you are allowed to reuse a dictionary word.
     * <p>
     * Example 3:
     * Input: s = "catsandog", wordDict = ["cats","dog","sand","and","cat"]
     * Output: false
     * <p>
     * Constraints:
     * 1 <= s.length <= 300
     * 1 <= wordDict.length <= 1000
     * 1 <= wordDict[i].length <= 20
     * s and wordDict[i] consist of only lowercase English letters.
     * All the strings of wordDict are unique.
     */

    public static void main(String[] args) {
        String s = "applepenapple";
        List<String> wordDict = Arrays.asList("apple", "pen");
        System.out.println(wordBreak(s, wordDict));
        System.out.println(wordBreakOptimised(s, wordDict));
    }


    //When given a string and a dictionary of words, the problem requires us to determine if the string can be segmented into a sequence of dictionary words.
    //A common way to approach this problem is to use either Dynamic Programming or Depth-First Search with memoization, considering the constraints and properties of the problem

    private static boolean wordBreak(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true; // since an empty string can always be segmented.

        for (int i = 0; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break; //breaking internal loop
                }
            }
        }

        return dp[s.length()];
    }

    //TC is O(N2), SC is O(N)


    /**
     * Explanation:
     * Consider the example with s = "leetcode" and wordDict = ["leet","code"]. Here's how the algorithm proceeds:
     * Initialization: dp = [True, False, False, False, False, False, False, False, False].
     * Determine Maximum Word Length: max_len = 4.
     * Iterate Through the String:
     * When i = 4, the loop finds that dp[0] is True and "leet" is in the dictionary, so dp[4] is set to True.
     * When i = 8, the loop finds that dp[4] is True and "code" is in the dictionary, so dp[8] is set to True.
     * Result: dp[8] is True, so the function returns True.
     * The use of dynamic programming ensures that we are not recomputing solutions to subproblems, and the consideration of the maximum word length helps in avoiding unnecessary iterations, making this approach both elegant and efficient.
     */

    //this is little optimised, not by a lot
    private static boolean wordBreakOptimised(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true; // since an empty string can always be segmented.

        for (int i = 0; i < s.length(); i++) {
            for (int j = i; j >= 0 && i - j <= 20; j--) {
                //System.out.println(s.substring(j,i+1));
                if (dp[j] && wordSet.contains(s.substring(j, i + 1))) { //(i + 1) because 2nd parameter in substring is exclusive
                    //  System.out.println(i);
                    dp[i + 1] = true;
                    break;
                }
            }
        }

        return dp[s.length()];
    }


}

package DSA_Interview_Questions.Grind150;

import java.util.Arrays;

public class BD_LongestCommonPrefix {
    /**
     * Write a function to find the longest common prefix string amongst an array of strings.
     * If there is no common prefix, return an empty string "".
     * <p>
     * Example 1:
     * Input: strs = ["flower","flow","flight"]
     * Output: "fl"
     * <p>
     * Example 2:
     * Input: strs = ["dog","racecar","car"]
     * Output: ""
     * Explanation: There is no common prefix among the input strings.
     * <p>
     * Constraints:
     * 1 <= strs.length <= 200
     * 0 <= strs[i].length <= 200
     * strs[i] consists of only lowercase English letters.
     **/

    public static void main(String[] args) {
        String[] strings = {"flower", "flow", "flight"};
        String ans = longestCommonPrefix(strings);
        String ans2 = longestCommonPrefix2(strings);
        System.out.println(ans);
        System.out.println(ans2);
    }



    private static String longestCommonPrefix(String[] strings) {
        if (strings == null || strings.length == 0) {
            return "";
        }

        String prefix = strings[0];
        for (int i = 1; i < strings.length; i++) {
            while (strings[i].indexOf(prefix) != 0) {
                //indexOf function returns the index of the first occurrence of the specified substring, or -1 if there is no such occurrence.
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) {
                    return "";
                }
            }
        }
        return prefix;
    }

    //more readable
    /**
     * The reason why we sort the input array of strings and compare the first and last strings is that the longest common prefix of all the strings
     * must be a prefix of the first string and a prefix of the last string in the sorted array.
     * This is because strings are ordered based on their alphabetical order (Lexicographical order).*/
    private static String longestCommonPrefix2(String[] strings) {
        Arrays.sort(strings);
        String s1 = strings[0];
        String s2 = strings[strings.length-1]; //last string in the array
        int idx = 0;
        while(idx < s1.length() && idx < s2.length()){
            if(s1.charAt(idx) == s2.charAt(idx)){
                idx++;
            } else {
                break;
            }
        }
        return s1.substring(0, idx);
    }
    //time complexity is O(Nlog(N) + M).
    //space complexity is O(1)

}

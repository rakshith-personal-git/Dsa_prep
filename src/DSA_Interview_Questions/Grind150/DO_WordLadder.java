package DSA_Interview_Questions.Grind150;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class DO_WordLadder {

    /**
     * A transformation sequence from word beginWord to word endWord using a dictionary wordList is a sequence of words beginWord -> s1 -> s2 -> ... -> sk such that:
     * Every adjacent pair of words differs by a single letter.
     * Every si for 1 <= i <= k is in wordList. Note that beginWord does not need to be in wordList.
     * sk == endWord
     * Given two words, beginWord and endWord, and a dictionary wordList, return the number of words in the shortest transformation sequence
     * from beginWord to endWord, or 0 if no such sequence exists.
     * <p>
     * Example 1:
     * Input: beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log","cog"]
     * Output: 5
     * Explanation: One shortest transformation sequence is "hit" -> "hot" -> "dot" -> "dog" -> cog", which is 5 words long.
     * <p>
     * Example 2:
     * Input: beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log"]
     * Output: 0
     * Explanation: The endWord "cog" is not in wordList, therefore there is no valid transformation sequence.
     * <p>
     * Constraints:
     * 1 <= beginWord.length <= 10
     * endWord.length == beginWord.length
     * 1 <= wordList.length <= 5000
     * wordList[i].length == beginWord.length
     * beginWord, endWord, and wordList[i] consist of lowercase English letters.
     * beginWord != endWord
     * All the words in wordList are unique.
     */

    public static void main(String[] args) {
        String beginWord = "hit";
        String endWord = "cog";
        List<String> wordList = Arrays.asList("hot", "dot", "dog", "lot", "log", "cog");
        System.out.println(ladderLength(beginWord, endWord, wordList));
    }

    private static int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordBank = new HashSet<>(wordList);
        if (!wordBank.contains(endWord)) {
            return 0;
        }

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();


        queue.add(beginWord);
        visited.add(beginWord);
        int level = 1;


        while (!queue.isEmpty()) {
            level++;
            int n = queue.size();
            while (n > 0) {
                n--;
                String s = queue.poll();
                char[] arr = s.toCharArray();
                for (char c = 'a'; c <= 'z'; c++) {
                    for (int i = 0; i < arr.length; i++) {
                        char temp = arr[i];
                        arr[i] = c;
                        String word = new String(arr);
                        if (!visited.contains(word) && wordBank.contains(word)) {
                            if (word.equals(endWord)) {
                                return level;
                            }
                            queue.add(word);
                            visited.add(word);
                        }
                        arr[i] = temp;
                    }
                }

            }

        }
        return 0;

    }

//explanation ->  https://leetcode.com/problems/word-ladder/solutions/1764371/a-very-highly-detailed-explanation/
}

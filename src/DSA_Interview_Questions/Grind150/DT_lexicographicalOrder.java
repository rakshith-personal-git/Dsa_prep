package DSA_Interview_Questions.Grind150;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DT_lexicographicalOrder {
    /**
     * Given an array of strings and a search key, implement a function that returns
     * the three lexicographically the smallest strings in the array that start with the search key.
     */

    public static void main(String[] args) {
        String[] words = {"apple", "app", "apricot", "banana", "bat", "bath", "cat"};
        String searchKey = "ap";
        List<String> result = getWordsWithPrefix(words, searchKey);
        System.out.println(result);
    }


    static class TrieNode {
        Map<Character, TrieNode> children;
        boolean isWord;
        List<String> words;  // Store words to facilitate searching


        TrieNode() {
            children = new HashMap<>();
            isWord = false;
            words = new ArrayList<>();
        }
    }


    private static List<String> getWordsWithPrefix(String[] words, String searchKey) {
        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
        return trie.search(searchKey);
    }

    static class Trie {
        TrieNode root;

        Trie() {
            root = new TrieNode();
        }

        void insert(String word) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                node.children.putIfAbsent(c, new TrieNode());
                node = node.children.get(c);

                node.words.add(word);
                Collections.sort(node.words); // Ensure lexicographical order
                if (node.words.size() > 3) {
                    node.words.remove(node.words.get(3)); // Keep only top 3 lexicographical words
                }
            }
            node.isWord = true;
        }

        public List<String> search(String searchKey) {
            TrieNode node = root;
            for (char c : searchKey.toCharArray()) {
                if (!node.children.containsKey(c)) {
                    return new ArrayList<>();
                }
                node = node.children.get(c);
            }
            return node.words;
        }
    }
    //Time Complexity:
    //Insertion: O(N * L) for inserting all words. (n is number of words, and
    //Search: O(L) for finding the top 3 words with a given prefix.
    //
    // Space Complexity:
    //O(N * L) for the trie structure and word lists.
}

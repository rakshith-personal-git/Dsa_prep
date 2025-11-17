package DSA_Interview_Questions.Grind150;

import java.util.HashMap;
import java.util.Map;

public class BX_ImplementTrie_PrefixTree {
    //todo learn about Trie inDetail, before revising this
    /**
     * A trie (pronounced as "try") or prefix tree is a tree data structure used to efficiently store and retrieve keys in a dataset of strings.
     * There are various applications of this data structure, such as autocomplete and spellchecker.
     * <p>
     * Implement the Trie class:
     * Trie() Initializes the trie object.
     * void insert(String word) Inserts the string word into the trie.
     * boolean search(String word) Returns true if the string word is in the trie (i.e., was inserted before), and false otherwise.
     * boolean startsWith(String prefix) Returns true if there is a previously inserted string word that has the prefix prefix, and false otherwise.
     * <p>
     * Example 1:
     * Input
     * ["Trie", "insert", "search", "search", "startsWith", "insert", "search"]
     * [[], ["apple"], ["apple"], ["app"], ["app"], ["app"], ["app"]]
     * Output
     * [null, null, true, false, true, null, true]
     * <p>
     * Explanation
     * Trie trie = new Trie();
     * trie.insert("apple");
     * trie.search("apple");   // return True
     * trie.search("app");     // return False
     * trie.startsWith("app"); // return True
     * trie.insert("app");
     * trie.search("app");     // return True
     * <p>
     * Constraints:
     * 1 <= word.length, prefix.length <= 2000
     * word and prefix consist only of lowercase English letters.
     * At most 3 * 104 calls in total will be made to insert, search, and startsWith.
     */

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("apple");
        System.out.println(trie.search("apple")); // return True
        System.out.println(trie.search("app"));     // return False
        System.out.println(trie.startsWith("app")); // return True
        trie.insert("app");
        System.out.println(trie.search("app")); //return True
    }

    static class Trie {
        // using map we can extend our solution to other english characters apart from lowercase
        public Map<Character, Trie> children;
        public Character curr;
        // denotes if current character marks end of word
        public boolean isWord = false;

        public Trie() {
            children = new HashMap<>();
            //dummy character for root trie
            curr = '-';
        }

        public Trie(char c) {
            children = new HashMap<>();
            curr = c;
        }


        public void insert(String word) {
            // traversing similar to linked list, but next node is determined by characters of word
            Trie temp = this;
            for (char c : word.toCharArray()) {
                temp.children.putIfAbsent(c, new Trie(c));
                temp = temp.children.get(c);
            }

            // we assign last character trie's isWord property to be true
            temp.isWord = true;
        }

        public boolean search(String word) {
            Trie temp = this;
            for (char c : word.toCharArray()) {
                if (!temp.children.containsKey(c)){
                    return false;
                }
                temp = temp.children.get(c);
            }
            //for word, it is necessary for isWord property to be true
            return temp.isWord;
        }

        public boolean startsWith(String prefix) {
            Trie temp = this;
            for(char ch: prefix.toCharArray()){
                if(!temp.children.containsKey(ch)){
                    return false;
                }
                temp = temp.children.get(ch);
            }
            // for prefix, it is not necessary for isWord property to be true
            return true;
        }

    }

}

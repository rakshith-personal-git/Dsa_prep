package DSA_Interview_Questions.Grind150;

import java.util.HashMap;

public class DE_LRUCache {
    /**
     * Design a data structure that follows the constraints of a Least Recently Used (LRU) cache.
     * Implement the LRUCache class:
     * LRUCache(int capacity) Initialize the LRU cache with positive size capacity.
     * int get(int key) Return the value of the key if the key exists, otherwise return -1.
     * void put(int key, int value) Update the value of the key if the key exists. Otherwise, add the key-value pair to the cache. If the number of keys exceeds the capacity from this operation, evict the least recently used key.
     * The functions get and put must each run in O(1) average time complexity.
     * <p>
     * Example 1:
     * Input
     * ["LRUCache", "put", "put", "get", "put", "get", "put", "get", "get", "get"]
     * [[2], [1, 1], [2, 2], [1], [3, 3], [2], [4, 4], [1], [3], [4]]
     * Output
     * [null, null, null, 1, null, -1, null, -1, 3, 4]
     * <p>
     * Explanation
     * LRUCache lRUCache = new LRUCache(2);
     * lRUCache.put(1, 1); // cache is {1=1}
     * lRUCache.put(2, 2); // cache is {1=1, 2=2}
     * lRUCache.get(1);    // return 1
     * lRUCache.put(3, 3); // LRU key was 2, evicts key 2, cache is {1=1, 3=3}
     * lRUCache.get(2);    // returns -1 (not found)
     * lRUCache.put(4, 4); // LRU key was 1, evicts key 1, cache is {4=4, 3=3}
     * lRUCache.get(1);    // return -1 (not found)
     * lRUCache.get(3);    // return 3
     * lRUCache.get(4);    // return 4
     * <p>
     * Constraints:
     * 1 <= capacity <= 3000
     * 0 <= key <= 104
     * 0 <= value <= 105
     * At most 2 * 105 calls will be made to get and put.
     */


    public static void main(String[] args) {
        LRUCache lRUCache = new LRUCache(2);
        lRUCache.put(1, 1); // cache is {1=1}
        lRUCache.put(2, 2); // cache is {1=1, 2=2}
        System.out.println(lRUCache.get(1));    // return 1
        lRUCache.put(3, 3); // LRU key was 2, evicts key 2, cache is {1=1, 3=3}
        System.out.println(lRUCache.get(2));    // returns -1 (not found)
        lRUCache.put(4, 4); // LRU key was 1, evicts key 1, cache is {4=4, 3=3}
        System.out.println(lRUCache.get(1));    // return -1 (not found)
        System.out.println(lRUCache.get(3));    // return 3
        System.out.println(lRUCache.get(4));    // return 4
    }

    static class LRUCache {

        class Node {
            int key;
            int value;
            Node previousNode;
            Node nextNode;

            public Node(int key, int value) {
                this.key = key;
                this.value = value;
            }
        }

        int capacity;
        HashMap<Integer, Node> map = new HashMap<>();
        Node head = new Node(-1, -1);
        Node tail = new Node(-1, -1);



        public LRUCache(int cap) {
            capacity = cap;
            head.nextNode = tail;
            tail.previousNode = head;
        }

        public int get(int key) {
            if (map.containsKey(key)) {
                Node tempNode = map.get(key);
                int ans = tempNode.value;

                map.remove(key);
                deleteNode(tempNode);
                addNode(tempNode);

                map.put(key, head.nextNode);
                return ans;
            }
            return -1;
        }


        public void put(int key, int value) {
            if (map.containsKey(key)) {
                Node current = map.get(key);
                map.remove(key);
                deleteNode(current);
            }

            if (map.size() == capacity) {
                map.remove(tail.previousNode.key);
                deleteNode(tail.previousNode);
            }

            addNode(new Node(key, value));
            map.put(key, head.nextNode);

        }

        private void deleteNode(Node deleteNode) {
            Node previousNode = deleteNode.previousNode;
            Node nextNode = deleteNode.nextNode;

            previousNode.nextNode = nextNode;
            nextNode.previousNode = previousNode;
        }


        private void addNode(Node newNode) {
            Node temp = head.nextNode;

            head.nextNode = newNode;
            newNode.previousNode = head;

            newNode.nextNode = temp;
            temp.previousNode = newNode;

            //basically adding this element as next element after the head node.
        }

    }
}

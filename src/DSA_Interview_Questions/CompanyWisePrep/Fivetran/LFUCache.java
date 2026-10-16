package DSA_Interview_Questions.CompanyWisePrep.Fivetran;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic Least Frequently Used (LFU) Cache
 *
 * @param <K> Generic Key Type
 * @param <V> Generic Value Type
 * 
 * STRATEGY: HashMap + Frequency-Indexed Doubly Linked Lists
 * - Achieves O(1) time complexity for both get() and put() operations.
 * - Resolves frequency ties using Least Recently Used (LRU) order via DLL tail eviction.
 */
public class LFUCache<K, V> {

    /**
     * Doubly Linked List Node containing Key, Value, and Frequency state.
     */
    private static class Node<K, V> {
        K key;
        V val;
        int freq;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V val) {
            this.key = key;
            this.val = val;
            this.freq = 1;
        }
    }

    /**
     * Custom Doubly Linked List for tracking LRU ordering within a single frequency bucket.
     */
    private class DoublyLinkedList {
        Node<K, V> head;
        Node<K, V> tail;
        int size;

        DoublyLinkedList() {
            // Sentinel nodes to eliminate null checks during insertion/removal
            head = new Node<>(null, null);
            tail = new Node<>(null, null);
            head.next = tail;
            tail.prev = head;
            size = 0;
        }

        void addFirst(Node<K, V> node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
            size++;
        }

        void remove(Node<K, V> node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }

        Node<K, V> removeTail() {
            if (size > 0) {
                Node<K, V> lruNode = tail.prev;
                remove(lruNode);
                return lruNode;
            }
            return null;
        }
    }

    private final int capacity;
    private int minFreq;
    private final Map<K, Node<K, V>> keyToNode;
    private final Map<Integer, DoublyLinkedList> freqToList;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyToNode = new HashMap<>();
        this.freqToList = new HashMap<>();
    }

    /**
     * Retrieves value associated with key and increments its access frequency in O(1).
     */
    public V get(K key) {
        Node<K, V> node = keyToNode.get(key);
        if (node == null) {
            return null;
        }
        updateFrequency(node);
        return node.val;
    }

    /**
     * Inserts or updates key-value pair in O(1).
     * Evicts LFU (and LRU tied) key when capacity is exceeded.
     */
    public void put(K key, V value) {
        if (capacity <= 0) {
            return;
        }

        // Case 1: Key already exists -> Update value and increment frequency
        if (keyToNode.containsKey(key)) {
            Node<K, V> node = keyToNode.get(key);
            node.val = value;
            updateFrequency(node);
            return;
        }

        // Case 2: Cache is full -> Evict LFU (LRU tied) node
        if (keyToNode.size() >= capacity) {
            DoublyLinkedList minList = freqToList.get(minFreq);
            Node<K, V> evictedNode = minList.removeTail();
            if (evictedNode != null) {
                keyToNode.remove(evictedNode.key);
            }
        }

        // Case 3: Insert new node with initial frequency = 1
        Node<K, V> newNode = new Node<>(key, value);
        keyToNode.put(key, newNode);
        freqToList.computeIfAbsent(1, k -> new DoublyLinkedList()).addFirst(newNode);
        minFreq = 1; // Reset minFreq to 1 for new insertions
    }

    /**
     * Helper to promote a node to its next frequency level in O(1).
     */
    private void updateFrequency(Node<K, V> node) {
        int oldFreq = node.freq;
        DoublyLinkedList oldList = freqToList.get(oldFreq);
        oldList.remove(node);

        // If current minFreq list becomes empty, advance minFreq
        if (oldFreq == minFreq && oldList.size == 0) {
            minFreq++;
        }

        node.freq++;
        freqToList.computeIfAbsent(node.freq, k -> new DoublyLinkedList()).addFirst(node);
    }

    public static void main(String[] args) {
        // Demonstration with Generic String keys and User payload values
        LFUCache<String, String> cache = new LFUCache<>(2);

        cache.put("K1", "V1");
        cache.put("K2", "V2");

        System.out.println("Get K1: " + cache.get("K1")); // Returns "V1", K1 freq becomes 2

        cache.put("K3", "V3"); // Capacity full (2). K2 has freq 1, K1 has freq 2. Evicts K2!

        System.out.println("Get K2: " + cache.get("K2")); // Returns null (evicted)
        System.out.println("Get K3: " + cache.get("K3")); // Returns "V3"
    }
}
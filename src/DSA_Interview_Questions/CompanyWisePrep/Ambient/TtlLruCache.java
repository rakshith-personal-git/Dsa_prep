package DSA_Interview_Questions.CompanyWisePrep.Ambient;

import java.util.*;

/**
 * LRU Cache with Millisecond Time-To-Live (TTL).
 *
 * Eviction triggers:
 * 1. Lazy eviction on get() if the item has expired.
 * 2. Standard LRU eviction when capacity is reached.
 *
 * Time Complexities:
 * - get(): O(1)
 * - put(): O(1)
 */
public class TtlLruCache<K, V> {

    private static class Node<K, V> {
        K key;
        V value;
        long expireAtMillis;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value, long expireAtMillis) {
            this.key = key;
            this.value = value;
            this.expireAtMillis = expireAtMillis;
        }

        boolean isExpired(long now) {
            return now > expireAtMillis;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private final Node<K, V> head; // Dummy head (Most Recently Used)
    private final Node<K, V> tail; // Dummy tail (Least Recently Used)

    public TtlLruCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be > 0");
        this.capacity = capacity;
        this.map = new HashMap<>();

        this.head = new Node<>(null, null, Long.MAX_VALUE);
        this.tail = new Node<>(null, null, Long.MAX_VALUE);
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Stores key-value with TTL in milliseconds.
     */
    public synchronized void put(K key, V value, long ttlMillis) {
        long expireAt = System.currentTimeMillis() + ttlMillis;

        if (map.containsKey(key)) {
            Node<K, V> node = map.get(key);
            node.value = value;
            node.expireAtMillis = expireAt;
            moveToHead(node);
            return;
        }

        // Evict LRU node if full
        if (map.size() >= capacity) {
            evictLru();
        }

        Node<K, V> newNode = new Node<>(key, value, expireAt);
        map.put(key, newNode);
        addToHead(newNode);
    }

    /**
     * Returns value if present and not expired; evicts lazily otherwise.
     */
    public synchronized V get(K key) {
        if (!map.containsKey(key)) {
            return null;
        }

        Node<K, V> node = map.get(key);
        long now = System.currentTimeMillis();

        if (node.isExpired(now)) {
            removeNode(node);
            map.remove(key);
            return null; // Expired
        }

        moveToHead(node);
        return node.value;
    }

    private void addToHead(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addToHead(node);
    }

    private void evictLru() {
        Node<K, V> lruNode = tail.prev;
        if (lruNode != head) {
            removeNode(lruNode);
            map.remove(lruNode.key);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TtlLruCache<String, String> cache = new TtlLruCache<>(2);

        // Test 1: TTL Expiration
        cache.put("sensor-1", "normal", 300); // 300ms TTL
        System.out.println("Immediate sensor-1: " + cache.get("sensor-1")); // normal

        Thread.sleep(350);
        System.out.println("Expired sensor-1: " + cache.get("sensor-1")); // null

        // Test 2: Standard LRU Eviction under capacity
        cache.put("cam-1", "online", 5000);
        cache.put("cam-2", "standby", 5000);
        cache.get("cam-1"); // Access cam-1 to make cam-2 LRU

        cache.put("cam-3", "alert", 5000); // Should evict cam-2
        System.out.println("cam-1 (retained): " + cache.get("cam-1")); // online
        System.out.println("cam-2 (evicted): " + cache.get("cam-2"));   // null
        System.out.println("cam-3 (newest): " + cache.get("cam-3"));   // alert
    }
}
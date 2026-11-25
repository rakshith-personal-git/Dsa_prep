package DSA_Interview_Questions.CompanyWisePrep.Ambient;

import java.util.*;

/**
 * In-Memory Key-Value Store supporting Nested Transactions.
 *
 * Supported Operations:
 * - SET(key, value)
 * - GET(key)
 * - DELETE(key)
 * - BEGIN()
 * - COMMIT()
 * - ROLLBACK()
 *
 * Design Architecture:
 * - Base state: globalStore (Map<String, String>).
 * - Active transactions: Stack of transaction frames (Deque>).
 * - Tombstone Pattern: A null value in a transaction map indicates a deleted key.
 */
public class TransactionalKeyValueStore {

    // Global persistent state
    private final Map<String, String> globalStore = new HashMap<>();

    // Stack holding active transaction layers.
    // Each layer maps key -> value (where value == null represents a tombstone/deleted entry).
    private final Deque<Map<String, String>> transactionStack = new ArrayDeque<>();

    /**
     * Starts a new transaction block.
     * Time Complexity: O(1)
     */
    public void begin() {
        transactionStack.push(new HashMap<>());
    }

    /**
     * Sets key to value. If inside a transaction, mutates only the current frame.
     * Time Complexity: O(1)
     */
    public void set(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and Value must not be null");
        }

        if (transactionStack.isEmpty()) {
            globalStore.put(key, value);
        } else {
            transactionStack.peek().put(key, value);
        }
    }

    /**
     * Retrieves key. Searches from top transaction downward to globalStore.
     * Time Complexity: O(T) where T is active transaction depth (typically O(1)).
     */
    public String get(String key) {
        if (key == null) return null;

        // Traverse down from innermost transaction to outermost
        for (Map<String, String> tx : transactionStack) {
            if (tx.containsKey(key)) {
                return tx.get(key); // Returns null if tombstone is present
            }
        }

        return globalStore.get(key);
    }

    /**
     * Deletes a key. Records a tombstone if inside an active transaction.
     * Time Complexity: O(1)
     */
    public void delete(String key) {
        if (key == null) return;

        if (transactionStack.isEmpty()) {
            globalStore.remove(key);
        } else {
            // Tombstone: store null to mask lower layers and globalStore
            transactionStack.peek().put(key, null);
        }
    }

    /**
     * Rolls back changes in the current active transaction.
     * Time Complexity: O(1)
     */
    public void rollback() {
        if (transactionStack.isEmpty()) {
            throw new IllegalStateException("No active transaction to rollback");
        }
        transactionStack.pop();
    }

    /**
     * Commits the current transaction.
     * - If outermost transaction: writes all mutations to globalStore.
     * - If nested transaction: merges mutations up into the parent transaction frame.
     * Time Complexity: O(K) where K is number of keys modified in the transaction.
     */
    public void commit() {
        if (transactionStack.isEmpty()) {
            throw new IllegalStateException("No active transaction to commit");
        }

        Map<String, String> currentTx = transactionStack.pop();

        if (transactionStack.isEmpty()) {
            // Outermost commit -> write to global store
            for (Map.Entry<String, String> entry : currentTx.entrySet()) {
                if (entry.getValue() == null) {
                    globalStore.remove(entry.getKey());
                } else {
                    globalStore.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            // Nested commit -> merge into parent transaction frame
            Map<String, String> parentTx = transactionStack.peek();
            parentTx.putAll(currentTx);
        }
    }

    public static void main(String[] args) {
        TransactionalKeyValueStore kv = new TransactionalKeyValueStore();

        // 1. Basic Global Operations
        kv.set("a", "10");
        System.out.println("Global 'a': " + kv.get("a")); // 10

        // 2. Transaction Rollback
        kv.begin();
        kv.set("a", "20");
        kv.set("b", "30");
        System.out.println("Tx1 'a': " + kv.get("a")); // 20
        System.out.println("Tx1 'b': " + kv.get("b")); // 30
        kv.rollback();
        System.out.println("Post-rollback 'a': " + kv.get("a")); // 10
        System.out.println("Post-rollback 'b': " + kv.get("b")); // null

        // 3. Nested Transactions and Propagation
        kv.begin(); // Level 1
        kv.set("x", "foo");

        kv.begin(); // Level 2
        kv.set("x", "bar");
        kv.set("y", "baz");
        System.out.println("Level 2 'x': " + kv.get("x")); // bar

        kv.rollback(); // Rollback Level 2
        System.out.println("After Level 2 Rollback 'x': " + kv.get("x")); // foo
        System.out.println("After Level 2 Rollback 'y': " + kv.get("y")); // null

        kv.begin(); // Level 2 again
        kv.set("z", "100");
        kv.delete("x"); // Tombstone 'x'
        System.out.println("Level 2 deleted 'x': " + kv.get("x")); // null
        kv.commit(); // Merges into Level 1

        System.out.println("Level 1 after Level 2 commit 'x': " + kv.get("x")); // null
        System.out.println("Level 1 after Level 2 commit 'z': " + kv.get("z")); // 100

        kv.commit(); // Commits Level 1 to Global
        System.out.println("Global 'x': " + kv.get("x")); // null
        System.out.println("Global 'z': " + kv.get("z")); // 100
    }
}
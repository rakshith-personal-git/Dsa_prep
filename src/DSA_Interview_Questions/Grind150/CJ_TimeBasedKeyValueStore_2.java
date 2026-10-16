package DSA_Interview_Questions.Grind150;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LeetCode 981: Time Based Key-Value Store
 *
 * STRATEGY: HashMap + ArrayList + Binary Search (Floor Search)
 * - HashMap provides O(1) key lookup.
 * - ArrayList stores (timestamp, value) pairs sorted by timestamp.
 * - Binary Search finds the floor timestamp <= target in O(log N) time.
 */
public class CJ_TimeBasedKeyValueStore_2 {

    // Helper class to store timestamp-value pairs
    private static class Data {
        int timestamp;
        String value;

        Data(int timestamp, String value) {
            this.timestamp = timestamp;
            this.value = value;
        }
    }

    private final Map<String, List<Data>> map;

    /** Initialize data structure. */
    public CJ_TimeBasedKeyValueStore_2() {
        this.map = new HashMap<>();
    }

    /**
     * Stores the key and value with the given timestamp.
     *
     * TIME COMPLEXITY:  O(1) amortized - appending to ArrayList.
     * SPACE COMPLEXITY: O(1) auxiliary space per insert.
     */
    public void set(String key, String value, int timestamp) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(new Data(timestamp, value));
    }

    /**
     * Returns a value such that set was called previously with timestamp_prev <= timestamp.
     * If multiple exist, returns value with largest timestamp_prev. Returns "" if none.
     *
     * TIME COMPLEXITY:  O(log N) where N is the number of timestamps for the given key.
     * SPACE COMPLEXITY: O(1) auxiliary space.
     */
    public String get(String key, int timestamp) {
        if (!map.containsKey(key)) {
            return "";
        }

        List<Data> list = map.get(key);
        return binarySearchFloor(list, timestamp);
    }

    /**
     * Custom Binary Search to find the floor entry (largest timestamp <= target).
     */
    private String binarySearchFloor(List<Data> list, int targetTimestamp) {
        int low = 0;
        int high = list.size() - 1;
        String result = "";

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (list.get(mid).timestamp <= targetTimestamp) {
                // Potential answer found! Record value and search right for larger valid timestamp
                result = list.get(mid).value;
                low = mid + 1;
            } else {
                // Timestamp is too large, search left
                high = mid - 1;
            }
        }

        return result;
    }

    // Main method for testing in local IntelliJ terminal
    public static void main(String[] args) {
        CJ_TimeBasedKeyValueStore_2 timeMap = new CJ_TimeBasedKeyValueStore_2();

        timeMap.set("foo", "bar", 1);
        System.out.println(timeMap.get("foo", 1)); // Expected: "bar"
        System.out.println(timeMap.get("foo", 3)); // Expected: "bar" (floor is timestamp 1)

        timeMap.set("foo", "bar2", 4);
        System.out.println(timeMap.get("foo", 4)); // Expected: "bar2"
        System.out.println(timeMap.get("foo", 5)); // Expected: "bar2"
        System.out.println(timeMap.get("foo", 0)); // Expected: "" (no timestamp <= 0)
    }
}
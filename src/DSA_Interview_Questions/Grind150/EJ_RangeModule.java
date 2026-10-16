package DSA_Interview_Questions.Grind150;

import java.util.TreeMap;

/**
 * LeetCode 715: Range Module
 *
 * STRATEGY: TreeMap Disjoint Interval Maintenance
 * - TreeMap maps 'start' -> 'end' for non-overlapping, merged half-open intervals.
 * - queryRange runs in O(log N) by checking if a single floor interval covers [left, right).
 * - addRange and removeRange use floorKey to iteratively update/split overlapping intervals.
 */
public class EJ_RangeModule {

    // Maps interval start -> interval end
    private final TreeMap<Integer, Integer> map;

    public EJ_RangeModule() {
        this.map = new TreeMap<>();
    }

    /**
     * Adds the half-open interval [left, right), tracking every real number in it.
     *
     * TIME COMPLEXITY:  O(K log N) amortized O(log N), where K is number of merged intervals.
     * SPACE COMPLEXITY: O(1) auxiliary space.
     */
    public void addRange(int left, int right) {
        // Find candidate interval that starts <= right
        Integer start = map.floorKey(right);

        while (start != null) {
            int end = map.get(start);

            // If this interval ends before 'left', no further overlaps/touching possible
            if (end < left) {
                break;
            }

            // Overlaps or touches! Merge boundaries
            left = Math.min(left, start);
            right = Math.max(right, end);

            map.remove(start); // Remove old unmerged interval

            start = map.floorKey(right);
        }

        // Insert the fully merged interval
        map.put(left, right);
    }

    /**
     * Returns true if every real number in [left, right) is currently being tracked.
     *
     * TIME COMPLEXITY:  O(log N) binary search lookup.
     * SPACE COMPLEXITY: O(1) auxiliary space.
     */
    public boolean queryRange(int left, int right) {
        // Find the single interval that starts <= left
        Integer start = map.floorKey(left);

        // If it exists and its end >= right, then [left, right) is fully covered!
        return start != null && map.get(start) >= right;
    }

    /**
     * Stops tracking every real number in the half-open interval [left, right).
     *
     * TIME COMPLEXITY:  O(K log N) amortized O(log N), where K is number of affected intervals.
     * SPACE COMPLEXITY: O(1) auxiliary space.
     */
    public void removeRange(int left, int right) {
        // Largest start point of an interval that could overlap [left, right) is right - 1
        Integer start = map.floorKey(right - 1);

        while (start != null) {
            int end = map.get(start);

            // If interval ends <= left, no further overlaps possible
            if (end <= left) {
                break;
            }

            map.remove(start); // Remove original interval

            // Preserve left surviving piece if start < left
            if (start < left) {
                map.put(start, left);
            }

            // Preserve right surviving piece if end > right
            if (end > right) {
                map.put(right, end);
            }

            start = map.floorKey(right - 1);
        }
    }

    // Main method for testing in local IntelliJ terminal
    public static void main(String[] args) {
        EJ_RangeModule EJRangeModule = new EJ_RangeModule();

        EJRangeModule.addRange(10, 20);
        EJRangeModule.removeRange(14, 16);

        System.out.println(EJRangeModule.queryRange(10, 14)); // Expected: true ([10, 20) -> split into [10, 14) and [16, 20))
        System.out.println(EJRangeModule.queryRange(13, 15)); // Expected: false (14 to 15 is erased)
        System.out.println(EJRangeModule.queryRange(16, 17)); // Expected: true

        EJRangeModule.addRange(20, 30); // Adds [20, 30)
        System.out.println(EJRangeModule.queryRange(18, 22)); // Expected: false (gap exists between 17 and 20)
    }
}
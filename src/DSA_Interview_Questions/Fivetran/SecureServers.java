package DSA_Interview_Questions.Fivetran;

//https://leetcode.com/discuss/post/2838608/fivetran-hackerrank-question-makes-no-se-eq3v/
/**
 You are given:

 n: number of servers.

 server[i]: vulnerability of the i‑th server (can be negative too).

 k: you must perform exactly k operations.

 In one operation, choose any index i and do server[i]-- (decrease by 1).

 You must return the maximum possible value of the minimum element in the array after exactly k operations.
 If it is impossible to keep the minimum element non‑negative (i.e., best achievable min is < 0), return -1.
 **/
import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

public class SecureServers {

    // ============= APPROACH 1: Original Max Heap (Your Version) =============
    // Time: O(k log n), Space: O(n)
    // Strategy: Always reduce the maximum element to balance the array
    public static int findLargestMinVulnerability(int[] server, long k) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
        for (int i = 0; i < server.length; i++) {
            pq.offer(server[i]);
        }

        while (k > 0) {
            int vul = pq.poll();
            pq.offer(vul - 1);
            k--;
        }

        int minVul = Collections.min(pq);

        return minVul < 0 ? -1 : minVul;
    }

    // ============= APPROACH 2: Optimized Max Heap with Min Tracking =============
    // Time: O(k log n), Space: O(n)
    // Improvement: Track min during operations instead of scanning at the end
    public static int findLargestMinVulnerability_V2(int[] server, long k) {
        if (server == null || server.length == 0) return -1;
        
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
        int minVal = Integer.MAX_VALUE;
        
        for (int val : server) {
            maxHeap.offer(val);
            minVal = Math.min(minVal, val);
        }

        while (k > 0) {
            int maxVal = maxHeap.poll();
            maxVal--;
            maxHeap.offer(maxVal);
            minVal = Math.min(minVal, maxVal);
            k--;
        }

        // Final scan to ensure we have the actual minimum
        while (!maxHeap.isEmpty()) {
            minVal = Math.min(minVal, maxHeap.poll());
        }

        return minVal < 0 ? -1 : minVal;
    }

    // ============= APPROACH 3: Stream-based Solution =============
    // Time: O(k log n), Space: O(n)
    // Strategy: Use Java Streams for a more functional approach
    // Same logic as V1 but with cleaner syntax
    public static int findLargestMinVulnerability_V3(int[] server, long k) {
        if (server == null || server.length == 0) return -1;
        
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        
        // Add all elements to max heap
        for (int val : server) {
            maxHeap.offer(val);
        }
        
        // Perform k operations
        for (long i = 0; i < k; i++) {
            int max = maxHeap.poll();
            maxHeap.offer(max - 1);
        }
        
        // Find minimum using stream
        int minVal = maxHeap.stream()
                .min(Integer::compareTo)
                .orElse(-1);
        
        return minVal < 0 ? -1 : minVal;
    }

    // ============= APPROACH 4: Sorting + Greedy (Level Reduction) =============
    // Time: O(n log n + n), Space: O(n) for sorting
    // Strategy: Sort and reduce from the highest values, leveling them down
    public static int findLargestMinVulnerability_V4(int[] server, long k) {
        if (server == null || server.length == 0) return -1;
        
        int[] sorted = server.clone();
        Arrays.sort(sorted);
        
        int n = sorted.length;
        long operations = k;
        
        // Start from the highest and work backwards, leveling down
        for (int i = n - 1; i > 0 && operations > 0; i--) {
            // How many elements are at or above current level?
            long elementsAtThisLevel = n - i;
            
            // Gap between current level and next lower level
            long gap = sorted[i] - sorted[i - 1];
            
            // Operations needed to bring all elements at this level down to next level
            long opsNeeded = gap * elementsAtThisLevel;
            
            if (opsNeeded <= operations) {
                // We can level down completely
                operations -= opsNeeded;
                for (int j = i; j < n; j++) {
                    sorted[j] = sorted[i - 1];
                }
            } else {
                // Partially reduce - distribute remaining operations
                long reductionPerElement = operations / elementsAtThisLevel;
                long remainder = operations % elementsAtThisLevel;
                
                for (int j = i; j < n; j++) {
                    sorted[j] -= reductionPerElement;
                    if (j - i < remainder) {
                        sorted[j]--; // Distribute remainder
                    }
                }
                operations = 0;
                break;
            }
        }
        
        // If we still have operations left, all elements are at same level
        if (operations > 0) {
            long reductionPerElement = operations / n;
            long remainder = operations % n;
            
            for (int i = 0; i < n; i++) {
                sorted[i] -= reductionPerElement;
                if (i < remainder) {
                    sorted[i]--;
                }
            }
        }
        
        // Find minimum
        int minVal = sorted[0];
        return minVal < 0 ? -1 : minVal;
    }

    // ============= APPROACH 5: In-place with Min Heap (Most Efficient) =============
    // Time: O(n + k log n), Space: O(n)
    // Strategy: Use min heap to track minimum efficiently
    public static int findLargestMinVulnerability_V5(int[] server, long k) {
        if (server == null || server.length == 0) return -1;
        
        // Use max heap but track the minimum separately
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        
        for (int val : server) {
            maxHeap.offer(val);
        }

        while (k > 0) {
            int maxVal = maxHeap.poll();
            maxHeap.offer(maxVal - 1);
            k--;
        }

        // Find minimum in the heap
        int minVal = Integer.MAX_VALUE;
        for (int val : maxHeap) {
            minVal = Math.min(minVal, val);
        }

        return minVal < 0 ? -1 : minVal;
    }

    public static void main(String[] args) {
        int[] server = {3, 4, 5};
        long k = 4;

        System.out.println("Test 1: [3,4,5], k=4");
        System.out.println("V1 (Original):     " + findLargestMinVulnerability(server, k));
        System.out.println("V2 (Optimized):    " + findLargestMinVulnerability_V2(server, k));
        System.out.println("V3 (Sort Optimized):" + findLargestMinVulnerability_V3(server, k));
        System.out.println("V4 (Sorting):      " + findLargestMinVulnerability_V4(server, k));
        System.out.println("V5 (Min Tracking): " + findLargestMinVulnerability_V5(server, k));
        System.out.println();

        int[] server2 = {0, 0, 0};
        long k2 = 3;

        System.out.println("Test 2: [0,0,0], k=3");
        System.out.println("V1 (Original):     " + findLargestMinVulnerability(server2, k2));
        System.out.println("V2 (Optimized):    " + findLargestMinVulnerability_V2(server2, k2));
        System.out.println("V3 (Sort Optimized):" + findLargestMinVulnerability_V3(server2, k2));
        System.out.println("V4 (Sorting):      " + findLargestMinVulnerability_V4(server2, k2));
        System.out.println("V5 (Min Tracking): " + findLargestMinVulnerability_V5(server2, k2));
        System.out.println();

        int[] server3 = {2, 2, 3, 3};
        long k3 = 3;

        System.out.println("Test 3: [2,2,3,3], k=3");
        System.out.println("V1 (Original):     " + findLargestMinVulnerability(server3, k3));
        System.out.println("V2 (Optimized):    " + findLargestMinVulnerability_V2(server3, k3));
        System.out.println("V3 (Sort Optimized):" + findLargestMinVulnerability_V3(server3, k3));
        System.out.println("V4 (Sorting):      " + findLargestMinVulnerability_V4(server3, k3));
        System.out.println("V5 (Min Tracking): " + findLargestMinVulnerability_V5(server3, k3));
    }
}

package DSA_Interview_Questions.Fivetran;

/*
A system has n services numbered 1 to n, and m requests to be processed. The service where the ith request is cached is denoted by cache[i].

Processing a request from cache takes 1 unit of time; otherwise, it takes 2 units. Different services can process different requests simultaneously, but one service can only process one request at a time.

Find the minimum time to process all requests by optimally allocating each request to a service.

Example:
    n = 3
    m = 6
    cache = [1, 1, 3, 1, 3, 1]

An optimal allocation:
    Assign 1st, 2nd, and 4th requests to service 1: 3 time units
    Assign 3rd request to service 3: 1 time unit
    Assign 5th request to service 2: 2 time unit
    Assign 6th request to service 3: 2 time unit
All requests can be processed in 3 time units.

Function Description:
Complete the function getMinTime in the editor with the following parameters:
    int n: the number of services in the system
    int cache[m]: the service in which the request is cached

Returns:
    int: the minimum time required to process all requests

Constraints:
    1 ≤ n, m ≤ 2 * 10^5
    1 ≤ cache[i] ≤ n
*/

import java.util.*;

class GetMinTimeForCacheServer {

    /*
     * n: number of services
     * cache: size m, cache[i] = service where request i is cached
     */
    //TC: O(m) + O(log m × n) = O(m + n log m)
    //SC: O(n)
    public static int getMinTime(int n, List<Integer> cache) {
        int m = cache.size();

        // freq[s] = how many requests are cached in service s (1‑based)
        int[] freq = new int[n + 1];
        for (int c : cache) {
            freq[c]++;
        }

        // Max possible time is 2 * m (if all cost 2)
        int low = 0, high = 2 * m, ans = 2 * m;

        while (low <= high) {
            int mid = low + (high - low) / 2;  // candidate minimum time
            if (canProcessInTime(freq, n, m, mid)) {
                ans = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return ans;
    }

    // Check if all m requests can be processed within T time units
    private static boolean canProcessInTime(int[] freq, int n, int m, int T) {
        long totalCapacity = 0L;

        for (int s = 1; s <= n; s++) {
            int cachedReq = freq[s];

            if (cachedReq >= T) {
                // This server is fully occupied with cached requests only
                totalCapacity += T;
            } else {
                // Cached requests consume 1 unit each
                int cachedTime = cachedReq;
                // Remaining time for uncached requests on this server
                int remaining = T - cachedTime;
                // Each uncached request takes 2 units
                int uncachedCanDo = remaining / 2;
                totalCapacity += cachedReq + uncachedCanDo;
            }

            if (totalCapacity >= m) return true;  // early exit
        }

        return totalCapacity >= m;
    }

    public static void main(String[] args) {
        // Sample Test Case 1
        int n1 = 3;
        List<Integer> cache1 = Arrays.asList(1, 1, 3, 1, 3, 1);
        System.out.println(GetMinTimeForCacheServer.getMinTime(n1, cache1)); // Expected output: 3

        // Sample Test Case 2
        int n2 = 4;
        List<Integer> cache2 = Arrays.asList(1, 2, 3, 4);
        System.out.println(GetMinTimeForCacheServer.getMinTime(n2, cache2)); // Expected output: 1

        // Additional Test Case
        int n3 = 5;
        List<Integer> cache3 = Arrays.asList(2, 3, 3, 2, 5, 5, 1);
        System.out.println(GetMinTimeForCacheServer.getMinTime(n3, cache3)); // Expected output: 2

        // Large Test Case
        int n4 = 100000;
        List<Integer> cache4 = new ArrayList<>();
        for (int i = 1; i <= 100000; i++) {
            cache4.add(i % n4 + 1);
        }
        System.out.println(GetMinTimeForCacheServer.getMinTime(n4, cache4)); // Expected output: 1
    }

}

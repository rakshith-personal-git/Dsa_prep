package DSA_Interview_Questions.CompanyWisePrep.Ambient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sliding Window Log Rate Limiter.
 *
 * Enforces: At most `maxRequests` per `windowSizeMillis` for each unique client.
 *
 * Algorithm:
 * - Maintains a Double-Ended Queue (Deque) of epoch timestamps per client.
 * - On each request:
 *   1. Evict timestamps strictly older than (now - windowSizeMillis).
 *   2. If deque.size() < maxRequests, allow request and append timestamp.
 *   3. Else, reject request.
 */
public class SlidingWindowRateLimiter {

    private final int maxRequests;
    private final long windowSizeMillis;
    private final Map<String, Deque<Long>> clientLogMap;

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.clientLogMap = new ConcurrentHashMap<>();
    }

    /**
     * Checks if a request by clientId is allowed.
     * Time Complexity: O(E) where E is the number of expired entries pruned.
     */
    public synchronized boolean allowRequest(String clientId) {
        long now = System.currentTimeMillis();
        long windowBoundary = now - windowSizeMillis;

        clientLogMap.putIfAbsent(clientId, new ArrayDeque<>());
        Deque<Long> log = clientLogMap.get(clientId);

        // Step 1: Evict timestamps outside the active sliding window
        while (!log.isEmpty() && log.peekFirst() <= windowBoundary) {
            log.pollFirst();
        }

        // Step 2: Rate limit check
        if (log.size() < maxRequests) {
            log.addLast(now);
            return true;
        }

        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        // Allow up to 3 requests per 1000ms window
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(3, 1000);

        String client = "camera-node-04";

        System.out.println("Req 1: " + limiter.allowRequest(client)); // true
        System.out.println("Req 2: " + limiter.allowRequest(client)); // true
        System.out.println("Req 3: " + limiter.allowRequest(client)); // true
        System.out.println("Req 4 (Burst rejected): " + limiter.allowRequest(client)); // false

        // Wait for the window to partially clear
        Thread.sleep(1050);

        System.out.println("Req 5 (After window shifts): " + limiter.allowRequest(client)); // true
    }
}
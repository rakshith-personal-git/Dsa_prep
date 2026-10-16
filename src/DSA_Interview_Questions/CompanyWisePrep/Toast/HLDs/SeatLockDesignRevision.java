package DSA_Interview_Questions.CompanyWisePrep.Toast.HLDs;

/**
 * ==============================================================================================
 * SYSTEM DESIGN REVISION: SEAT / ROOM LOCK EXPIRATION STRATEGIES (SDE-3 / SSE PREP)
 * ==============================================================================================
 * 
 * PROBLEM: 
 * When a user selects a room/seat, we need to temporarily lock it for 10 minutes.
 * If the user completes payment, the lock becomes a permanent booking.
 * If the 10-minute TTL expires without payment, the lock must be released back to the pool.
 *
 * THIS FILE COVERS:
 * 1. Approach 1: Relational DB "Lazy Invalidation" (Optimistic / Conditional Locking)
 * 2. Approach 2: Redis Distributed Lock (In-Memory High-QPS Guard)
 * 3. Deep Dive: How Redis Expire Mechanics Work Under the Hood (Active vs. Passive Deletion)
 * 4. Safe Lock Release: Atomic Lua Scripting to avoid race conditions.
 * ==============================================================================================
 */
public class SeatLockDesignRevision {

    /* ==========================================================================================
     * APPROACH 1: DATABASE "LAZY INVALIDATION" (Relational DB / OLTP)
     * ==========================================================================================
     *
     * HOW IT WORKS:
     * - We store 'status' and 'expires_at' directly in the relational database table.
     * - No background timer scans the database to delete expired locks.
     * - Invalidation happens "LAZILY" when the next user attempts to query or lock the seat.
     *
     * ATOMIC SQL QUERY:
     * UPDATE room_inventory
     * SET status = 'LOCKED',
     *     locked_by_user_id = :userId,
     *     expires_at = NOW() + INTERVAL '10 minutes'
     * WHERE room_id = :roomId
     *   AND date = :bookingDate
     *   AND (
     *         status = 'AVAILABLE' 
     *         OR (status = 'LOCKED' AND expires_at < NOW()) -- Stale lock reclaimed automatically!
     *       );
     *
     * INTERVIEW TRADE-OFFS (When to propose this):
     * [+] PROS:
     *     1. Simple & Robust: Uses DB ACID guarantees. Row-level lock prevents double-booking.
     *     2. Zero External Infra: No Redis cluster required.
     *     3. Crash-Safe: If app workers crash, stale locks fix themselves on subsequent queries.
     * [-] CONS:
     *     1. High DB Contention: Under heavy traffic (e.g. flash sales), thousands of concurrent 
     *        requests fight for row locks on PostgreSQL/MySQL, burning CPU and connection pools.
     */
    public String getDbLazyInvalidationQuery() {
        return """
            UPDATE room_inventory
            SET status = 'LOCKED',
                locked_by_user_id = :userId,
                expires_at = CURRENT_TIMESTAMP + INTERVAL '10 MINUTES'
            WHERE room_id = :roomId
              AND date = :bookingDate
              AND (
                    status = 'AVAILABLE' 
                    OR (status = 'LOCKED' AND expires_at < CURRENT_TIMESTAMP)
                  );
            """;
    }


    /* ==========================================================================================
     * APPROACH 2: REDIS DISTRIBUTED LOCK (In-Memory High-Scale Layer)
     * ==========================================================================================
     *
     * HOW IT WORKS:
     * - At high QPS, we put Redis in front of the database as a fast rate-limiting guard.
     * - We acquire a lock using the atomic SET command with NX (Not Exists) and EX (Expire in Seconds).
     *
     * REDIS COMMAND:
     * SET lock:room:101:2026-10-05 "user_456_random_token" NX EX 600
     *
     * PARAMETERS:
     * - Key:   lock:room:<roomId>:<date>
     * - Value: Unique owner token (e.g. UUID/userID). VERY IMPORTANT to prevent unauthorized deletes.
     * - NX:    Only set the key if it does NOT already exist.
     * - EX:    Set expiration time (e.g. 600 seconds = 10 minutes).
     */

    // Conceptual Spring Data Redis implementation reference:
    /*
    public boolean acquireRedisLock(StringRedisTemplate redisTemplate, String roomId, String date, String userToken) {
        String lockKey = "lock:room:" + roomId + ":" + date;
        
        // Executes: SET lockKey userToken NX EX 600
        Boolean success = redisTemplate.opsForValue().setIfAbsent(
            lockKey, 
            userToken, 
            java.time.Duration.ofMinutes(10)
        );
        
        return Boolean.TRUE.equals(success);
    }
    */


    /* ==========================================================================================
     * DEEP DIVE: HOW REDIS REMOVES EXPIRED LOCKS UNDER THE HOOD
     * ==========================================================================================
     * Interviewers frequently ask: "How does Redis delete the key once the 10 mins pass?"
     *
     * Redis uses TWO mechanisms in parallel:
     *
     * 1. PASSIVE EXPIRATION (Lazy Deletion on Read):
     *    - Redis does NOT run a separate timer per key.
     *    - When any client attempts to read/query key `lock:room:101:2026-10-05`, Redis checks
     *      if current_time > key_expiration_time.
     *    - If expired, Redis instantly deletes the key on the spot and returns 'nil'.
     *
     * 2. ACTIVE EXPIRATION (Probabilistic Background Sampling):
     *    - What if no client queries that room again? To prevent memory leaks, Redis runs a 
     *      background task 10 times per second (10 Hz).
     *    - Step A: Randomly sample 20 keys with an associated TTL.
     *    - Step B: Delete all keys found to be expired.
     *    - Step C: If > 25% of the sampled keys were expired, repeat Step A immediately 
     *              to flush memory quickly without blocking client execution.
     */


    /* ==========================================================================================
     * SAFE LOCK RELEASE: ATOMIC LUA SCRIPTING
     * ==========================================================================================
     * DANGER SCENARIO (Why naive `DEL key` causes bugs):
     * 1. User A acquires lock with TTL = 10 mins.
     * 2. User A gets delayed (e.g. payment gateway lag or JVM GC pause) for 10 mins 1 sec.
     * 3. Redis automatically expires User A's lock.
     * 4. User B acquires lock for the same room.
     * 5. User A's thread wakes up, finishes payment, and calls `DEL lockKey`.
     * 6. CRITICAL BUG: User A just deleted User B's lock! User C can now book the room!
     *
     * SOLUTION:
     * Always release locks using an atomic Lua script that verifies ownership before deleting.
     */

    /**
     * Lua Script for Atomic Check-and-Delete:
     * 
     * KEYS[1] = lock key (e.g. lock:room:101:2026-10-05)
     * ARGV[1] = owner token (e.g. user_456_random_token)
     */
    public static final String UNLOCK_LUA_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "    return redis.call('del', KEYS[1]) " +
        "else " +
        "    return 0 " +
        "end";

    // Conceptual Spring Data Redis Lua release execution:
    /*
    public boolean releaseRedisLockSafely(StringRedisTemplate redisTemplate, String lockKey, String userToken) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(UNLOCK_LUA_SCRIPT);
        script.setResultType(Long.class);

        Long result = redisTemplate.execute(
            script, 
            Collections.singletonList(lockKey), 
            userToken
        );

        return Long.valueOf(1L).equals(result);
    }
    */


    /* ==========================================================================================
     * SUMMARY COMPARISON MATRIX (FOR INTERVIEW DEFENSE)
     * ==========================================================================================
     * 
     * +---------------------+-----------------------------------+-----------------------------------+
     * | Criterion           | DB Lazy Invalidation (`expires_at`)| Redis Distributed Lock            |
     * +---------------------+-----------------------------------+-----------------------------------+
     * | Ideal Scale         | Low to Moderate QPS               | High QPS / Flash Sales            |
     * | Source of Truth     | PostgreSQL / MySQL                | In-Memory Cache (Redis)           |
     * | Cleanup Trigger     | WHERE expires_at < NOW() on read  | Passive (Read) + Active (10Hz)    |
     * | Execution Latency   | Higher (Disk / DB Locks)          | Sub-millisecond (In-Memory)       |
     * +---------------------+-----------------------------------+-----------------------------------+
     *
     * RECOMMENDED HYBRID ANSWER IN INTERVIEWS:
     * "For standard booking traffic, lazy invalidation directly in PostgreSQL via conditional 
     *  atomic UPDATE statements ensures total ACID consistency with minimal architecture complexity. 
     *  However, for high-concurrency systems, we put Redis in front using `SET key token NX EX 600` 
     *  to absorb peak read/write traffic, combined with an atomic Lua script for safe lock release."
     */

    public static void main(String[] args) {
        System.out.println("=== Seat Lock Expiration Revision File ===");
        System.out.println("Use this class as a quick reference in IntelliJ for SSE/SDE-3 System Design rounds.");
    }
}
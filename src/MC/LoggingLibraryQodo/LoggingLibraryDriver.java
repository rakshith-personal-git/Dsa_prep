package MC.LoggingLibraryQodo;

import MC.LoggingLibraryQodo.config.LoggerConfig;
import MC.LoggingLibraryQodo.config.LoggerConfig.LoggerType;
import MC.LoggingLibraryQodo.factory.LoggerFactory;
import MC.LoggingLibraryQodo.logger.Logger;
import MC.LoggingLibraryQodo.model.LogLevel;
import MC.LoggingLibraryQodo.sink.StdOutSink;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Driver class demonstrating all features of the Logging Library.
 *
 * Test Cases:
 * ===========
 * 1. demoSyncLogging()         — Basic synchronous logging with level filtering
 * 2. demoAsyncLogging()        — Asynchronous logging with buffered queue
 * 3. demoConcurrentLogging()   — Multi-threaded concurrent logging (thread safety)
 * 4. demoSinkLevelFiltering()  — Sink-level filtering independent of logger level
 * 5. demoMultipleSinks()       — Logger with multiple sinks at different levels
 * 6. demoEdgeCases()           — Null messages, empty messages, logging after close
 *
 * Each test case is self-contained with its own logger configuration.
 */
public class LoggingLibraryDriver {

    public static void main(String[] args) throws InterruptedException {
        demoSyncLogging();
        System.out.println();

        demoAsyncLogging();
        System.out.println();

        demoConcurrentLogging();
        System.out.println();

        demoSinkLevelFiltering();
        System.out.println();

        demoMultipleSinks();
        System.out.println();

        demoEdgeCases();
    }

    // ==================== Test Case 1: Sync Logging ====================

    /**
     * Demonstrates basic synchronous logging.
     *
     * Config: SYNC logger, INFO level, STDOUT sink at INFO level.
     *
     * Expected Output:
     * - INFO message  → printed ✓ (INFO >= INFO)
     * - WARN message  → printed ✓ (WARN >= INFO)
     * - DEBUG message → filtered ✗ (DEBUG < INFO)
     * - ERROR message → printed ✓ (ERROR >= INFO)
     * - FATAL message → printed ✓ (FATAL >= INFO)
     */
    private static void demoSyncLogging() {
        System.out.println("========== TEST 1: Synchronous Logging ==========");

        LoggerConfig config = new LoggerConfig.Builder("sync-logger")
                .logLevel(LogLevel.INFO)
                .loggerType(LoggerType.SYNC)
                .timestampFormat("dd-MM-yyyy-HH-mm-ss")
                .addSink(new StdOutSink(LogLevel.INFO))
                .build();

        Logger logger = LoggerFactory.createLogger(config);

        logger.info("Info message");
        logger.warn("Warn message");
        logger.debug("Debug message");   // Should be filtered (DEBUG < INFO)
        logger.error("Error message");
        logger.fatal("Fatal message");

        logger.close();
        System.out.println("========== END TEST 1 ==========");
    }

    // ==================== Test Case 2: Async Logging ====================

    /**
     * Demonstrates asynchronous logging with a bounded buffer.
     *
     * Config: ASYNC logger, INFO level, buffer size 25, STDOUT sink.
     *
     * Key observations:
     * - Messages are enqueued and processed by a background thread
     * - close() ensures all buffered messages are flushed before returning
     * - Message ordering is preserved (messages appear in the order they were logged)
     */
    private static void demoAsyncLogging() throws InterruptedException {
        System.out.println("========== TEST 2: Asynchronous Logging ==========");

        LoggerConfig config = new LoggerConfig.Builder("async-logger")
                .logLevel(LogLevel.INFO)
                .loggerType(LoggerType.ASYNC)
                .bufferSize(25)
                .timestampFormat("dd-MM-yyyy-HH-mm-ss")
                .addSink(new StdOutSink(LogLevel.INFO))
                .build();

        Logger logger = LoggerFactory.createLogger(config);

        logger.info("Async Info message 1");
        logger.warn("Async Warn message 2");
        logger.debug("Async Debug message (should be filtered)");
        logger.error("Async Error message 3");
        logger.fatal("Async Fatal message 4");
        logger.info("Async Info message 5");

        // close() flushes all remaining messages and waits for consumer thread to finish
        logger.close();

        // Small delay to ensure all output is flushed to console
        Thread.sleep(100);
        System.out.println("========== END TEST 2 ==========");
    }

    // ==================== Test Case 3: Concurrent Logging ====================

    /**
     * Demonstrates thread-safe concurrent logging.
     *
     * Spawns 5 threads, each logging 10 messages.
     * Total expected messages: 50 (all at INFO level, all should be printed).
     *
     * Key observations:
     * - No data loss: all 50 messages should appear in the output
     * - No garbled output: each log line is complete and well-formed
     * - Ordering: messages from the same thread appear in order relative to each other
     *   (global ordering across threads depends on scheduling)
     *
     * Uses CountDownLatch to ensure all threads start simultaneously,
     * maximizing the chance of true concurrency and exposing any thread-safety issues.
     */
    private static void demoConcurrentLogging() throws InterruptedException {
        System.out.println("========== TEST 3: Concurrent Logging (Async) ==========");

        LoggerConfig config = new LoggerConfig.Builder("concurrent-logger")
                .logLevel(LogLevel.DEBUG)
                .loggerType(LoggerType.ASYNC)
                .bufferSize(100)
                .timestampFormat("dd-MM-yyyy-HH-mm-ss")
                .addSink(new StdOutSink(LogLevel.DEBUG))
                .build();

        Logger logger = LoggerFactory.createLogger(config);

        int threadCount = 5;
        int messagesPerThread = 10;

        // CountDownLatch ensures all threads start logging at the same time
        CountDownLatch startLatch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for the signal to start
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                for (int j = 0; j < messagesPerThread; j++) {
                    logger.info("Thread-" + threadId + " | Message-" + j);
                }
            });
        }

        // Release all threads simultaneously
        startLatch.countDown();

        // Wait for all threads to finish submitting
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Close logger to flush remaining messages
        logger.close();

        Thread.sleep(200);
        System.out.println("Expected " + (threadCount * messagesPerThread) + " messages above.");
        System.out.println("========== END TEST 3 ==========");
    }

    // ==================== Test Case 4: Sink-Level Filtering ====================

    /**
     * Demonstrates that sinks can have their own independent log level filtering.
     *
     * Config: Logger level = DEBUG (accepts everything), but Sink level = ERROR.
     * The logger passes all messages through, but the sink only prints ERROR and FATAL.
     *
     * This shows the TWO-GATE filtering:
     * Gate 1 (Logger level): message.level >= logger.level → pass to sinks
     * Gate 2 (Sink level):   message.level >= sink.level   → actually write
     *
     * Expected Output:
     * - DEBUG → passes Gate 1 (DEBUG >= DEBUG) → blocked at Gate 2 (DEBUG < ERROR) ✗
     * - INFO  → passes Gate 1 (INFO >= DEBUG)  → blocked at Gate 2 (INFO < ERROR)  ✗
     * - WARN  → passes Gate 1 (WARN >= DEBUG)  → blocked at Gate 2 (WARN < ERROR)  ✗
     * - ERROR → passes Gate 1 (ERROR >= DEBUG) → passes Gate 2 (ERROR >= ERROR)    ✓
     * - FATAL → passes Gate 1 (FATAL >= DEBUG) → passes Gate 2 (FATAL >= ERROR)    ✓
     */
    private static void demoSinkLevelFiltering() {
        System.out.println("========== TEST 4: Sink-Level Filtering ==========");
        System.out.println("Logger level: DEBUG, Sink level: ERROR");
        System.out.println("Only ERROR and FATAL should appear:");

        LoggerConfig config = new LoggerConfig.Builder("sink-filter-logger")
                .logLevel(LogLevel.DEBUG)       // Logger accepts everything
                .loggerType(LoggerType.SYNC)
                .timestampFormat("dd-MM-yyyy-HH-mm-ss")
                .addSink(new StdOutSink(LogLevel.ERROR))  // Sink only accepts ERROR+
                .build();

        Logger logger = LoggerFactory.createLogger(config);

        logger.debug("This DEBUG message should NOT appear");
        logger.info("This INFO message should NOT appear");
        logger.warn("This WARN message should NOT appear");
        logger.error("This ERROR message SHOULD appear");
        logger.fatal("This FATAL message SHOULD appear");

        logger.close();
        System.out.println("========== END TEST 4 ==========");
    }

    // ==================== Test Case 5: Multiple Sinks ====================

    /**
     * Demonstrates a logger with MULTIPLE sinks at different log levels.
     *
     * Config:
     * - Logger level: DEBUG (passes everything through)
     * - Sink 1 (Console - DEBUG): prints ALL messages
     * - Sink 2 (Console - ERROR): prints only ERROR and FATAL
     *
     * This simulates a real-world scenario where you might have:
     * - A verbose console output for development
     * - A file sink that only captures errors for production monitoring
     *
     * Expected Output:
     * Each message that passes BOTH gates will be printed.
     * ERROR and FATAL messages will appear TWICE (once from each sink).
     *
     * DEBUG message:
     *   Sink1 (DEBUG): ✓ printed
     *   Sink2 (ERROR): ✗ filtered
     *
     * INFO message:
     *   Sink1 (DEBUG): ✓ printed
     *   Sink2 (ERROR): ✗ filtered
     *
     * ERROR message:
     *   Sink1 (DEBUG): ✓ printed
     *   Sink2 (ERROR): ✓ printed  → appears TWICE
     *
     * FATAL message:
     *   Sink1 (DEBUG): ✓ printed
     *   Sink2 (ERROR): ✓ printed  → appears TWICE
     */
    private static void demoMultipleSinks() {
        System.out.println("========== TEST 5: Multiple Sinks ==========");
        System.out.println("Sink1: StdOut at DEBUG level (prints everything)");
        System.out.println("Sink2: StdOut at ERROR level (prints ERROR+ only)");
        System.out.println("ERROR and FATAL messages should appear TWICE:");

        LoggerConfig config = new LoggerConfig.Builder("multi-sink-logger")
                .logLevel(LogLevel.DEBUG)
                .loggerType(LoggerType.SYNC)
                .timestampFormat("dd-MM-yyyy-HH-mm-ss")
                .addSink(new StdOutSink(LogLevel.DEBUG))   // Sink 1: verbose
                .addSink(new StdOutSink(LogLevel.ERROR))   // Sink 2: errors only
                .build();

        Logger logger = LoggerFactory.createLogger(config);

        logger.debug("DEBUG - should appear once (Sink1 only)");
        logger.info("INFO - should appear once (Sink1 only)");
        logger.warn("WARN - should appear once (Sink1 only)");
        logger.error("ERROR - should appear TWICE (Sink1 + Sink2)");
        logger.fatal("FATAL - should appear TWICE (Sink1 + Sink2)");

        logger.close();
        System.out.println("========== END TEST 5 ==========");
    }

    // ==================== Test Case 6: Edge Cases ====================

    /**
     * Demonstrates graceful handling of edge cases:
     *
     * 1. Null message       → silently skipped (no crash)
     * 2. Empty message      → silently skipped (no crash)
     * 3. Blank message      → silently skipped (no crash)
     * 4. Very long message  → handled normally
     * 5. Log after close()  → throws IllegalStateException (caught here)
     *
     * These edge cases demonstrate defensive programming:
     * - The library should NEVER crash the host application
     * - Invalid inputs are handled gracefully
     * - Post-close usage is explicitly rejected with a clear error
     */
    private static void demoEdgeCases() {
        System.out.println("========== TEST 6: Edge Cases ==========");

        LoggerConfig config = new LoggerConfig.Builder("edge-case-logger")
                .logLevel(LogLevel.DEBUG)
                .loggerType(LoggerType.SYNC)
                .timestampFormat("dd-MM-yyyy-HH-mm-ss")
                .addSink(new StdOutSink(LogLevel.DEBUG))
                .build();

        Logger logger = LoggerFactory.createLogger(config);

        // Edge Case 1: Null message
        System.out.println("\n--- Edge Case 1: Null message (should be silently skipped) ---");
        logger.info(null);
        System.out.println("(No output above = correct behavior)");

        // Edge Case 2: Empty message
        System.out.println("\n--- Edge Case 2: Empty message (should be silently skipped) ---");
        logger.info("");
        System.out.println("(No output above = correct behavior)");

        // Edge Case 3: Blank message (whitespace only)
        System.out.println("\n--- Edge Case 3: Blank message (should be silently skipped) ---");
        logger.info("   ");
        System.out.println("(No output above = correct behavior)");

        // Edge Case 4: Very long message
        System.out.println("\n--- Edge Case 4: Very long message (should be handled normally) ---");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) sb.append("A");
        String longMessage = sb.toString();
        logger.info(longMessage);

        // Edge Case 5: Normal message to confirm logger still works
        System.out.println("\n--- Edge Case 5: Normal message after edge cases ---");
        logger.info("Logger still works after edge cases!");

        // Edge Case 6: Logging after close
        System.out.println("\n--- Edge Case 6: Logging after close() (should throw IllegalStateException) ---");
        logger.close();
        try {
            logger.info("This should throw an exception");
            System.out.println("ERROR: No exception was thrown!");
        } catch (IllegalStateException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        // Edge Case 7: Invalid config — no sinks
        System.out.println("\n--- Edge Case 7: Invalid config - no sinks (should throw IllegalArgumentException) ---");
        try {
            LoggerConfig badConfig = new LoggerConfig.Builder("bad-logger")
                    .logLevel(LogLevel.INFO)
                    .build();
            System.out.println("ERROR: No exception was thrown!");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        // Edge Case 8: Invalid config — null sink
        System.out.println("\n--- Edge Case 8: Invalid config - null sink (should throw IllegalArgumentException) ---");
        try {
            LoggerConfig badConfig = new LoggerConfig.Builder("bad-logger")
                    .addSink(null)
                    .build();
            System.out.println("ERROR: No exception was thrown!");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        // Edge Case 9: Invalid config — blank logger name
        System.out.println("\n--- Edge Case 9: Invalid config - blank name (should throw IllegalArgumentException) ---");
        try {
            LoggerConfig badConfig = new LoggerConfig.Builder("")
                    .addSink(new StdOutSink(LogLevel.INFO))
                    .build();
            System.out.println("ERROR: No exception was thrown!");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        // Edge Case 10: ASYNC logger with invalid buffer size
        System.out.println("\n--- Edge Case 10: ASYNC logger with buffer size 0 (should throw IllegalArgumentException) ---");
        try {
            LoggerConfig badConfig = new LoggerConfig.Builder("bad-async")
                    .loggerType(LoggerType.ASYNC)
                    .bufferSize(0)
                    .addSink(new StdOutSink(LogLevel.INFO))
                    .build();
            System.out.println("ERROR: No exception was thrown!");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        System.out.println("\n========== END TEST 6 ==========");
    }
}

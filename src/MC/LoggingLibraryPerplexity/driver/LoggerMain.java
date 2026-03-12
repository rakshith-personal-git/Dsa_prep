package MC.LoggingLibraryPerplexity.driver;

import MC.LoggingLibraryPerplexity.util.LoggingLibrary;
import MC.LoggingLibraryPerplexity.core.LogLevel;
import MC.LoggingLibraryPerplexity.core.LoggerConfig;
import MC.LoggingLibraryPerplexity.core.LoggerType;
import MC.LoggingLibraryPerplexity.logger.Logger;
import MC.LoggingLibraryPerplexity.sink.FileSink;
import MC.LoggingLibraryPerplexity.sink.StdoutSink;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Driver / demo program for the logging library.
 *
 * <p>Demonstrates all 6 scenarios required by the specification:
 * <ol>
 *   <li>Sync logging with level filtering</li>
 *   <li>Async logging with level filtering</li>
 *   <li>Concurrent multi-thread logging (no data loss)</li>
 *   <li>Sink-level filtering independent of logger level</li>
 *   <li>Multiple sinks with different levels</li>
 *   <li>Edge cases: null and blank messages</li>
 * </ol>
 */
public class LoggerMain {

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("==============================================");
        System.out.println("          LOGGING LIBRARY DEMO               ");
        System.out.println("==============================================\n");

        demoSyncLogging();
        demoAsyncLogging();
        demoConcurrentLogging();
        demoSinkLevelFiltering();
        demoMultipleSinks();
        demoEdgeCases();

        // Drain async queues and release resources
        LoggingLibrary.shutdownAll();
        System.out.println("[Main] All loggers shut down gracefully.");
    }

    // ── Demo 1: Sync Logging ──────────────────────────────────────────────────

    /**
     * Demonstrates synchronous logging at INFO level.
     * Expected: INFO, WARN, ERROR, FATAL are printed; DEBUG is silently dropped.
     */
    static void demoSyncLogging() {
        System.out.println("--- Demo 1: Sync Logging (logger=INFO, sink=INFO) ---");

        Logger logger = LoggingLibrary.initialize(new LoggerConfig.Builder()
                .loggerName("sync-logger")
                .logLevel(LogLevel.INFO)
                .loggerType(LoggerType.SYNC)
                .timestampFormat("dd-MM-yyyy-HH-mm-ss")
                .addSink(new StdoutSink(LogLevel.INFO))
                .build());

        logger.info("Info message");
        logger.warn("Warn message");
        logger.debug("Debug message");  // filtered — should NOT appear
        logger.error("Error message");
        logger.fatal("Fatal message");
        System.out.println();
    }

    // ── Demo 2: Async Logging ─────────────────────────────────────────────────

    /**
     * Demonstrates asynchronous logging with a bounded buffer of 25.
     * Expected: only WARN, ERROR, FATAL appear; DEBUG and INFO are dropped at logger level.
     */
    static void demoAsyncLogging() throws InterruptedException {
        System.out.println("--- Demo 2: Async Logging (logger=WARN, buffer=25) ---");

        Logger logger = LoggingLibrary.initialize(new LoggerConfig.Builder()
                .loggerName("async-logger")
                .logLevel(LogLevel.WARN)
                .loggerType(LoggerType.ASYNC)
                .bufferSize(25)
                .timestampFormat("dd-MM-yyyy-HH-mm-ss")
                .addSink(new StdoutSink(LogLevel.WARN))
                .build());

        logger.debug("Debug — should NOT appear");
        logger.info("Info — should NOT appear");
        logger.warn("Warn message async");
        logger.error("Error message async");
        logger.fatal("Fatal message async");

        logger.shutdown(); // drain queue before continuing
        System.out.println();
    }

    // ── Demo 3: Concurrent Logging ────────────────────────────────────────────

    /**
     * Fires 10 threads each sending 5 messages = 50 total.
     * All 50 must be delivered in order within each thread (FIFO queue guarantees this).
     */
    static void demoConcurrentLogging() throws InterruptedException {
        System.out.println("--- Demo 3: Concurrent Logging (10 threads x 5 messages) ---");

        Logger logger = LoggingLibrary.initialize(new LoggerConfig.Builder()
                .loggerName("concurrent-logger")
                .logLevel(LogLevel.DEBUG)
                .loggerType(LoggerType.ASYNC)
                .bufferSize(200)
                .timestampFormat("dd-MM-yyyy-HH-mm-ss")
                .addSink(new StdoutSink(LogLevel.DEBUG))
                .build());

        int threads = 10, msgsPerThread = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int t = 0; t < threads; t++) {
            final int tid = t;
            executor.submit(() -> {
                for (int m = 0; m < msgsPerThread; m++) {
                    logger.info("Thread-" + tid + " | Msg-" + m);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        logger.shutdown();
        System.out.println("[Demo3] " + (threads * msgsPerThread) + " messages processed.\n");
    }

    // ── Demo 4: Sink-Level Filtering ──────────────────────────────────────────

    /**
     * Logger is at DEBUG (accepts everything), but the sink only accepts ERROR+.
     * Expected: only ERROR and FATAL printed.
     */
    static void demoSinkLevelFiltering() {
        System.out.println("--- Demo 4: Sink-Level Filtering (logger=DEBUG, sink=ERROR) ---");

        Logger logger = LoggingLibrary.initialize(new LoggerConfig.Builder()
                .loggerName("sink-filter-logger")
                .logLevel(LogLevel.DEBUG)
                .loggerType(LoggerType.SYNC)
                .timestampFormat("dd-MM-yyyy-HH-mm-ss")
                .addSink(new StdoutSink(LogLevel.ERROR)) // only ERROR and above
                .build());

        logger.debug("Debug — filtered by sink");
        logger.info("Info — filtered by sink");
        logger.warn("Warn — filtered by sink");
        logger.error("Error — passes sink filter");
        logger.fatal("Fatal — passes sink filter");
        System.out.println();
    }

    // ── Demo 5: Multiple Sinks ────────────────────────────────────────────────

    /**
     * Two sinks attached: STDOUT accepts INFO+, FileSink accepts WARN+.
     * Expected: INFO goes to STDOUT only; WARN/ERROR go to both.
     */
    static void demoMultipleSinks() throws IOException {
        System.out.println("--- Demo 5: Multiple Sinks (STDOUT=INFO+, File=WARN+) ---");

        FileSink fileSink = new FileSink(LogLevel.WARN, "app.log");

        Logger logger = LoggingLibrary.initialize(new LoggerConfig.Builder()
                .loggerName("multi-sink-logger")
                .logLevel(LogLevel.DEBUG)
                .loggerType(LoggerType.SYNC)
                .timestampFormat("dd-MM-yyyy-HH-mm-ss")
                .addSink(new StdoutSink(LogLevel.INFO))
                .addSink(fileSink)
                .build());

        logger.debug("Debug — below all sinks");
        logger.info("Info — STDOUT only");
        logger.warn("Warn — STDOUT + File");
        logger.error("Error — STDOUT + File");

        fileSink.close(); // flush and release file handle
        System.out.println("[Demo5] Check app.log for WARN+ entries.\n");
    }

    // ── Demo 6: Edge Cases ────────────────────────────────────────────────────

    /**
     * Null and blank messages must be silently ignored (no exception, no output).
     * A valid message sent after confirms the logger still works normally.
     */
    static void demoEdgeCases() {
        System.out.println("--- Demo 6: Edge Cases (null / blank messages) ---");

        Logger logger = LoggingLibrary.initialize(new LoggerConfig.Builder()
                .loggerName("edge-case-logger")
                .logLevel(LogLevel.INFO)
                .loggerType(LoggerType.SYNC)
                .timestampFormat("dd-MM-yyyy-HH-mm-ss")
                .addSink(new StdoutSink(LogLevel.INFO))
                .build());

        logger.info(null);     // must NOT throw NullPointerException
        logger.info("   ");    // must NOT produce any output
        logger.info("Valid message after edge cases");
        System.out.println();
    }
}

package MC.LoggingLibraryQodo.logger;

import MC.LoggingLibraryQodo.config.LoggerConfig;
import MC.LoggingLibraryQodo.model.LogLevel;
import MC.LoggingLibraryQodo.model.LogMessage;
import MC.LoggingLibraryQodo.sink.Sink;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Abstract base class for all Logger implementations.
 *
 * Design Pattern: Template Method Pattern
 * ========================================
 * This class defines the SKELETON of the logging algorithm:
 *   1. Check if the logger is still open (not closed)
 *   2. Validate the message (null/empty check)
 *   3. Check if the message level meets the logger's minimum threshold
 *   4. Enrich the message with a formatted timestamp
 *   5. Delegate to subclass-specific publish() method  ← THIS IS THE TEMPLATE METHOD HOOK
 *
 * Steps 1-4 are COMMON to all logger types (sync, async, etc.)
 * Step 5 varies: SyncLogger writes immediately, AsyncLogger enqueues.
 *
 * Why Template Method?
 * - Avoids code duplication: timestamp formatting, level checking, null validation
 *   are written ONCE here, not repeated in every Logger subclass.
 * - Subclasses only override the part that differs (publish mechanism).
 * - Adding a new logger type (e.g., BatchLogger) only requires implementing publish().
 *
 * The convenience methods (info, debug, warn, error, fatal) are implemented here
 * as simple delegations to log(level, message), following DRY principle.
 */
public abstract class AbstractLogger implements Logger {

    protected final LoggerConfig config;
    protected final List<Sink> sinks;
    private final DateTimeFormatter formatter;

    /**
     * Flag to track if the logger has been closed.
     * volatile ensures visibility across threads — if one thread calls close(),
     * other threads will immediately see isClosed = true.
     */
    protected volatile boolean isClosed = false;

    /**
     * Initializes the logger with the given configuration.
     *
     * @param config the logger configuration (name, level, sinks, etc.)
     */
    protected AbstractLogger(LoggerConfig config) {
        this.config = config;
        this.sinks = config.getSinks();
        this.formatter = DateTimeFormatter.ofPattern(config.getTimestampFormat());
    }

    // ==================== Convenience Methods (DRY) ====================

    @Override
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    @Override
    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    @Override
    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    @Override
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    @Override
    public void fatal(String message) {
        log(LogLevel.FATAL, message);
    }

    // ==================== Core Logging Algorithm (Template Method) ====================

    /**
     * Core logging method implementing the Template Method pattern.
     *
     * Algorithm:
     * 1. Guard: reject if logger is closed
     * 2. Guard: skip null/empty messages gracefully
     * 3. Guard: skip if message level is below logger's configured minimum
     * 4. Enrich: create immutable LogMessage with current timestamp
     * 5. Dispatch: delegate to subclass via publish() — the "hook" method
     *
     * @param level   severity level of the message
     * @param message the log message content
     */
    @Override
    public void log(LogLevel level, String message) {
        // Step 1: Check if logger is still open
        if (isClosed) {
            throw new IllegalStateException("Logger '" + config.getName() + "' has been closed. Cannot log messages.");
        }

        // Step 2: Gracefully handle null/empty messages
        if (message == null || message.trim().isEmpty()) {
            return; // Silently skip — don't crash the application for a bad log call
        }

        // Step 3: Logger-level filtering (first gate)
        // If the message's priority is below the logger's configured minimum, discard it.
        // This is an optimization: we avoid creating LogMessage objects for filtered messages.
        if (level.getPriority() < config.getLogLevel().getPriority()) {
            return;
        }

        // Step 4: Enrich with timestamp
        // Timestamp is captured NOW (at log call time), not when the message is eventually written.
        // This is important for async loggers where there may be a delay between log() and write().
        String timestamp = LocalDateTime.now().format(formatter);
        LogMessage logMessage = new LogMessage(message, level, timestamp);

        // Step 5: Delegate to subclass-specific dispatch mechanism (Template Method hook)
        publish(logMessage);
    }

    // ==================== Abstract Hook Method ====================

    /**
     * Template Method hook — subclasses implement their specific dispatch mechanism.
     *
     * - SyncLogger:  writes to all sinks immediately (synchronized)
     * - AsyncLogger: enqueues the message into a bounded blocking queue
     *
     * @param message the enriched, immutable log message
     */
    protected abstract void publish(LogMessage message);

    /**
     * Dispatches a message to all configured sinks.
     * Each sink performs its own level filtering internally.
     *
     * This is a utility method used by both SyncLogger (directly) and
     * AsyncLogger (from the consumer thread).
     *
     * @param message the log message to dispatch
     */
    protected void writeToSinks(LogMessage message) {
        for (Sink sink : sinks) {
            try {
                sink.write(message);
            } catch (Exception e) {
                // Fail gracefully: don't let one sink's failure crash the logger
                // In production, you'd log this to a fallback/error sink
                System.err.println("Error writing to sink " + sink + ": " + e.getMessage());
            }
        }
    }
}

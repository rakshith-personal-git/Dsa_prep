package MC.LoggingLibraryQodo.logger;

import MC.LoggingLibraryQodo.model.LogLevel;

/**
 * Public API interface for the Logging Library.
 *
 * This is the ONLY interface that client code interacts with.
 * Clients should program to this interface, not to concrete implementations
 * (SyncLogger, AsyncLogger). This follows the Dependency Inversion Principle.
 *
 * Usage:
 *   Logger logger = LoggerFactory.createLogger(config);
 *   logger.info("Application started");
 *   logger.error("Something went wrong");
 *   logger.close();  // Important for async loggers!
 *
 * Convenience methods (info, debug, warn, error, fatal) delegate to log(level, message).
 *
 * close() MUST be called when the logger is no longer needed:
 * - For SyncLogger: no-op (nothing to flush)
 * - For AsyncLogger: signals the consumer thread to drain remaining messages and shut down
 *   Failing to call close() on an async logger may result in lost messages!
 */
public interface Logger {

    /**
     * Log a message at DEBUG level.
     * Use for detailed diagnostic information useful during development.
     */
    void debug(String message);

    /**
     * Log a message at INFO level.
     * Use for general informational messages about application flow.
     */
    void info(String message);

    /**
     * Log a message at WARN level.
     * Use for potentially harmful situations that don't prevent operation.
     */
    void warn(String message);

    /**
     * Log a message at ERROR level.
     * Use for error events that might still allow the application to continue.
     */
    void error(String message);

    /**
     * Log a message at FATAL level.
     * Use for severe errors that will likely cause the application to abort.
     */
    void fatal(String message);

    /**
     * Log a message at the specified level.
     * This is the core method — all convenience methods delegate here.
     *
     * @param level   the severity level of the message
     * @param message the log message content
     */
    void log(LogLevel level, String message);

    /**
     * Gracefully shuts down the logger.
     *
     * For AsyncLogger: flushes all buffered messages and stops the consumer thread.
     * For SyncLogger: no-op.
     *
     * After close() is called, subsequent log calls will throw IllegalStateException.
     */
    void close();
}

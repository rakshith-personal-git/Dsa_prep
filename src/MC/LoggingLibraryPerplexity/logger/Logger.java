package MC.LoggingLibraryPerplexity.logger;

import MC.LoggingLibraryPerplexity.core.LogLevel;

/**
 * Public contract for all logger implementations.
 *
 * <p>Consumers of the library should depend only on this interface,
 * allowing the underlying implementation (Sync/Async) to be swapped
 * without changing calling code.
 */
public interface Logger {

    /** Generic log method — all convenience methods delegate here. */
    void log(String message, LogLevel level);

    void debug(String message);
    void info(String message);
    void warn(String message);
    void error(String message);
    void fatal(String message);

    /**
     * Gracefully shuts down the logger.
     * For async loggers this drains the queue before returning.
     * Safe to call on sync loggers (no-op).
     */
    void shutdown();

    /** Returns the unique name this logger was registered under. */
    String getName();
}

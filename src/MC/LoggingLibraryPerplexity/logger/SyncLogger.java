package MC.LoggingLibraryPerplexity.logger;

import MC.LoggingLibraryPerplexity.core.LogLevel;
import MC.LoggingLibraryPerplexity.core.LogMessage;
import MC.LoggingLibraryPerplexity.core.LoggerConfig;

/**
 * A thread-safe synchronous logger.
 *
 * <p>The calling thread writes directly to all sinks before returning.
 * The {@code synchronized} keyword on {@link #log} ensures:
 * <ul>
 *   <li>Messages from multiple threads are never interleaved</li>
 *   <li>FIFO ordering is preserved within the logger</li>
 * </ul>
 *
 * <p>Trade-off: caller blocks until all sinks have written. Suitable for
 * low-to-medium throughput or where latency predictability matters more
 * than throughput. Use {@link AsyncLogger} for high-throughput scenarios.
 */
public class SyncLogger extends AbstractLogger {

    public SyncLogger(LoggerConfig config) {
        super(config);
    }

    /**
     * Synchronously writes the message to all eligible sinks.
     *
     * <p>Guards:
     * <ul>
     *   <li>null or blank messages are silently dropped</li>
     *   <li>messages below the configured logger level are discarded</li>
     * </ul>
     */
    @Override
    public synchronized void log(String message, LogLevel level) {
        if (message == null || message.isBlank()) return; // edge case guard
        if (!isLevelEnabled(level)) return;               // logger-level gate
        dispatchToSinks(new LogMessage(message, level));
    }

    /** No background threads — nothing to clean up. */
    @Override
    public void shutdown() { /* no-op */ }
}

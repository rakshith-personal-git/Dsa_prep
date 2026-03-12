package MC.LoggingLibrary.logger;

import MC.LoggingLibrary.config.LoggerConfig;
import MC.LoggingLibrary.model.LogMessage;

/**
 * Synchronous Logger implementation.
 *
 * Behavior:
 * - Writes to all sinks IMMEDIATELY on the caller's thread.
 * - The log() call blocks until all sinks have finished writing.
 * - Simple and predictable — the caller knows the message is written when log() returns.
 *
 * Thread Safety:
 * ==============
 * Uses `synchronized` on the publish() method to ensure:
 * 1. Mutual exclusion: only one thread writes at a time
 * 2. Message ordering: if Thread A calls log() before Thread B,
 *    Thread A's message will be written first (assuming A acquires the lock first)
 *
 * Trade-offs:
 * - PRO: Simple, no data loss risk, immediate feedback
 * - CON: Caller thread is blocked during I/O (sink writes)
 * - CON: Under high concurrency, threads contend for the lock → potential bottleneck
 *
 * When to use:
 * - Low-throughput applications
 * - When you need guaranteed write-before-return semantics
 * - During development/debugging where simplicity matters
 */
public class SyncLogger extends AbstractLogger {

    public SyncLogger(LoggerConfig config) {
        super(config);
        System.out.println("[SyncLogger] Initialized: " + config.getName()
                + " | Level: " + config.getLogLevel()
                + " | Sinks: " + config.getSinks().size());
    }

    /**
     * Synchronously writes the message to all configured sinks.
     *
     * synchronized ensures:
     * - Thread safety: no interleaved writes from concurrent threads
     * - Ordering: messages are written in the order threads acquire the lock
     *
     * @param message the enriched log message
     */
    @Override
    protected synchronized void publish(LogMessage message) {
        writeToSinks(message);
    }

    /**
     * No-op for SyncLogger — there's nothing to flush since messages
     * are written immediately.
     */
    @Override
    public void close() {
        isClosed = true;
        System.out.println("[SyncLogger] Closed: " + config.getName());
    }
}

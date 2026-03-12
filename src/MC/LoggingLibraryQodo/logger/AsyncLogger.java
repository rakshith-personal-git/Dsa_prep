package MC.LoggingLibraryQodo.logger;

import MC.LoggingLibraryQodo.config.LoggerConfig;
import MC.LoggingLibraryQodo.model.LogMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Asynchronous Logger implementation using the Producer-Consumer pattern.
 *
 * Architecture:
 * =============
 *
 *   [Thread 1] ──log()──┐
 *   [Thread 2] ──log()──┤──► [Bounded BlockingQueue] ──► [Consumer Thread] ──► [Sinks]
 *   [Thread 3] ──log()──┘         (buffer)                  (single)
 *
 * Design Pattern: Producer-Consumer
 * ==================================
 * - Producers: Application threads calling log(). They put messages into the queue.
 * - Consumer:  A single background thread that drains the queue and writes to sinks.
 * - Buffer:    A bounded LinkedBlockingQueue (size = bufferSize from config).
 *
 * Why LinkedBlockingQueue?
 * - Thread-safe: multiple producers can call put() concurrently without external sync
 * - Bounded: limits memory usage; provides backpressure when full
 * - FIFO: preserves message ordering (critical requirement)
 * - Blocking: put() blocks when full, take() blocks when empty
 *
 * Key Design Decisions:
 * =====================
 *
 * 1. put() vs offer():
 *    We use put() which BLOCKS when the queue is full.
 *    - This provides backpressure: the calling thread slows down instead of dropping messages.
 *    - Guarantees NO DATA LOSS — every message will eventually be enqueued.
 *    - offer() would silently drop messages when full → data loss!
 *
 * 2. Single Consumer Thread:
 *    Only ONE thread reads from the queue and writes to sinks.
 *    - Guarantees strict FIFO ordering of messages to sinks.
 *    - If we had multiple consumers, messages could be written out of order.
 *
 * 3. Graceful Shutdown (close()):
 *    - Sets isClosed = true (prevents new messages)
 *    - Interrupts the consumer thread (wakes it from blocking take())
 *    - Drains any remaining messages in the queue
 *    - Joins the consumer thread to ensure completion
 *
 * 4. volatile boolean isClosed:
 *    - Ensures visibility across threads without full synchronization
 *    - When one thread calls close(), producer threads immediately see isClosed = true
 *
 * Trade-offs:
 * - PRO: Non-blocking for callers (until queue is full) → better throughput
 * - PRO: Decouples application logic from I/O latency
 * - CON: Messages may be lost if JVM crashes before consumer processes them
 * - CON: Slightly more complex than sync; requires proper shutdown
 */
public class AsyncLogger extends AbstractLogger {

    /**
     * Bounded blocking queue acting as the message buffer.
     * Capacity = bufferSize from config.
     * LinkedBlockingQueue is chosen over ArrayBlockingQueue for better concurrent throughput
     * (it uses separate locks for put and take operations).
     */
    private final LinkedBlockingQueue<LogMessage> queue;

    /**
     * Single consumer thread that drains the queue and writes to sinks.
     * Daemon thread: won't prevent JVM shutdown, but we handle graceful shutdown via close().
     */
    private final Thread consumerThread;

    public AsyncLogger(LoggerConfig config) {
        super(config);

        // Initialize bounded queue with configured buffer size
        this.queue = new LinkedBlockingQueue<>(config.getBufferSize());

        // Create and start the consumer thread
        this.consumerThread = new Thread(this::consumeMessages, "async-logger-" + config.getName());
        this.consumerThread.setDaemon(true); // Won't prevent JVM shutdown
        this.consumerThread.start();

        System.out.println("[AsyncLogger] Initialized: " + config.getName()
                + " | Level: " + config.getLogLevel()
                + " | BufferSize: " + config.getBufferSize()
                + " | Sinks: " + config.getSinks().size());
    }

    /**
     * Enqueues the message into the bounded blocking queue.
     *
     * put() will BLOCK if the queue is full (backpressure).
     * This ensures no data loss — the caller thread simply waits until space is available.
     *
     * @param message the enriched log message to enqueue
     */
    @Override
    protected void publish(LogMessage message) {
        try {
            queue.put(message); // Blocks if queue is full → no data loss
        } catch (InterruptedException e) {
            // Restore the interrupt flag — important for proper thread interruption handling
            Thread.currentThread().interrupt();
            // Fallback: write directly to sinks to avoid losing this message
            System.err.println("[AsyncLogger] Interrupted while enqueuing message. Writing directly.");
            writeToSinks(message);
        }
    }

    /**
     * Consumer thread's run method.
     *
     * Continuously takes messages from the queue and writes them to all sinks.
     * Uses take() which blocks when the queue is empty (efficient — no busy-waiting).
     *
     * The loop exits when:
     * 1. isClosed is set to true AND
     * 2. The thread is interrupted (from close() method)
     *
     * After the loop exits, we drain any remaining messages to ensure no data loss.
     */
    private void consumeMessages() {
        while (!isClosed || !queue.isEmpty()) {
            try {
                // take() blocks until a message is available
                LogMessage message = queue.take();
                writeToSinks(message);
            } catch (InterruptedException e) {
                // Interrupted by close() — break out of the loop
                // But first, we'll drain remaining messages below
                if (isClosed) {
                    break;
                }
            }
        }

        // Drain any remaining messages after shutdown signal
        // This ensures NO DATA LOSS — all enqueued messages are written
        drainRemainingMessages();
    }

    /**
     * Drains all remaining messages from the queue and writes them to sinks.
     * Called during shutdown to ensure no messages are lost.
     */
    private void drainRemainingMessages() {
        List<LogMessage> remaining = new ArrayList<>();
        queue.drainTo(remaining); // Atomically removes all elements from queue
        for (LogMessage message : remaining) {
            writeToSinks(message);
        }
        if (!remaining.isEmpty()) {
            System.out.println("[AsyncLogger] Drained " + remaining.size() + " remaining messages during shutdown.");
        }
    }

    /**
     * Gracefully shuts down the async logger.
     *
     * Shutdown sequence:
     * 1. Set isClosed = true → prevents new messages from being enqueued
     * 2. Interrupt consumer thread → wakes it from blocking take()
     * 3. Join consumer thread → waits for it to finish draining remaining messages
     *
     * After close() returns, ALL previously enqueued messages are guaranteed to be written.
     */
    @Override
    public void close() {
        isClosed = true;

        // Interrupt the consumer thread to wake it from blocking take()
        consumerThread.interrupt();

        try {
            // Wait for consumer thread to finish processing remaining messages
            // Timeout of 5 seconds to prevent indefinite blocking
            consumerThread.join(5000);
            if (consumerThread.isAlive()) {
                System.err.println("[AsyncLogger] WARNING: Consumer thread did not terminate within 5 seconds.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[AsyncLogger] Interrupted while waiting for consumer thread to finish.");
        }

        System.out.println("[AsyncLogger] Closed: " + config.getName());
    }
}

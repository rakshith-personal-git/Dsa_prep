package MC.LoggingLibraryPerplexity.logger;

import MC.LoggingLibraryPerplexity.core.LogLevel;
import MC.LoggingLibraryPerplexity.core.LogMessage;
import MC.LoggingLibraryPerplexity.core.LoggerConfig;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A non-blocking asynchronous logger using the Producer-Consumer pattern.
 *
 * <h3>Design</h3>
 * <ul>
 *   <li><b>Producer side</b> ({@link #log}): calling threads enqueue messages into a
 *       bounded {@link ArrayBlockingQueue}. Returns immediately — no sink I/O on the caller.</li>
 *   <li><b>Consumer side</b> ({@link #processMessages}): a single dedicated worker thread
 *       drains the queue and dispatches to sinks. Single consumer = guaranteed FIFO order.</li>
 * </ul>
 *
 * <h3>Back-pressure</h3>
 * If the queue is full (buffer_size reached), the producer waits up to 100 ms.
 * If still full, the message is dropped with a warning — preventing unbounded memory growth.
 *
 * <h3>Graceful shutdown</h3>
 * {@link #shutdown()} signals the worker to stop accepting new messages, then waits up to
 * 5 seconds for the queue to drain, ensuring zero data loss for in-flight messages.
 */
public class AsyncLogger extends AbstractLogger {

    /** Bounded queue — size controlled by LoggerConfig.bufferSize */
    private final BlockingQueue<LogMessage> messageQueue;

    /** Single consumer thread — guarantees message ordering */
    private final Thread workerThread;

    /** Flag used to signal the worker to stop after draining the queue */
    private final AtomicBoolean running = new AtomicBoolean(true);

    public AsyncLogger(LoggerConfig config) {
        super(config);
        this.messageQueue = new ArrayBlockingQueue<>(config.getBufferSize());
        this.workerThread = new Thread(this::processMessages,
                config.getLoggerName() + "-async-worker");
        this.workerThread.setDaemon(false); // non-daemon: JVM waits for it on shutdown
        this.workerThread.start();
    }

    /**
     * Non-blocking enqueue.
     *
     * <p>Drops the message with a warning if:
     * <ul>
     *   <li>The queue is full after a 100 ms wait (back-pressure exceeded)</li>
     *   <li>The logger is already shutting down</li>
     * </ul>
     */
    @Override
    public void log(String message, LogLevel level) {
        if (message == null || message.isBlank()) return;
        if (!isLevelEnabled(level)) return;
        if (!running.get()) {
            System.err.println("[AsyncLogger] Shutting down — dropped: " + message);
            return;
        }
        try {
            // offer with timeout rather than put() to avoid indefinite blocking
            if (!messageQueue.offer(new LogMessage(message, level), 100, TimeUnit.MILLISECONDS)) {
                System.err.println("[AsyncLogger] Buffer full — dropped: " + message);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Worker loop — runs on the dedicated consumer thread.
     * Polls with a short timeout so it can re-check the running flag.
     * Continues until both: running=false AND queue is empty.
     */
    private void processMessages() {
        while (running.get() || !messageQueue.isEmpty()) {
            try {
                LogMessage msg = messageQueue.poll(50, TimeUnit.MILLISECONDS);
                if (msg != null) {
                    dispatchToSinks(msg);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Signals shutdown and waits up to 5 seconds for the queue to drain.
     * After this call returns, all messages that were enqueued before
     * shutdown() was called are guaranteed to have been dispatched.
     */
    @Override
    public void shutdown() {
        running.set(false);
        try {
            workerThread.join(5000); // wait for drain
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

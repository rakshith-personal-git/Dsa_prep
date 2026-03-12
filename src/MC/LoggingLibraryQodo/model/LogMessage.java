package MC.LoggingLibraryQodo.model;

/**
 * Immutable value object representing a single log entry.
 *
 * Contains:
 * - content:   the actual log message string
 * - level:     severity level (DEBUG, INFO, WARN, ERROR, FATAL)
 * - timestamp: pre-formatted timestamp string, set at creation time
 *
 * Design Pattern: Immutable Object
 * ---------------------------------
 * All fields are final and set via constructor. No setters.
 * This guarantees thread-safety: once created, a LogMessage can be safely
 * passed between threads (e.g., from producer to consumer in AsyncLogger)
 * without any synchronization.
 *
 * The timestamp is enriched by the Logger (AbstractLogger) at the moment
 * the log() call is made, NOT when the message reaches the sink.
 * This ensures the timestamp reflects when the event occurred, not when
 * it was processed.
 */
public class LogMessage {
    private final String content;
    private final LogLevel level;
    private final String timestamp;

    public LogMessage(String content, LogLevel level, String timestamp) {
        this.content = content;
        this.level = level;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Formats the log message in the standard output format:
     * "timestamp [LEVEL] content"
     *
     * Example: "03-01-2024-09-30-00 [INFO] This is a sample log message."
     */
    @Override
    public String toString() {
        return timestamp + " [" + level.name() + "] " + content;
    }
}

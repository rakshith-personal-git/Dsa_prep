package MC.LoggingLibraryPerplexity.core;

import java.time.LocalDateTime;

/**
 * Immutable value object representing a single log entry.
 *
 * <p>Timestamp is captured at construction time to preserve ordering
 * semantics even when messages are queued in the async logger.
 */
public class LogMessage {

    private final String content;
    private final LogLevel level;
    private final LocalDateTime timestamp;

    /**
     * @param content the log message text (must not be null/blank — enforced by logger)
     * @param level   the severity level of this message
     */
    public LogMessage(String content, LogLevel level) {
        this.content   = content;
        this.level     = level;
        this.timestamp = LocalDateTime.now(); // captured immediately on creation
    }

    public String getContent()           { return content; }
    public LogLevel getLevel()           { return level; }
    public LocalDateTime getTimestamp()  { return timestamp; }
}

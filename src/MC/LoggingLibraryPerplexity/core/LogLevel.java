package MC.LoggingLibraryPerplexity.core;

/**
 * Represents the severity level of a log message.
 *
 * <p>Levels are ordered by priority (lowest → highest):
 * DEBUG < INFO < WARN < ERROR < FATAL
 *
 * <p>Used both for filtering at the Logger level and at individual Sink level.
 */
public enum LogLevel {

    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3),
    FATAL(4);

    private final int priority;

    LogLevel(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * Returns true if this level's priority is >= the other level's priority.
     * Used to decide whether a message passes a level filter.
     */
    public boolean isAtLeast(LogLevel other) {
        return this.priority >= other.priority;
    }


}

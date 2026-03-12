package MC.LoggingLibraryQodo.model;

/**
 * Enum representing log severity levels in ascending order of priority.
 *
 * Priority order: DEBUG(0) < INFO(1) < WARN(2) < ERROR(3) < FATAL(4)
 *
 * Used by both Logger and Sink to filter messages:
 * - Logger-level filter: messages below the logger's configured level are discarded early.
 * - Sink-level filter: each sink can independently filter based on its own level threshold.
 *
 * Design Note: Using an integer priority allows simple comparison via getPriority().
 * A message is accepted if: message.level.priority >= threshold.level.priority
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
}

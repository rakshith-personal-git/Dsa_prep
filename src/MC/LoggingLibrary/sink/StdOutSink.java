package MC.LoggingLibrary.sink;

import MC.LoggingLibrary.model.LogLevel;
import MC.LoggingLibrary.model.LogMessage;

/**
 * Concrete Sink implementation that writes log messages to System.out (console).
 *
 * Strategy Pattern: This is one concrete strategy for the Sink interface.
 * The Logger doesn't know or care that this writes to stdout — it just
 * calls write() on whatever Sink implementations are configured.
 *
 * Sink-Level Filtering:
 * ---------------------
 * Each sink has its own log level threshold. A message is only written
 * if its priority >= this sink's level priority.
 *
 * Example: If sink level is WARN, then:
 *   - DEBUG → discarded
 *   - INFO  → discarded
 *   - WARN  → written ✓
 *   - ERROR → written ✓
 *   - FATAL → written ✓
 *
 * Thread Safety:
 * System.out.println() is internally synchronized, so concurrent calls
 * from multiple threads won't produce garbled output.
 */
public class StdOutSink implements Sink {
    private final LogLevel level;

    /**
     * Creates a StdOutSink with the specified minimum log level.
     *
     * @param level minimum level for messages to be printed
     */
    public StdOutSink(LogLevel level) {
        this.level = level;
    }

    /**
     * Writes the log message to System.out if it meets the level threshold.
     * Uses LogMessage.toString() which formats as: "timestamp [LEVEL] content"
     *
     * @param message the log message to write
     */
    @Override
    public void write(LogMessage message) {
        // Sink-level filtering: only write if message priority >= sink's threshold
        if (message.getLevel().getPriority() >= level.getPriority()) {
            System.out.println(message.toString());
        }
    }

    @Override
    public LogLevel getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "StdOutSink{level=" + level + "}";
    }
}

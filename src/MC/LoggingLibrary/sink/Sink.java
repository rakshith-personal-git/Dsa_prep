package MC.LoggingLibrary.sink;

import MC.LoggingLibrary.model.LogLevel;
import MC.LoggingLibrary.model.LogMessage;

/**
 * Strategy Pattern: Sink Interface
 * ==================================
 * Defines the contract for all log output destinations.
 *
 * Each Sink implementation represents a different output strategy:
 * - StdOutSink  → prints to System.out (console)
 * - FileSink    → writes to a file (future extension)
 * - DatabaseSink → writes to a DB (future extension)
 *
 * Each sink has its own log level threshold. Messages with a level
 * BELOW the sink's threshold are silently discarded. This allows
 * different sinks to capture different severity levels independently.
 *
 * For example:
 * - Console sink at DEBUG level → sees everything
 * - File sink at ERROR level   → only captures ERROR and FATAL
 *
 * To add a new sink type:
 * 1. Create a new class implementing this interface
 * 2. Implement write() with the destination-specific logic
 * 3. Pass it into LoggerConfig — no changes to Logger code needed
 */
public interface Sink {

    /**
     * Writes a log message to this sink's destination.
     * Implementations should check the message level against getLevel()
     * and discard messages below the threshold.
     *
     * @param message the enriched log message (with timestamp)
     */
    void write(LogMessage message);

    /**
     * Returns the minimum log level this sink will accept.
     * Messages with level.priority < getLevel().priority are discarded.
     *
     * @return the sink's log level threshold
     */
    LogLevel getLevel();
}

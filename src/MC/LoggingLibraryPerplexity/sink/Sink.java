package MC.LoggingLibraryPerplexity.sink;

import MC.LoggingLibraryPerplexity.core.LogLevel;
import MC.LoggingLibraryPerplexity.core.LogMessage;

/**
 * Contract for all log destinations (sinks).
 *
 * <p>Each Sink has its own level filter: messages whose level is below
 * the sink's configured level will not be written.
 *
 * <p>To add a new sink type (e.g. DatabaseSink, HttpSink), simply implement
 * this interface — no changes needed to Logger or LoggerFactory.
 */
public interface Sink {

    /**
     * Write the formatted log entry to this sink's destination.
     *
     * @param message       the original {@link LogMessage} (metadata)
     * @param formattedEntry the pre-formatted string ready for output
     */
    void write(LogMessage message, String formattedEntry);

    /** The minimum log level this sink will accept. */
    LogLevel getSinkLevel();

    /** Human-readable destination label (e.g. "STDOUT", file path). */
    String getDestination();
}

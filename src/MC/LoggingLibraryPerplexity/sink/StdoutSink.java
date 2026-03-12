package MC.LoggingLibraryPerplexity.sink;

import MC.LoggingLibraryPerplexity.core.LogLevel;
import MC.LoggingLibraryPerplexity.core.LogMessage;

/**
 * A {@link Sink} that writes formatted log entries to standard output (console).
 *
 * <p>{@code System.out.println} is used directly. For high-throughput scenarios
 * consider wrapping in a BufferedWriter, but for this library scope it is sufficient.
 */
public class StdoutSink implements Sink {

    private final LogLevel sinkLevel;

    /**
     * @param sinkLevel minimum level required for a message to be printed
     */
    public StdoutSink(LogLevel sinkLevel) {
        this.sinkLevel = sinkLevel;
    }

    @Override
    public void write(LogMessage message, String formattedEntry) {
        // Thread-safety: System.out.println is internally synchronized
        System.out.println(formattedEntry);
    }

    @Override
    public LogLevel getSinkLevel() { return sinkLevel; }

    @Override
    public String getDestination() { return "STDOUT"; }
}

package MC.LoggingLibraryPerplexity.sink;

import MC.LoggingLibraryPerplexity.core.LogLevel;
import MC.LoggingLibraryPerplexity.core.LogMessage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A {@link Sink} that appends formatted log entries to a file on disk.
 *
 * <p>Opens the file in append mode so restarts don't overwrite existing logs.
 * The {@code write} method is {@code synchronized} to prevent interleaved
 * writes from multiple threads.
 *
 * <p>Always call {@link #close()} when done to flush and release the file handle.
 */
public class FileSink implements Sink {

    private final LogLevel sinkLevel;
    private final String filePath;
    private final BufferedWriter writer;

    /**
     * @param sinkLevel minimum level for a message to be written to this file
     * @param filePath  path to the log file (created if it does not exist)
     * @throws IOException if the file cannot be opened
     */
    public FileSink(LogLevel sinkLevel, String filePath) throws IOException {
        this.sinkLevel = sinkLevel;
        this.filePath  = filePath;
        // append=true: existing log entries are preserved across runs
        this.writer = new BufferedWriter(new FileWriter(filePath, true));
    }

    @Override
    public synchronized void write(LogMessage message, String formattedEntry) {
        try {
            writer.write(formattedEntry);
            writer.newLine();
            writer.flush(); // flush per entry to minimise data loss on crash
        } catch (IOException e) {
            System.err.println("[FileSink] Write error to " + filePath + ": " + e.getMessage());
        }
    }

    @Override
    public LogLevel getSinkLevel() { return sinkLevel; }

    @Override
    public String getDestination() { return filePath; }

    /** Closes the underlying writer. Call this during application shutdown. */
    public void close() {
        try { writer.close(); } catch (IOException ignored) {}
    }
}

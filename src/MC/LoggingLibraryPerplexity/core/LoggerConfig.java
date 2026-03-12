package MC.LoggingLibraryPerplexity.core;

import MC.LoggingLibraryPerplexity.sink.Sink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable configuration for a Logger instance.
 *
 * <p>Built via the fluent {@link Builder} to keep construction readable
 * and to enforce required fields at build time.
 *
 * <p>Example:
 * <pre>{@code
 * LoggerConfig config = new LoggerConfig.Builder()
 *     .loggerName("app-logger")
 *     .logLevel(LogLevel.INFO)
 *     .loggerType(LoggerType.ASYNC)
 *     .bufferSize(25)
 *     .timestampFormat("dd-MM-yyyy-HH-mm-ss")
 *     .addSink(new StdoutSink(LogLevel.INFO))
 *     .build();
 * }</pre>
 */
public class LoggerConfig {

    private final String loggerName;
    private final LogLevel logLevel;
    private final LoggerType loggerType;
    private final int bufferSize;
    private final String timestampFormat;
    private final List<Sink> sinks;

    private LoggerConfig(Builder b) {
        this.loggerName      = b.loggerName;
        this.logLevel        = b.logLevel;
        this.loggerType      = b.loggerType;
        this.bufferSize      = b.bufferSize;
        this.timestampFormat = b.timestampFormat;
        this.sinks           = Collections.unmodifiableList(new ArrayList<>(b.sinks));
    }

    public String      getLoggerName()     { return loggerName; }
    public LogLevel    getLogLevel()       { return logLevel; }
    public LoggerType  getLoggerType()     { return loggerType; }
    public int         getBufferSize()     { return bufferSize; }
    public String      getTimestampFormat(){ return timestampFormat; }
    public List<Sink>  getSinks()          { return sinks; }

    // ── Builder ──────────────────────────────────────────────────────────────

    public static class Builder {
        private String     loggerName      = "default";
        private LogLevel   logLevel        = LogLevel.INFO;
        private LoggerType loggerType      = LoggerType.SYNC;
        private int        bufferSize      = 100;
        private String     timestampFormat = "dd-MM-yyyy-HH-mm-ss";
        private List<Sink> sinks           = new ArrayList<>();

        public Builder loggerName(String v)      { this.loggerName = v;      return this; }
        public Builder logLevel(LogLevel v)       { this.logLevel = v;        return this; }
        public Builder loggerType(LoggerType v)   { this.loggerType = v;      return this; }
        public Builder bufferSize(int v)          { this.bufferSize = v;      return this; }
        public Builder timestampFormat(String v)  { this.timestampFormat = v; return this; }

        /** Attach a sink. Multiple sinks are allowed; each filters independently. */
        public Builder addSink(Sink s) {
            this.sinks.add(s);
            return this;
        }

        /**
         * Validates and constructs the {@link LoggerConfig}.
         * @throws IllegalStateException if no sinks have been added
         */
        public LoggerConfig build() {
            if (sinks.isEmpty()) {
                throw new IllegalStateException("At least one sink must be configured.");
            }
            return new LoggerConfig(this);
        }
    }
}

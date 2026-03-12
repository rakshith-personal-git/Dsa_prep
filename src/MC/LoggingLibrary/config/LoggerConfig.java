package MC.LoggingLibrary.config;

import MC.LoggingLibrary.model.LogLevel;
import MC.LoggingLibrary.sink.Sink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configuration object for initializing a Logger instance.
 *
 * Design Pattern: Builder Pattern
 * ================================
 * Uses a nested static Builder class for clean, readable construction.
 *
 * Why Builder?
 * - LoggerConfig has many parameters, some optional (bufferSize only for ASYNC)
 * - Avoids telescoping constructors (constructor with 2 params, 3 params, 4 params...)
 * - Makes the construction code self-documenting:
 *
 *   LoggerConfig config = new LoggerConfig.Builder("my-logger")
 *       .logLevel(LogLevel.INFO)
 *       .loggerType(LoggerType.ASYNC)
 *       .bufferSize(25)
 *       .timestampFormat("dd-MM-yyyy-HH-mm-ss")
 *       .addSink(new StdOutSink(LogLevel.INFO))
 *       .build();
 *
 * Immutability:
 * - Once built, the config is immutable (all fields final, sinks list is unmodifiable)
 * - Safe to share across threads without synchronization
 *
 * Validation:
 * - build() validates required fields and constraints (e.g., bufferSize > 0 for ASYNC)
 */
public class LoggerConfig {

    /**
     * Enum to specify whether the logger operates synchronously or asynchronously.
     */
    public enum LoggerType {
        SYNC,
        ASYNC
    }

    private final String name;
    private final LogLevel logLevel;
    private final LoggerType loggerType;
    private final int bufferSize;
    private final String timestampFormat;
    private final List<Sink> sinks;

    /**
     * Private constructor — only accessible via Builder.build()
     */
    private LoggerConfig(Builder builder) {
        this.name = builder.name;
        this.logLevel = builder.logLevel;
        this.loggerType = builder.loggerType;
        this.bufferSize = builder.bufferSize;
        this.timestampFormat = builder.timestampFormat;
        // Wrap in unmodifiable list to preserve immutability
        this.sinks = Collections.unmodifiableList(new ArrayList<>(builder.sinks));
    }

    // ==================== Getters ====================

    public String getName() {
        return name;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public LoggerType getLoggerType() {
        return loggerType;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public List<Sink> getSinks() {
        return sinks;
    }

    // ==================== Builder ====================

    /**
     * Builder class for constructing LoggerConfig instances.
     *
     * Required: name (passed in constructor)
     * Optional: logLevel (default INFO), loggerType (default SYNC),
     *           bufferSize (default 10), timestampFormat (default "dd-MM-yyyy-HH-mm-ss")
     */
    public static class Builder {
        // Required
        private final String name;

        // Optional with defaults
        private LogLevel logLevel = LogLevel.INFO;
        private LoggerType loggerType = LoggerType.SYNC;
        private int bufferSize = 10;
        private String timestampFormat = "dd-MM-yyyy-HH-mm-ss";
        private final List<Sink> sinks = new ArrayList<>();

        /**
         * @param name unique identifier for this logger
         */
        public Builder(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Logger name cannot be null or blank");
            }
            this.name = name;
        }

        public Builder logLevel(LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public Builder loggerType(LoggerType loggerType) {
            this.loggerType = loggerType;
            return this;
        }

        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder timestampFormat(String timestampFormat) {
            this.timestampFormat = timestampFormat;
            return this;
        }

        /**
         * Adds a sink to the logger configuration.
         * Multiple sinks can be added — the logger will fan out messages to all of them.
         */
        public Builder addSink(Sink sink) {
            if (sink == null) {
                throw new IllegalArgumentException("Sink cannot be null");
            }
            this.sinks.add(sink);
            return this;
        }

        /**
         * Validates and builds the LoggerConfig.
         *
         * Validation rules:
         * - At least one sink must be configured
         * - ASYNC logger must have bufferSize > 0
         */
        public LoggerConfig build() {
            if (sinks.isEmpty()) {
                throw new IllegalArgumentException("At least one sink must be configured");
            }
            if (loggerType == LoggerType.ASYNC && bufferSize <= 0) {
                throw new IllegalArgumentException("Buffer size must be > 0 for ASYNC logger");
            }
            return new LoggerConfig(this);
        }
    }

    @Override
    public String toString() {
        return "LoggerConfig{" +
                "name='" + name + '\'' +
                ", logLevel=" + logLevel +
                ", loggerType=" + loggerType +
                ", bufferSize=" + bufferSize +
                ", timestampFormat='" + timestampFormat + '\'' +
                ", sinks=" + sinks +
                '}';
    }
}

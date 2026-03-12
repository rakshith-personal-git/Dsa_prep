package MC.LoggingLibraryPerplexity.logger;

import MC.LoggingLibraryPerplexity.core.LogLevel;
import MC.LoggingLibraryPerplexity.core.LogMessage;
import MC.LoggingLibraryPerplexity.core.LoggerConfig;
import MC.LoggingLibraryPerplexity.sink.Sink;

import java.time.format.DateTimeFormatter;

/**
 * Base class for all logger implementations.
 *
 * <p>Centralises shared responsibilities:
 * <ul>
 *   <li>Convenience shorthand methods (debug/info/warn/error/fatal)</li>
 *   <li>Message formatting with configurable timestamp pattern</li>
 *   <li>Two-layer level filtering: logger-level gate + per-sink gate</li>
 *   <li>Dispatch to all attached sinks</li>
 * </ul>
 *
 * <p>Subclasses only need to implement {@link #log(String, LogLevel)} and
 * {@link #shutdown()}, keeping each implementation focused and DRY.
 */
public abstract class AbstractLogger implements Logger {

    protected final LoggerConfig config;
    protected final DateTimeFormatter formatter;

    protected AbstractLogger(LoggerConfig config) {
        this.config = config;
        // Pre-compile the pattern once — DateTimeFormatter is thread-safe
        this.formatter = DateTimeFormatter.ofPattern(config.getTimestampFormat());
    }

    // ── Convenience methods ───────────────────────────────────────────────────

    @Override
    public void debug(String msg) {
        log(msg, LogLevel.DEBUG);
    }

    @Override
    public void info(String msg) {
        log(msg, LogLevel.INFO);
    }

    @Override
    public void warn(String msg) {
        log(msg, LogLevel.WARN);
    }

    @Override
    public void error(String msg) {
        log(msg, LogLevel.ERROR);
    }

    @Override
    public void fatal(String msg) {
        log(msg, LogLevel.FATAL);
    }

    @Override
    public String getName() {
        return config.getLoggerName();
    }

    // ── Formatting ────────────────────────────────────────────────────────────

    /**
     * Formats a LogMessage into the output string.
     * Pattern: {@code <timestamp> [LEVEL] message content}
     * Example: {@code 11-03-2026-22-00-01 [INFO] Server started}
     */
    protected String format(LogMessage message) {
        return message.getTimestamp().format(formatter)
                + " [" + message.getLevel().name() + "] "
                + message.getContent();
    }

    // ── Dispatch ──────────────────────────────────────────────────────────────

    /**
     * Sends the message to every sink whose level filter is satisfied.
     *
     * <p>Two-layer filtering:
     * <ol>
     *   <li>Logger-level gate: checked before enqueuing/writing (cheap early exit)</li>
     *   <li>Sink-level gate: checked here to allow different sinks to have different thresholds</li>
     * </ol>
     */
    protected void dispatchToSinks(LogMessage message) {
        String formatted = format(message);
        for (Sink sink : config.getSinks()) {
            if (message.getLevel().isAtLeast(sink.getSinkLevel())) {
                sink.write(message, formatted);
            }
        }
    }

    /**
     * Logger-level gate: returns true if the given level meets the
     * minimum configured for this logger.
     */
    protected boolean isLevelEnabled(LogLevel level) {
        return level.isAtLeast(config.getLogLevel());
    }
}

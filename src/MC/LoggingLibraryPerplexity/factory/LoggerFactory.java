package MC.LoggingLibraryPerplexity.factory;

import MC.LoggingLibraryPerplexity.core.LoggerConfig;
import MC.LoggingLibraryPerplexity.core.LoggerType;
import MC.LoggingLibraryPerplexity.logger.AsyncLogger;
import MC.LoggingLibraryPerplexity.logger.Logger;
import MC.LoggingLibraryPerplexity.logger.SyncLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory and registry for Logger instances.
 *
 * <p>Ensures that only one Logger exists per name across the application
 * (singleton-per-name pattern via {@link ConcurrentHashMap#computeIfAbsent}).
 * This prevents duplicate sinks and double-writing.
 *
 * <p>To add a new logger type, add a case to {@link #createLogger}.
 */
public class LoggerFactory {

    /** Registry: loggerName → Logger instance */
    private static final Map<String, Logger> registry = new ConcurrentHashMap<>();

    private LoggerFactory() {} // utility class — no instances

    /**
     * Returns existing logger for the given name, or creates a new one.
     * Thread-safe due to ConcurrentHashMap.computeIfAbsent.
     */
    public static Logger getLogger(LoggerConfig config) {
        return registry.computeIfAbsent(config.getLoggerName(), name -> createLogger(config));
    }

    /** Instantiates the correct Logger subclass based on LoggerType. */
    private static Logger createLogger(LoggerConfig config) {
        return config.getLoggerType() == LoggerType.ASYNC
                ? new AsyncLogger(config)
                : new SyncLogger(config);
    }

    /** Shuts down all registered loggers and clears the registry. */
    public static void shutdownAll() {
        registry.values().forEach(Logger::shutdown);
        registry.clear();
    }
}

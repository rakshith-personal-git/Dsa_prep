package MC.LoggingLibraryPerplexity.util;

import MC.LoggingLibraryPerplexity.core.LoggerConfig;
import MC.LoggingLibraryPerplexity.factory.LoggerFactory;
import MC.LoggingLibraryPerplexity.logger.Logger;

/**
 * Public façade / entry point for the logging library.
 *
 * <p>Application code should interact with the library only through this class,
 * keeping it decoupled from internal implementation details (Factory, concrete
 * Logger types, etc.).
 *
 * <p>Usage:
 * <pre>{@code
 * Logger logger = LoggingLibrary.initialize(config);
 * logger.info("App started");
 * // ... on app exit:
 * LoggingLibrary.shutdownAll();
 * }</pre>
 */
public class LoggingLibrary {

    private LoggingLibrary() {} // static utility — prevent instantiation

    /**
     * Initialises (or retrieves) a Logger for the given configuration.
     * Calling this multiple times with the same loggerName returns the same instance.
     *
     * @param config fully built {@link LoggerConfig}
     * @return a ready-to-use {@link Logger}
     */
    public static Logger initialize(LoggerConfig config) {
        return LoggerFactory.getLogger(config);
    }

    /**
     * Shuts down all loggers, draining async queues.
     * Call once during application exit.
     */
    public static void shutdownAll() {
        LoggerFactory.shutdownAll();
    }
}

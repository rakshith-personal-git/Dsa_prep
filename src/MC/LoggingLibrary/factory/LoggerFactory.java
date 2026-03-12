package MC.LoggingLibrary.factory;

import MC.LoggingLibrary.config.LoggerConfig;
import MC.LoggingLibrary.logger.AsyncLogger;
import MC.LoggingLibrary.logger.Logger;
import MC.LoggingLibrary.logger.SyncLogger;

/**
 * Factory for creating Logger instances from configuration.
 *
 * Design Pattern: Factory Pattern (Simple Factory)
 * =================================================
 * Encapsulates the object creation logic — the client doesn't need to know
 * which concrete Logger class to instantiate. It just provides a config
 * and gets back a Logger interface.
 *
 * Why Factory?
 * - Decouples client code from concrete Logger implementations
 * - Centralizes creation logic — if we add a new logger type (e.g., BatchLogger),
 *   we only modify this factory, not every place that creates loggers
 * - Client code programs to the Logger interface, not to SyncLogger/AsyncLogger
 *
 * Usage:
 *   Logger logger = LoggerFactory.createLogger(config);
 *   // Client doesn't know or care if it's sync or async
 *   logger.info("Hello");
 *   logger.close();
 */
public class LoggerFactory {

    /**
     * Creates a Logger instance based on the provided configuration.
     *
     * @param config the logger configuration specifying type, level, sinks, etc.
     * @return a Logger instance (SyncLogger or AsyncLogger)
     * @throws IllegalArgumentException if the logger type is not recognized
     */
    public static Logger createLogger(LoggerConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("LoggerConfig cannot be null");
        }

        switch (config.getLoggerType()) {
            case SYNC:
                return new SyncLogger(config);
            case ASYNC:
                return new AsyncLogger(config);
            default:
                throw new IllegalArgumentException("Unknown logger type: " + config.getLoggerType());
        }
    }
}

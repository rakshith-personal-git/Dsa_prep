package MC.LoggingLibraryPerplexity.core;

/**
 * Determines whether a Logger processes messages synchronously (blocking)
 * or asynchronously (non-blocking, via an internal queue + worker thread).
 */
public enum LoggerType {
    /** Caller thread writes directly to sinks. Simple, ordered, blocking. */
    SYNC,

    /** Messages are queued and consumed by a dedicated background thread. */
    ASYNC
}

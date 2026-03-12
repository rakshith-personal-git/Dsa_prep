package MC.PubSubLibrary.retention;

import MC.PubSubLibrary.model.Message;

/**
 * Pluggable policy for message retention. Decides whether a message should be evicted.
 * Enables different strategies (time-based, size-based, etc.) without changing core storage.
 */
public interface RetentionPolicy {

    /**
     * Returns true if the message should be evicted (removed) from the topic.
     *
     * @param message  the message to evaluate
     * @param nowMs    current time in milliseconds (allows deterministic testing)
     */
    boolean shouldEvict(Message message, long nowMs);
}

package MC.PubSubLibrary.api;

/**
 * Contract for publishing messages to a single topic. Thread-safe for parallel publish.
 */
public interface Publisher {

    /**
     * Publishes a message to the topic.
     *
     * @return the offset assigned to the message
     * @throws MC.PubSubLibrary.exception.PubSubException if publish fails
     */
    long publish(String message);
}

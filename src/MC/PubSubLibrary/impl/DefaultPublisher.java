package MC.PubSubLibrary.impl;

import MC.PubSubLibrary.api.Publisher;
import MC.PubSubLibrary.exception.PubSubException;

/**
 * Default in-memory publisher. Thread-safe for parallel publish.
 */
final class DefaultPublisher implements Publisher {
    private final InMemoryTopic topic;

    DefaultPublisher(InMemoryTopic topic) {
        this.topic = topic;
    }

    @Override
    public long publish(String message) {
        if (message == null) {
            throw new PubSubException("Message content cannot be null");
        }
        try {
            return topic.publish(message);
        } catch (Exception e) {
            throw new PubSubException("Failed to publish message", e);
        }
    }
}

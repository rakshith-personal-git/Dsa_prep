package MC.PubSubLibrary.api;

import MC.PubSubLibrary.model.Message;

/**
 * Contract for the message broker. Manages topic lifecycle and creates publishers/consumers.
 * Implementations can be in-memory, persistent, or distributed.
 */
public interface Broker {

    /**
     * Creates a topic with the given configuration.
     *
     * @throws MC.PubSubLibrary.exception.PubSubException if topic already exists or config is invalid
     */
    void createTopic(TopicConfig config);

    /**
     * Creates a topic with name and retention period (convenience).
     */
    default void createTopic(String name, long retentionPeriodMs) {
        createTopic(TopicConfig.of(name, retentionPeriodMs));
    }

    /**
     * Deletes the topic. Handles to publishers/consumers for this topic become invalid.
     *
     * @throws MC.PubSubLibrary.exception.TopicNotFoundException if topic does not exist
     */
    void deleteTopic(String topicName);

    /**
     * Creates a publisher for the given topic.
     *
     * @throws MC.PubSubLibrary.exception.TopicNotFoundException if topic does not exist
     */
    Publisher createPublisher(String topicName);

    /**
     * Creates a consumer for the given topic. Each consumer has its own offset.
     *
     * @throws MC.PubSubLibrary.exception.TopicNotFoundException if topic does not exist
     */
    Consumer createConsumer(String topicName);

    /**
     * Topic-level visibility: start offset, end offset, message count (Bonus-2).
     *
     * @throws MC.PubSubLibrary.exception.TopicNotFoundException if topic does not exist
     */
    TopicInfo getTopicInfo(String topicName);
}

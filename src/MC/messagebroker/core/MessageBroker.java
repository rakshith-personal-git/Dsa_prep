package MC.messagebroker.core;

import MC.messagebroker.model.ConsumerLag;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface MessageBroker {
    void createTopic(String topicName, Duration retentionPeriod);

    void deleteTopic(String topicName);

    List<String> listTopics();

    Publisher createPublisher(String topicName);

    Consumer createConsumer(String consumerId, String topicName, MessageHandler handler);

    Map<String, ConsumerLag> getTopicLag(String topicName);

    void shutdown();
}

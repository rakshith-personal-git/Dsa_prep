package MC.PubSubLibrary.impl;

import MC.PubSubLibrary.api.Broker;
import MC.PubSubLibrary.api.Consumer;
import MC.PubSubLibrary.api.Publisher;
import MC.PubSubLibrary.api.TopicConfig;
import MC.PubSubLibrary.api.TopicInfo;
import MC.PubSubLibrary.exception.PubSubException;
import MC.PubSubLibrary.exception.TopicNotFoundException;
import MC.PubSubLibrary.retention.RetentionPolicy;
import MC.PubSubLibrary.retention.TimeBasedRetentionPolicy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of {@link Broker}. Thread-safe; supports parallel publish and multiple topics.
 */
public final class InMemoryBroker implements Broker {
    private final Map<String, InMemoryTopic> topics;

    public InMemoryBroker() {
        this.topics = new ConcurrentHashMap<>();
    }

    @Override
    public void createTopic(TopicConfig config) {
        if (config == null) {
            throw new PubSubException("TopicConfig cannot be null");
        }
        String name = config.getName();
        RetentionPolicy policy = new TimeBasedRetentionPolicy(config.getRetentionPeriodMs());
        InMemoryTopic topic = new InMemoryTopic(name, policy);
        InMemoryTopic existing = topics.putIfAbsent(name, topic);
        if (existing != null) {
            throw new PubSubException("Topic already exists: " + name);
        }
    }

    @Override
    public void deleteTopic(String topicName) {
        InMemoryTopic removed = topics.remove(topicName);
        if (removed == null) {
            throw new TopicNotFoundException(topicName);
        }
    }

    @Override
    public Publisher createPublisher(String topicName) {
        InMemoryTopic topic = getTopic(topicName);
        return new DefaultPublisher(topic);
    }

    @Override
    public Consumer createConsumer(String topicName) {
        InMemoryTopic topic = getTopic(topicName);
        return new DefaultConsumer(topic);
    }

    @Override
    public TopicInfo getTopicInfo(String topicName) {
        InMemoryTopic topic = getTopic(topicName);
        long start = topic.getStartOffset();
        long end = topic.getEndOffset();
        return new TopicInfo(topicName, start, end, end - start);
    }

    private InMemoryTopic getTopic(String topicName) {
        InMemoryTopic topic = topics.get(topicName);
        if (topic == null) {
            throw new TopicNotFoundException(topicName);
        }
        return topic;
    }
}

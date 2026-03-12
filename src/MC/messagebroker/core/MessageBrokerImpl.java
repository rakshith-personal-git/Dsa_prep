package MC.messagebroker.core;

import MC.messagebroker.exception.ConsumerAlreadyRegisteredException;
import MC.messagebroker.model.ConsumerLag;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class MessageBrokerImpl implements MessageBroker {

    private final TopicRegistry registry = new TopicRegistry();
    private final Set<String> registeredConsumers = ConcurrentHashMap.newKeySet();
    private volatile boolean shutdown = false;

    @Override
    public void createTopic(String topicName, Duration retentionPeriod) {
        ensureRunning();
        Objects.requireNonNull(topicName);
        Objects.requireNonNull(retentionPeriod);
        registry.create(topicName, retentionPeriod);
    }

    @Override
    public void deleteTopic(String topicName) {
        ensureRunning();
        registry.delete(topicName);
        registeredConsumers.removeIf(k -> k.endsWith("@" + topicName));
    }

    @Override
    public List<String> listTopics() {
        return registry.listTopics();
    }

    @Override
    public Publisher createPublisher(String topicName) {
        ensureRunning();
        registry.getOrThrow(topicName);
        return new PublisherImpl(topicName, registry);
    }

    @Override
    public Consumer createConsumer(String consumerId, String topicName, MessageHandler handler) {
        ensureRunning();
        Objects.requireNonNull(consumerId);
        Objects.requireNonNull(handler);
        String key = consumerId + "@" + topicName;
        if (!registeredConsumers.add(key)) {
            throw new ConsumerAlreadyRegisteredException(consumerId, topicName);
        }
        registry.getOrThrow(topicName).registerConsumer(consumerId, handler);
        return new ConsumerImpl(consumerId, topicName, registry);
    }

    @Override
    public Map<String, ConsumerLag> getTopicLag(String topicName) {
        TopicPartition tp = registry.getOrThrow(topicName);
        long hwm = tp.getHighWatermark();
        Map<String, ConsumerLag> result = new LinkedHashMap<>();
        registeredConsumers.stream()
                .filter(k -> k.endsWith("@" + topicName))
                .map(k -> k.substring(0, k.lastIndexOf('@')))
                .forEach(cid -> result.put(cid,
                        new ConsumerLag(cid, topicName, tp.getOffset(cid) - 1, hwm)));
        return Collections.unmodifiableMap(result);
    }

    @Override
    public void shutdown() {
        shutdown = true;
        registry.closeAll();
    }

    private void ensureRunning() {
        if (shutdown) {
            throw new MC.messagebroker.exception.BrokerException("Broker is shut down");
        }
    }
}

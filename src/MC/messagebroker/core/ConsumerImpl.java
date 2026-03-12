package MC.messagebroker.core;

import MC.messagebroker.model.ConsumerLag;
import MC.messagebroker.model.Message;

import java.util.List;

final class ConsumerImpl implements Consumer {

    private final String consumerId;
    private final String topicName;
    private final TopicRegistry registry;

    ConsumerImpl(String consumerId, String topicName, TopicRegistry registry) {
        this.consumerId = consumerId;
        this.topicName = topicName;
        this.registry = registry;
    }

    @Override
    public String getConsumerId() {
        return consumerId;
    }

    @Override
    public String getTopicName() {
        return topicName;
    }

    @Override
    public long getCurrentOffset() {
        return registry.getOrThrow(topicName).getOffset(consumerId);
    }

    @Override
    public void resetOffset(long offset) {
        registry.getOrThrow(topicName).resetOffset(consumerId, offset);
    }

    @Override
    public List<Message> poll(int maxMessages) {
        return registry.getOrThrow(topicName).poll(consumerId, maxMessages);
    }

    @Override
    public ConsumerLag getLag() {
        TopicPartition tp = registry.getOrThrow(topicName);
        long last = tp.getOffset(consumerId) - 1;
        long hwm = tp.getHighWatermark();
        return new ConsumerLag(consumerId, topicName, last, hwm);
    }

    @Override
    public void close() {
        registry.getOrThrow(topicName).unregisterConsumer(consumerId);
    }
}

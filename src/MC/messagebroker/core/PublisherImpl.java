package MC.messagebroker.core;

import MC.messagebroker.exception.BrokerException;

import java.util.concurrent.atomic.AtomicBoolean;

final class PublisherImpl implements Publisher {

    private final String topicName;
    private final TopicRegistry registry;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    PublisherImpl(String topicName, TopicRegistry registry) {
        this.topicName = topicName;
        this.registry = registry;
    }

    @Override
    public String getTopicName() {
        return topicName;
    }

    @Override
    public long publish(String payload) {
        if (closed.get()) {
            throw new BrokerException("Publisher is closed");
        }
        if (payload == null) {
            throw new BrokerException("Payload cannot be null");
        }
        return registry.getOrThrow(topicName).publish(payload);
    }

    @Override
    public void close() {
        closed.set(true);
    }
}

package MC.PubSubLibrary.impl;

import MC.PubSubLibrary.api.Consumer;
import MC.PubSubLibrary.api.ConsumerOffsetInfo;
import MC.PubSubLibrary.exception.PubSubException;
import MC.PubSubLibrary.exception.InvalidOffsetException;
import MC.PubSubLibrary.model.Message;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default in-memory consumer. Each consumer has its own offset; thread-safe.
 */
final class DefaultConsumer implements Consumer {
    private final InMemoryTopic topic;
    private final String consumerId;
    private final AtomicLong offset;

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    DefaultConsumer(InMemoryTopic topic) {
        this.topic = topic;
        this.consumerId = "consumer-" + ID_GENERATOR.incrementAndGet();
        this.offset = new AtomicLong(topic.getStartOffset());
    }

    @Override
    public Optional<Message> consume() {
        try {
            long start = topic.getStartOffset();
            long current = offset.get();
            if (current < start) {
                offset.set(start);
                current = start;
            }
            long end = topic.getEndOffset();
            if (current >= end) {
                return Optional.empty();
            }
            Message msg = topic.getMessageAt(current);
            if (msg == null) {
                return Optional.empty();
            }
            offset.incrementAndGet();
            return Optional.of(msg);
        } catch (Exception e) {
            throw new PubSubException("Failed to consume message", e);
        }
    }

    @Override
    public Optional<Message> consumeBlocking(long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            Optional<Message> msg = consume();
            if (msg.isPresent()) return msg;
            Thread.sleep(50);
        }
        return Optional.empty();
    }

    @Override
    public void resetOffset(long newOffset) {
        long start = topic.getStartOffset();
        if (newOffset < start) {
            throw new InvalidOffsetException(
                    "Cannot reset offset to " + newOffset + ": before startOffset " + start + " (retention)");
        }
        offset.set(newOffset);
    }

    @Override
    public long getCurrentOffset() {
        return offset.get();
    }

    @Override
    public long getLag() {
        long end = topic.getEndOffset();
        long current = offset.get();
        return Math.max(0, end - current);
    }

    @Override
    public ConsumerOffsetInfo getOffsetInfo() {
        long current = offset.get();
        long lastReadOffset = current > 0 ? current - 1 : -1;
        long lag = getLag();
        return new ConsumerOffsetInfo(consumerId, topic.getName(), lastReadOffset, current, lag);
    }

    @Override
    public String getConsumerId() {
        return consumerId;
    }
}

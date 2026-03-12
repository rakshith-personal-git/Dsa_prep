package MC.PubSubLibrary.api;

import MC.PubSubLibrary.model.Message;

import java.util.Optional;

/**
 * Contract for consuming messages from a single topic. Each consumer manages its own offset.
 */
public interface Consumer {

    /**
     * Consumes the next message. Returns empty if no message is available (caught up or offset past retention).
     */
    Optional<Message> consume();

    /**
     * Blocks until a message is available or the timeout expires. Polls periodically.
     */
    Optional<Message> consumeBlocking(long timeoutMs) throws InterruptedException;

    /**
     * Resets this consumer's offset to allow replay (Bonus-1).
     *
     * @throws MC.PubSubLibrary.exception.InvalidOffsetException if offset is before current start (retention)
     */
    void resetOffset(long newOffset);

    /**
     * Current offset (next offset to be read).
     */
    long getCurrentOffset();

    /**
     * Lag: number of messages not yet consumed (Bonus-2).
     */
    long getLag();

    /**
     * Consumer visibility: last offset read, current offset, lag (Bonus-2).
     */
    ConsumerOffsetInfo getOffsetInfo();

    /** Unique identifier for this consumer. */
    String getConsumerId();
}

package MC.PubSubLibrary.api;

/**
 * Consumer-level visibility: last offset read, current offset, and lag (Bonus-2).
 */
public final class ConsumerOffsetInfo {
    private final String consumerId;
    private final String topicName;
    private final long lastOffsetRead;
    private final long currentOffset;
    private final long lag;

    public ConsumerOffsetInfo(String consumerId, String topicName, long lastOffsetRead, long currentOffset, long lag) {
        this.consumerId = consumerId;
        this.topicName = topicName;
        this.lastOffsetRead = lastOffsetRead;
        this.currentOffset = currentOffset;
        this.lag = lag;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public String getTopicName() {
        return topicName;
    }

    public long getLastOffsetRead() {
        return lastOffsetRead;
    }

    public long getCurrentOffset() {
        return currentOffset;
    }

    public long getLag() {
        return lag;
    }

    @Override
    public String toString() {
        return String.format("ConsumerOffsetInfo{consumerId='%s', topic='%s', lastOffsetRead=%d, currentOffset=%d, lag=%d}",
                consumerId, topicName, lastOffsetRead, currentOffset, lag);
    }
}

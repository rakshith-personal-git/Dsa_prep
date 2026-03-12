package MC.messagebroker.model;

public final class ConsumerLag {

    private final String consumerId;
    private final String topicName;
    private final long lastOffset;
    private final long highWatermark;
    private final long lag;

    public ConsumerLag(String consumerId, String topicName, long lastOffset, long highWatermark) {
        this.consumerId = consumerId;
        this.topicName = topicName;
        this.lastOffset = lastOffset;
        this.highWatermark = highWatermark;
        this.lag = Math.max(0, highWatermark - lastOffset);
    }

    public String getConsumerId() {
        return consumerId;
    }

    public String getTopicName() {
        return topicName;
    }

    public long getLastOffset() {
        return lastOffset;
    }

    public long getHighWatermark() {
        return highWatermark;
    }

    public long getLag() {
        return lag;
    }

    @Override
    public String toString() {
        return String.format(
                "ConsumerLag{consumer='%s', topic='%s', lastOffset=%d, highWatermark=%d, lag=%d}",
                consumerId, topicName, lastOffset, highWatermark, lag);
    }
}

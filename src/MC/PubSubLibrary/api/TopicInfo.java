package MC.PubSubLibrary.api;

/**
 * Topic-level visibility: start offset, end offset, and message count (Bonus-2).
 */
public final class TopicInfo {
    private final String topicName;
    private final long startOffset;
    private final long endOffset;
    private final long messageCount;

    public TopicInfo(String topicName, long startOffset, long endOffset, long messageCount) {
        this.topicName = topicName;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.messageCount = messageCount;
    }

    public String getTopicName() {
        return topicName;
    }

    public long getStartOffset() {
        return startOffset;
    }

    public long getEndOffset() {
        return endOffset;
    }

    public long getMessageCount() {
        return messageCount;
    }

    @Override
    public String toString() {
        return String.format("TopicInfo{topic='%s', startOffset=%d, endOffset=%d, messageCount=%d}",
                topicName, startOffset, endOffset, messageCount);
    }
}

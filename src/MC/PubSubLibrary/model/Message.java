package MC.PubSubLibrary.model;

/**
 * Immutable value object representing a message in the pub-sub system.
 */
public final class Message {
    private final String content;
    private final long timestampMs;
    private final long offset;

    public Message(String content, long timestampMs, long offset) {
        this.content = content;
        this.timestampMs = timestampMs;
        this.offset = offset;
    }

    public String getContent() {
        return content;
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public long getOffset() {
        return offset;
    }
}

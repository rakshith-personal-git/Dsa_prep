package MC.messagebroker.model;

import java.time.Instant;

public final class Message {

    private final long offset;
    private final String payload;
    private final Instant createdAt;

    public Message(long offset, String payload) {
        this.offset = offset;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public long getOffset() {
        return offset;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("Message{offset=%d, payload='%s', createdAt=%s}", offset, payload, createdAt);
    }
}

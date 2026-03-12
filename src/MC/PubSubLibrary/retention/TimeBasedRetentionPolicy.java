package MC.PubSubLibrary.retention;

import MC.PubSubLibrary.model.Message;

/**
 * Evicts messages older than a fixed retention period. Topic-level property.
 */
public final class TimeBasedRetentionPolicy implements RetentionPolicy {
    private final long retentionPeriodMs;

    public TimeBasedRetentionPolicy(long retentionPeriodMs) {
        if (retentionPeriodMs <= 0) {
            throw new IllegalArgumentException("Retention period must be positive");
        }
        this.retentionPeriodMs = retentionPeriodMs;
    }

    @Override
    public boolean shouldEvict(Message message, long nowMs) {
        return (nowMs - message.getTimestampMs()) >= retentionPeriodMs;
    }
}

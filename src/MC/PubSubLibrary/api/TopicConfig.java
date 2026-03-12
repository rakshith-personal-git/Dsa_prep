package MC.PubSubLibrary.api;

import java.util.Objects;

/**
 * Configuration for creating a topic. Extensible for future options (e.g. partitions, replication).
 */
public final class TopicConfig {
    private final String name;
    private final long retentionPeriodMs;

    private TopicConfig(String name, long retentionPeriodMs) {
        this.name = name;
        this.retentionPeriodMs = retentionPeriodMs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static TopicConfig of(String name, long retentionPeriodMs) {
        return builder().name(name).retentionPeriodMs(retentionPeriodMs).build();
    }

    public String getName() {
        return name;
    }

    public long getRetentionPeriodMs() {
        return retentionPeriodMs;
    }

    public static final class Builder {
        private String name;
        private long retentionPeriodMs;

        public Builder name(String name) {
            this.name = Objects.requireNonNull(name, "name");
            return this;
        }

        public Builder retentionPeriodMs(long retentionPeriodMs) {
            this.retentionPeriodMs = retentionPeriodMs;
            return this;
        }

        public TopicConfig build() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Topic name cannot be null or blank");
            }
            if (retentionPeriodMs <= 0) {
                throw new IllegalArgumentException("Retention period must be positive");
            }
            return new TopicConfig(name.trim(), retentionPeriodMs);
        }
    }
}

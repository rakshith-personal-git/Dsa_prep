package MC.PubSubLibrary.impl;

import MC.PubSubLibrary.model.Message;
import MC.PubSubLibrary.retention.RetentionPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In-memory topic: ordered log with retention. Thread-safe for parallel publish and consume.
 */
final class InMemoryTopic {
    private final String name;
    private final RetentionPolicy retentionPolicy;
    private final List<Message> messages;
    private long startOffset;
    private final Lock lock;

    InMemoryTopic(String name, RetentionPolicy retentionPolicy) {
        this.name = name;
        this.retentionPolicy = retentionPolicy;
        this.messages = new ArrayList<>();
        this.startOffset = 0L;
        this.lock = new ReentrantLock();
    }

    String getName() {
        return name;
    }

    long publish(String content) {
        lock.lock();
        try {
            evictExpired();
            long offset = startOffset + messages.size();
            Message msg = new Message(content, System.currentTimeMillis(), offset);
            messages.add(msg);
            return offset;
        } finally {
            lock.unlock();
        }
    }

    private void evictExpired() {
        long now = System.currentTimeMillis();
        while (!messages.isEmpty() && retentionPolicy.shouldEvict(messages.get(0), now)) {
            messages.remove(0);
            startOffset++;
        }
    }

    Message getMessageAt(long offset) {
        lock.lock();
        try {
            evictExpired();
            if (offset < startOffset) return null;
            int index = (int) (offset - startOffset);
            if (index < 0 || index >= messages.size()) return null;
            return messages.get(index);
        } finally {
            lock.unlock();
        }
    }

    long getStartOffset() {
        lock.lock();
        try {
            evictExpired();
            return startOffset;
        } finally {
            lock.unlock();
        }
    }

    long getEndOffset() {
        lock.lock();
        try {
            evictExpired();
            return startOffset + messages.size();
        } finally {
            lock.unlock();
        }
    }
}

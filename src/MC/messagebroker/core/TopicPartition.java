package MC.messagebroker.core;

import MC.messagebroker.exception.InvalidOffsetException;
import MC.messagebroker.model.Message;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

final class TopicPartition {

    private static final Logger LOG = Logger.getLogger(TopicPartition.class.getName());

    private final String name;
    private final Duration retentionPeriod;
    private final ConcurrentSkipListMap<Long, Message> store = new ConcurrentSkipListMap<>();
    private final AtomicLong nextOffset = new AtomicLong(0);
    private final ConcurrentHashMap<String, AtomicLong> consumerOffsets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MessageHandler> handlers = new ConcurrentHashMap<>();
    private final ExecutorService dispatcher;
    private final ScheduledExecutorService cleaner;
    private volatile boolean closed = false;

    TopicPartition(String name, Duration retentionPeriod) {
        this.name = name;
        this.retentionPeriod = retentionPeriod;

        this.dispatcher = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "broker-dispatcher-" + name);
            t.setDaemon(true);
            return t;
        });
        this.cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "broker-cleaner-" + name);
            t.setDaemon(true);
            return t;
        });

        long periodSecs = Math.max(1, retentionPeriod.toSeconds());
        cleaner.scheduleAtFixedRate(this::runRetentionCleanup, periodSecs, periodSecs, TimeUnit.SECONDS);
    }

    long publish(String payload) {
        ensureOpen();
        long offset = nextOffset.getAndIncrement();
        Message msg = new Message(offset, payload);
        store.put(offset, msg);
        dispatcher.submit(() -> dispatchToConsumers(msg));
        return offset;
    }

    void registerConsumer(String consumerId, MessageHandler handler) {
        ensureOpen();
        consumerOffsets.putIfAbsent(consumerId, new AtomicLong(0));
        handlers.put(consumerId, handler);
    }

    void unregisterConsumer(String consumerId) {
        consumerOffsets.remove(consumerId);
        handlers.remove(consumerId);
    }

    void resetOffset(String consumerId, long offset) {
        long hi = nextOffset.get();
        if (offset < 0 || offset > hi) {
            throw new InvalidOffsetException(offset, hi);
        }
        AtomicLong cur = consumerOffsets.get(consumerId);
        if (cur != null) {
            cur.set(offset);
        }
    }

    long getOffset(String consumerId) {
        AtomicLong cur = consumerOffsets.get(consumerId);
        return cur == null ? 0L : cur.get();
    }

    List<Message> poll(String consumerId, int maxMessages) {
        AtomicLong cur = consumerOffsets.computeIfAbsent(consumerId, k -> new AtomicLong(0));
        long from = cur.get();
        List<Message> result = new ArrayList<>();
        NavigableMap<Long, Message> tail = store.tailMap(from, true);
        for (Map.Entry<Long, Message> e : tail.entrySet()) {
            if (result.size() >= maxMessages) {
                break;
            }
            result.add(e.getValue());
        }
        if (!result.isEmpty()) {
            cur.set(result.get(result.size() - 1).getOffset() + 1);
        }
        return Collections.unmodifiableList(result);
    }

    long getHighWatermark() {
        return nextOffset.get() - 1;
    }

    private void runRetentionCleanup() {
        Instant cutoff = Instant.now().minus(retentionPeriod);
        Iterator<Map.Entry<Long, Message>> it = store.entrySet().iterator();
        int removed = 0;
        while (it.hasNext()) {
            Message m = it.next().getValue();
            if (m.getCreatedAt().isBefore(cutoff)) {
                it.remove();
                removed++;
            } else {
                break;
            }
        }
        if (removed > 0) {
            LOG.info(String.format("[%s] Retention cleanup removed %d message(s)", name, removed));
        }
    }

    private void dispatchToConsumers(Message msg) {
        for (Map.Entry<String, MessageHandler> entry : handlers.entrySet()) {
            String consumerId = entry.getKey();
            MessageHandler handler = entry.getValue();
            AtomicLong offset = consumerOffsets.get(consumerId);
            if (offset == null) {
                continue;
            }
            if (offset.get() == msg.getOffset()) {
                try {
                    handler.onMessage(msg);
                    offset.incrementAndGet();
                } catch (Exception ex) {
                    LOG.log(Level.WARNING,
                            String.format("[%s] Handler for consumer '%s' threw exception at offset %d",
                                    name, consumerId, msg.getOffset()), ex);
                }
            }
        }
    }

    void close() {
        closed = true;
        dispatcher.shutdown();
        cleaner.shutdown();
        try {
            if (!dispatcher.awaitTermination(5, TimeUnit.SECONDS)) {
                dispatcher.shutdownNow();
            }
            if (!cleaner.awaitTermination(2, TimeUnit.SECONDS)) {
                cleaner.shutdownNow();
            }
        } catch (InterruptedException e) {
            dispatcher.shutdownNow();
            cleaner.shutdownNow();
            Thread.currentThread().interrupt();
        }
        store.clear();
        consumerOffsets.clear();
        handlers.clear();
    }

    private void ensureOpen() {
        if (closed) {
            throw new MC.messagebroker.exception.BrokerException("Topic '" + name + "' is closed");
        }
    }
}

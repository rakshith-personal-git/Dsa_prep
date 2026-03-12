package MC.messagebroker.core;

import MC.messagebroker.exception.TopicAlreadyExistsException;
import MC.messagebroker.exception.TopicNotFoundException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

final class TopicRegistry {

    private final ConcurrentHashMap<String, TopicPartition> topics = new ConcurrentHashMap<>();

    void create(String name, Duration retention) {
        if (topics.putIfAbsent(name, new TopicPartition(name, retention)) != null) {
            throw new TopicAlreadyExistsException(name);
        }
    }

    void delete(String name) {
        TopicPartition tp = topics.remove(name);
        if (tp == null) {
            throw new TopicNotFoundException(name);
        }
        tp.close();
    }

    TopicPartition getOrThrow(String name) {
        TopicPartition tp = topics.get(name);
        if (tp == null) {
            throw new TopicNotFoundException(name);
        }
        return tp;
    }

    List<String> listTopics() {
        return new ArrayList<>(topics.keySet());
    }

    void closeAll() {
        topics.values().forEach(TopicPartition::close);
        topics.clear();
    }
}

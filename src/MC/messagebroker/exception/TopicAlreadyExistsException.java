package MC.messagebroker.exception;

public class TopicAlreadyExistsException extends BrokerException {
    public TopicAlreadyExistsException(String topicName) {
        super("Topic already exists: " + topicName);
    }
}

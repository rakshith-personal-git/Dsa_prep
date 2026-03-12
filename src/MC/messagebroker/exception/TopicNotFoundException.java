package MC.messagebroker.exception;

public class TopicNotFoundException extends BrokerException {
    public TopicNotFoundException(String topicName) {
        super("Topic not found: " + topicName);
    }
}

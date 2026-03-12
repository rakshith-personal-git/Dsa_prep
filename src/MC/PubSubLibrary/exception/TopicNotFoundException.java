package MC.PubSubLibrary.exception;

public class TopicNotFoundException extends PubSubException {
    public TopicNotFoundException(String topicName) {
        super("Topic not found: " + topicName);
    }
}

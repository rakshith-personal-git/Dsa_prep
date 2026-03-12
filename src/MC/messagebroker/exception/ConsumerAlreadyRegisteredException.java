package MC.messagebroker.exception;

public class ConsumerAlreadyRegisteredException extends BrokerException {
    public ConsumerAlreadyRegisteredException(String consumerId, String topicName) {
        super(String.format("Consumer '%s' is already registered on topic '%s'", consumerId, topicName));
    }
}

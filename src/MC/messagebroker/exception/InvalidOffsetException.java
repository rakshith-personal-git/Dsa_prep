package MC.messagebroker.exception;

public class InvalidOffsetException extends BrokerException {
    public InvalidOffsetException(long offset, long max) {
        super(String.format("Offset %d is out of range. Valid range: [0, %d]", offset, max));
    }
}

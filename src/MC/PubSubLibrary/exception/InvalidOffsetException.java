package MC.PubSubLibrary.exception;

/**
 * Thrown when an offset is invalid (e.g. reset to offset before retention window).
 */
public class InvalidOffsetException extends PubSubException {
    public InvalidOffsetException(String message) {
        super(message);
    }
}

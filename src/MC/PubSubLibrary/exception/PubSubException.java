package MC.PubSubLibrary.exception;

/**
 * Base exception for the Pub-Sub library. Enables graceful handling and consistent error reporting.
 */
public class PubSubException extends RuntimeException {
    public PubSubException(String message) {
        super(message);
    }

    public PubSubException(String message, Throwable cause) {
        super(message, cause);
    }
}

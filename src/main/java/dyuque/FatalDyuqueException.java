package dyuque;

/**
 * Represents an application-specific fatal exception for Dyuque operations.
 */
public class FatalDyuqueException extends DyuqueException {
    /**
     * Creates an exception with the specified error message.
     *
     * @param message Error message.
     */
    public FatalDyuqueException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a custom message and the specified cause.
     *
     * @param message Error message to display.
     * @param cause Underlying cause of the error.
     */
    public FatalDyuqueException(String message, Throwable cause) {
        super(message, cause);
    }
}

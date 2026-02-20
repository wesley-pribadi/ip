package dyuque;

/**
 * Represents an application-specific exception for Dyuque operations.
 */
public class DyuqueException extends Exception {
    /**
     * Creates an exception with the specified error message.
     *
     * @param message Error message.
     */
    public DyuqueException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a custom message and the specified cause.
     *
     * @param customErrorMessage Custom error message to display.
     * @param cause Underlying cause of the error.
     */
    public DyuqueException(String customErrorMessage, Throwable cause) {
        super(customErrorMessage
                + "\n\n"
                + "[ERROR HELP]\n"
                + cause.getMessage(),
                cause);
    }
}

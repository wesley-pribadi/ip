public class DyuqueException extends Exception {
    public DyuqueException(String message) {
        super(message);
    }

    public DyuqueException(String customErrorMessage, Throwable cause) {
        super(customErrorMessage
                + "\n"
                + cause.toString(),
                cause);
    }
}

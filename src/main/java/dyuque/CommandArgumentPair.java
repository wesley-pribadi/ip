package dyuque;

/**
 * Stores a parsed command and its arguments.
 */
public record CommandArgumentPair(Dyuque.Command command, String[] argument) {
}

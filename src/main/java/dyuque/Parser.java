package dyuque;

/**
 * Parses user input strings into commands and structured arguments.
 */
public class Parser {
    // Class inspired from multiple LLMs including ChatGPT, Claude, and Google AI

    /**
     * Returns a command-argument pair parsed from the specified user input.
     *
     * @param input Raw user input line.
     * @return Parsed command and associated arguments.
     * @throws DyuqueException If the input is blank, the command is unknown, or the command usage is invalid.
     */
    public CommandArgumentPair parseInput(String input) throws DyuqueException {
        String sanitizedInput = sanitizeNewlinesAndBlanks(input);
        validateNotBlank(sanitizedInput);

        // Splits "deadline return book /by Sunday" into ["deadline", "return book /by Sunday"]
        String[] commandAndArguments = input.trim().split(" ", 2);
        assert commandAndArguments.length >= 1 : "Split should always produce at least one element";

        Dyuque.Command command = extractCommand(commandAndArguments);
        String arguments = extractArguments(commandAndArguments);
        return parseCommandArguments(command, arguments);
    }

    /**
     * Removes all Unicode line break sequences from the input string,
     * then strips leading and trailing whitespace.
     * <p>
     * This is intended for sanitizing single‑line command input where
     * newlines are considered unexpected noise (e.g., from copy‑paste).
     * Line breaks are simply deleted, not replaced with spaces.
     *
     * @param input the string to sanitize (can be {@code null})
     * @return a string with all line breaks removed, and leading/trailing
     *         whitespace trimmed; never {@code null} (empty string if input is {@code null})
     */
    public static String sanitizeNewlinesAndBlanks(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replaceAll("\\R+", "")
                .strip();
    }

    private static void validateNotBlank(String input) throws DyuqueException {
        if (input.isBlank()) {
            throw new DyuqueException("Please enter a command");
        }
    }

    private Dyuque.Command extractCommand(String[] commandAndArguments) throws DyuqueException {
        String commandStr = commandAndArguments[0];
        return Dyuque.Command
                .getCommand(commandStr)
                .orElseThrow(() -> new DyuqueException("Unknown command: " + commandStr));
    }

    private String extractArguments(String[] commandAndArguments) {
        return (commandAndArguments.length == 2)
                ? commandAndArguments[1].strip()
                : "";
    }

    private CommandArgumentPair parseCommandArguments(Dyuque.Command command, String arguments) throws DyuqueException {
        return switch (command) {
            case HELP, LIST, UNDO, EXIT -> new CommandArgumentPair(command, new String[0]);
            case DELETE, MARK, UNMARK, FIND, TODO -> parseSingleArgCommand(command, arguments);
            case EVENT -> parseEventCommand(command, arguments);
            case DEADLINE -> parseDeadlineCommand(command, arguments);
        };
    }

    private CommandArgumentPair parseSingleArgCommand(Dyuque.Command command, String arguments) throws DyuqueException {
        if (arguments.isBlank()) {
            throw new DyuqueException(command.getUsageHelpStr());
        }
        return new CommandArgumentPair(command, new String[]{ arguments });
    }

    private CommandArgumentPair parseEventCommand(Dyuque.Command command, String arguments) throws DyuqueException {
        String trimmed = arguments.strip();
        int fromIndex = trimmed.indexOf("/from");
        int toIndex = trimmed.indexOf("/to");

        if (fromIndex == -1 || toIndex == -1 || toIndex <= fromIndex) {
            throw new DyuqueException(command.getUsageHelpStr());
        }

        String desc = trimmed.substring(0, fromIndex).strip();
        String from = trimmed.substring(fromIndex + 5, toIndex).strip(); // "/from".length() = 5
        String to = trimmed.substring(toIndex + 3).strip(); // "/to".length() = 3

        if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new DyuqueException(command.getUsageHelpStr());
        }

        return new CommandArgumentPair(command, new String[]{desc, from, to});
    }

    private CommandArgumentPair parseDeadlineCommand(Dyuque.Command command, String arguments) throws DyuqueException {
        String trimmed = arguments.strip();
        int byIndex = trimmed.indexOf("/by");
        if (byIndex == -1) {
            throw new DyuqueException(command.getUsageHelpStr());
        }

        String description = trimmed.substring(0, byIndex).strip();
        String date = trimmed.substring(byIndex + 3).strip(); // "/by".length() = 3

        if (description.isEmpty() || date.isEmpty()) {
            throw new DyuqueException("Description or date cannot be blank");
        }

        return new CommandArgumentPair(command, new String[]{description, date});
    }
}

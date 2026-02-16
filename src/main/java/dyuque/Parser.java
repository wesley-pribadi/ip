package dyuque;

/**
 * Parses user input strings into commands and structured arguments.
 */
public class Parser {
    /**
     * Returns a command-argument pair parsed from the specified user input.
     *
     * @param input Raw user input line.
     * @return Parsed command and associated arguments.
     * @throws DyuqueException If the input is blank, the command is unknown, or the command usage is invalid.
     */
    public CommandArgumentPair parseCommand(String input) throws DyuqueException {
        // Method inspired from multiple LLMs including ChatGPT, Claude, and Google AI
        validateNotBlank(input);

        // Splits "deadline return book /by Sunday" into ["deadline", "return book /by Sunday"]
        String[] commandAndArguments = input.trim().split(" ", 2);
        assert commandAndArguments.length >= 1 : "Split should always produce at least one element";

        Dyuque.Command command = extractCommand(commandAndArguments);
        String arguments = extractArguments(commandAndArguments);
        return parseCommandArguments(command, arguments);
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
            case EXIT, LIST -> new CommandArgumentPair(command, new String[0]);
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

    private CommandArgumentPair parseEventCommand (Dyuque.Command command, String arguments) throws DyuqueException {
        int fromPos = arguments.indexOf(" /from ");
        int toPos = arguments.indexOf(" /to ");
        if (fromPos < 0 || toPos < 0 || toPos < fromPos) {
            throw new DyuqueException(command.getUsageHelpStr());
        }

        String desc = arguments.substring(0, fromPos).trim();
        String from = arguments.substring(fromPos + " /from ".length(), toPos).trim();
        String to = arguments.substring(toPos + " /to ".length()).trim();
        if (desc.isBlank() || from.isBlank() || to.isBlank()) {
            throw new DyuqueException(command.getUsageHelpStr());
        }

        return new CommandArgumentPair(command, new String[]{ desc, from, to });
    }

    private CommandArgumentPair parseDeadlineCommand(Dyuque.Command command, String arguments) throws DyuqueException {
        String[] parts = arguments.split(" /by ", 2);
        if (parts.length != 2) {
            throw new DyuqueException(command.getUsageHelpStr());
        }

        String description = parts[0].strip();
        String date = parts[1].strip();
        if (description.isEmpty() || date.isEmpty()) {
            throw new DyuqueException("Description and date cannot be blank");
        }

        return new CommandArgumentPair(command, new String[]{ description, date });
    }
}

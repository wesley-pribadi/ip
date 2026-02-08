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

        if (input.isBlank()) {
            throw new DyuqueException("Please enter a command");
        }

        String[] commandAndArguments = input.trim().split(" ", 2); /* Splits "deadline return book /by Sunday"
                                                               into ["deadline", "return book /by Sunday"] */
        String commandStr = commandAndArguments[0];
        Dyuque.Command command = Dyuque.Command
                .get(commandStr)
                .orElseThrow(() -> new DyuqueException("Unknown command: " + commandStr));
        String arguments = (commandAndArguments.length == 2)
                ? commandAndArguments[1].trim()
                : "";

        return switch (command) {
            case Exit, List -> new CommandArgumentPair(command, new String[0]);

            case Mark, Unmark, Delete -> {
                if (arguments.isBlank()) {
                    throw new DyuqueException("Usage: " + commandStr + " <index>");
                }
                yield new CommandArgumentPair(command, new String[]{ arguments });
            }

            case Todo -> {
                if (arguments.isBlank()) {
                    throw new DyuqueException("Usage: todo <description>");
                }
                yield new CommandArgumentPair(command, new String[]{ arguments });
            }
            case Event -> {
                // Avoid regex split, this is less fragile
                int fromPos = arguments.indexOf(" /from ");
                int toPos = arguments.indexOf(" /to ");
                if (fromPos < 0 || toPos < 0 || toPos < fromPos) {
                    throw new DyuqueException("Usage: event <description> /from <date> /to <date>");
                }
                String desc = arguments.substring(0, fromPos).trim();
                String from = arguments.substring(fromPos + " /from ".length(), toPos).trim();
                String to = arguments.substring(toPos + " /to ".length()).trim();
                if (desc.isBlank() || from.isBlank() || to.isBlank()) {
                    throw new DyuqueException("Usage: event <description> /from <date> /to <date>");
                }

                yield new CommandArgumentPair(command, new String[]{ desc, from, to });
            }
            case Deadline -> {
                String[] parts = arguments.split(" /by ", 2);          /* Splits "return book /by Sunday"
                                                                       into ["return book", "Sunday"] */
                if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                    throw new DyuqueException("Usage: deadline <description> /by <date>");
                }
                yield new CommandArgumentPair(command, new String[]{ parts[0].trim(), parts[1].trim() });
            }
        };
    }
}

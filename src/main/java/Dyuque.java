import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class Dyuque {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String SAVE_PATH = "./data/dyuque.txt";

    private final Scanner scanner;
    private final Storage storage;
    private final TaskList taskList;
    private Boolean shouldContinueExecution;

    protected enum Command {
        LIST("list", "ls"),
        DELETE("delete", "remove"),
        TODO("todo"),
        DEADLINE("deadline"),
        EVENT("event"),
        MARK("mark"),
        UNMARK("unmark"),
        EXIT_CODE("bye", "exit");

        private final Set<String> keywords;

        Command(String... keywords) {
            this.keywords = Set.of(keywords);
        }

        protected static Optional<Command> get(String keyword) {
            for (Command command : values()) {
                if (command.keywords.contains(keyword)) {
                    return Optional.of((command));
                }
            }
            return Optional.empty();
        }
    }

    public Dyuque() throws DyuqueException {
        this.scanner = new Scanner(System.in);
        this.storage = new Storage(SAVE_PATH);
        this.taskList = new TaskList(storage.load(), storage);
        // if file/folder missing: Storage creates them and loads empty list
        // if reading fails: throw and exit (no overwrite risk)
        this.shouldContinueExecution = true;
    }

    public static void main(String[] args) {
        try {
            new Dyuque().newChat();
        } catch (DyuqueException e) {
            System.out.print(ANSI_RED);
            System.out.println("[FATAL] Failed to load saved tasks due to error:");
            System.out.println("[FATAL] " + e.getMessage());
            System.out.print(ANSI_RESET);
        }
    }

    public void newChat() {
        printGreet();

        do {
            System.out.print("> ");
            try {
                shouldContinueExecution = executeCommand(scanInput());
            } catch (DyuqueException e) {
                System.out.print(ANSI_RED);
                System.out.println("[ERROR] " + e.getMessage());
                System.out.print(ANSI_RESET);
            }
        } while (shouldContinueExecution);
    }

    public void printLine() {
        System.out.print("__________________________________________________");
        System.out.println();
    }

    public void printGreet() {
        System.out.println("Hello! I'm Dyuque");
        System.out.println("What can I do for you?");
        printLine();
        System.out.println("Commands:");
        System.out.println("  \"list\" --- list stored tasks");
        System.out.println("  \"delete\" - delete a task");
        System.out.println("  \"mark\" --- mark a task as done");
        System.out.println("             mark <task number>");
        System.out.println("  \"unmark\" - unmark a task as done");
        System.out.println("             unmark <task number>");
        System.out.println("  \"bye\" ---- quit Dyuque");
        System.out.println("Task types:");
        System.out.println("  todo ----- \"todo <description>\"");
        System.out.println("  deadline - \"deadline <description> /by <YYYY-MM-DD>\"");
        System.out.println("  event ---- \"event <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD>\"");
        printLine();
        System.out.println("Enter a new task or command:");
    }

    public void printExit() {
        System.out.println("Goodbye, hope to see you again soon!");
    }

    public String scanInput() {
        return scanner.nextLine();
    }

    public CommandArgumentPair parseCommand(String input) throws DyuqueException {
        // Solution below inspired from multiple LLMs including ChatGPT, Claude, and Google AI

        if (input.isBlank()) {
            throw new DyuqueException("Please enter a command");
        }

        String[] commandAndArguments = input.trim().split(" ", 2); /* Splits "deadline return book /by Sunday"
                                                               into ["deadline", "return book /by Sunday"] */
        String commandStr = commandAndArguments[0];
        Command command = Command
                .get(commandStr)
                .orElseThrow(() -> new DyuqueException("Unknown command: " + commandStr));
        String arguments = (commandAndArguments.length == 2)
                ? commandAndArguments[1].trim()
                : "";
        
        return switch (command) {
            case EXIT_CODE, LIST -> new CommandArgumentPair(command, new String[0]);

            case MARK, UNMARK, DELETE -> {
                if (arguments.isBlank()) {
                    throw new DyuqueException("Usage: " + commandStr + " <index>");
                }
                yield new CommandArgumentPair(command, new String[]{ arguments });
            }

            case TODO -> {
                if (arguments.isBlank()) {
                    throw new DyuqueException("Usage: todo <description>");
                }
                yield new CommandArgumentPair(command, new String[]{ arguments });
            }
            case EVENT -> {
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
            case DEADLINE -> {
                String[] parts = arguments.split(" /by ", 2);          /* Splits "return book /by Sunday"
                                                                       into ["return book", "Sunday"] */
                if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                    throw new DyuqueException("Usage: deadline <description> /by <date>");
                }
                yield new CommandArgumentPair(command, new String[]{ parts[0].trim(), parts[1].trim() });
            }
        };
    }

    public Boolean executeCommand(String input) throws DyuqueException {
        printLine();
        CommandArgumentPair commandArgumentPair = parseCommand(input);
        Command command = commandArgumentPair.command();
        String[] arguments = commandArgumentPair.argument();
        /*  Elements of arguments:
                delete/mark/unmark: index
                to-do:              description
                deadline:           description, dueDate
                event:              description, fromDate, toDate
         */
        try {
            switch (command) {
                case EXIT_CODE -> {
                    printExit();
                    return false;
                }
                case LIST -> taskList.list();

                case TODO -> taskList.add(new Todo(arguments[0]));
                case DEADLINE -> taskList.add(new Deadline(arguments[0], arguments[1]));
                case EVENT -> taskList.add(new Event(arguments[0], arguments[1], arguments[2]));

                case DELETE -> taskList.delete(Integer.parseInt(arguments[0]) - 1);
                case MARK -> taskList.setMarkedState(TaskList.markedState.MARKED, Integer.parseInt(arguments[0]) - 1);
                case UNMARK -> taskList.setMarkedState(TaskList.markedState.UNMARKED, Integer.parseInt(arguments[0]) - 1);
            }
        } catch (NumberFormatException nfe) {
            throw new DyuqueException("Expected integer but received something else...", nfe);
        }
        return true;
    }
}

import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class Dyuque {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String SAVE_PATH = "./data/dyuque.txt";

    private final Scanner scanner;
    private final Storage storage;
    private final Parser parser;
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
        this.parser = new Parser();
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

    public Boolean executeCommand(String input) throws DyuqueException {
        printLine();
        CommandArgumentPair commandArgumentPair = parser.parseCommand(input);
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

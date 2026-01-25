import java.util.Objects;
import java.util.Scanner;

public class Dyuque {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String EXIT_CODE = "bye";

    private final Scanner scanner;
    private final TaskList taskList;
    private String input;

    public static void main(String[] args) {
        new Dyuque().newChat();
    }

    public Dyuque(){
        this.scanner = new Scanner(System.in);
        this.taskList = new TaskList();
        this.input = "";
    }

    public void newChat() {
        printGreet();

        do {
            System.out.print("> ");
            try {
                input = scanInput();
                executeCommand(input);
            } catch (DyuqueException e) {
                System.out.print(ANSI_RED);
                System.out.println("[ERROR] " + e.getMessage());
                System.out.print(ANSI_RESET);
            }
        } while (!Objects.equals(input, EXIT_CODE));
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
        System.out.println("  \"mark\" --- mark a task as done");
        System.out.println("  \"unmark\" - unmark a task as done");
        System.out.println("  \"bye\" ---- quit Dyuque");
        System.out.println("Task types:");
        System.out.println("  todo ----- \"todo <description>\"");
        System.out.println("  deadline - \"deadline <description> \\by <date>\"");
        System.out.println("  event ---- \"event <description> \\from <date> \\to <date>\"");
        printLine();
        System.out.println("Enter a new task or command:");
    }

    public void printExit() {
        System.out.println("Goodbye, hope to see you again soon!");
    }

    public String scanInput() {
        return scanner.nextLine();
    }

    public InputBundle parseCommand(String input) throws DyuqueException {
        // Solution below inspired from multiple LLMs including ChatGPT, Claude, and Google AI

        if (input.isBlank()) throw new DyuqueException("Please enter a command");

        String[] commandAndArguments = input.trim().split(" ", 2); /* Splits "deadline return book /by Sunday"
                                                               into ["deadline", "return book /by Sunday"] */
        String command = commandAndArguments[0];
        String arguments = (commandAndArguments.length == 2)
                ? commandAndArguments[1].trim()
                : "";
        
        return switch (command) {
            case EXIT_CODE, "list" -> new InputBundle(command, new String[0]);
            case "todo" -> {
                if (arguments.isBlank()) throw new DyuqueException("Usage: todo <description>");
                yield new InputBundle(command, new String[]{ arguments });
            }
            case "mark", "unmark" -> {
                if (arguments.isBlank()) throw new DyuqueException("Usage: " + command + " <index>");
                yield new InputBundle(command, new String[]{ arguments });
            }
            case "event" -> {
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

                yield new InputBundle(command, new String[]{ desc, from, to });
            }
            case "deadline" -> {
                String[] parts = arguments.split(" /by ", 2);          /* Splits "return book /by Sunday"
                                                                       into ["return book", "Sunday"] */
                if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                    throw new DyuqueException("Usage: deadline <description> /by <date>");
                }
                yield new InputBundle(command, new String[]{ parts[0].trim(), parts[1].trim() });
            }
            default -> throw new DyuqueException("Command not understood");
        };
    }

    public void executeCommand(String input) throws DyuqueException {
        printLine();
        InputBundle inputBundle = parseCommand(input);
        String command = inputBundle.command();
        String[] arguments = inputBundle.argument();
        /*  Elements of arguments:
                to-do:       description
                deadline:    description, dueDate
                event:       description, fromDate, toDate
                mark/unmark: index
         */

        switch (command) {
            case EXIT_CODE ->   printExit();
            case "list" ->      taskList.list();

            case "todo" ->      taskList.add(new Todo(arguments[0]));
            case "deadline" ->  taskList.add(new Deadline(arguments[0], arguments[1]));
            case "event" ->     taskList.add(new Event(arguments[0], arguments[1], arguments[2]));

            case "mark" -> taskList.setMarkedState(TaskList.markedState.MARKED, Integer.parseInt(arguments[0]));
            case "unmark" -> taskList.setMarkedState(TaskList.markedState.UNMARKED, Integer.parseInt(arguments[0]));

            default -> throw new DyuqueException("Command not understood");
        }
    }
}

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Dyuque {

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
            input = scanInput();
            executeCommand(input);
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
        System.out.println("  \"list\" - list stored tasks");
        System.out.println("  \"bye\" - quit Dyuque");
        printLine();
        System.out.println("Enter a new task or command:");
    }

    public void printExit() {
        System.out.println("Goodbye, hope to see you again soon!");
    }

    public String scanInput() {
        return scanner.nextLine();
    }

    public String[] parseCommand(String input) {
        // Splits "deadline return book /by Sunday" into ["deadline", "return book /by Sunday"]
        String[] commandAndArguments = input.split(" ", 2);
        String command = commandAndArguments[0];
        String arguments = (commandAndArguments.length > 1)
                ? commandAndArguments[1]
                : commandAndArguments[0]; // TODO: Make this less hacky.

        switch (command) {
            case EXIT_CODE -> {
                return new String[]{EXIT_CODE};
            }
            case "list" -> {
                return new String[]{"list"};
            }
            case "todo" -> {
                return new String[]{"todo", arguments};
            }
            case "deadline" -> {
                // Splits "return book /by Sunday" into ["return book", "Sunday"]
                String[] parts = arguments.split(" /by ");
                return new String[]{"deadline", parts[0], parts[1]};
            }
            case "event" -> {
                // Splits "meeting /from Mon 2pm /to 4pm" into ["meeting", "Mon 2pm", "4pm"]
                String[] parts = arguments.split(" /from | /to ");
                return new String[]{"event", parts[0], parts[1], parts[2]};
            }
            default -> {
                return commandAndArguments;
            }
        }
    }

    public void executeCommand(String input) {
        printLine();
        String[] commandAndArguments = parseCommand(input);
        String command = commandAndArguments[0];
        String[] arguments = Arrays.copyOfRange(commandAndArguments, 1, commandAndArguments.length);
        /*  Elements of arguments:
                to-do:       description
                deadline:    description, dueDate
                event:       description, fromDate, toDate
         */

        switch (command) {
            case EXIT_CODE ->   printExit();
            case "list" ->      taskList.list();
            case "todo" ->      taskList.add(new Todo(arguments[0]));
            case "deadline" ->  taskList.add(new Deadline(arguments[0], arguments[1]));
            case "event" ->     taskList.add(new Event(arguments[0], arguments[1], arguments[2]));
            default ->          System.out.println("Error: Please specify task type followed by task description");
        }
    }
}

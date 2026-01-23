import java.util.Objects;
import java.util.Scanner;

public class Dyuque {

    private static final String EXIT_CODE = "bye";

    private TodoList todoList;

    public static void main(String[] args) {
        new Dyuque().newChat();
    }

    public Dyuque(){
        this.todoList = new TodoList();
    }

    public void newChat() {
        printGreet();

        System.out.println("Enter a new task or command:");
        String input;
        do {
            input = scanInput();
            parseInput(input);
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
    }

    public void printExit() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    public String scanInput() {
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public String[] parseCommand(String input) {
        return input.split(" ", 2);
    }

    public void parseInput(String input) {
        printLine();
        if (Objects.equals(input, EXIT_CODE)) {
            printExit();
        } else if (Objects.equals(input, "list")) {
            todoList.read();
        } else if (Objects.equals(parseCommand(input)[0], "mark")) {
            todoList.mark(Integer.parseInt(parseCommand(input)[1]) - 1);
        } else if (Objects.equals(parseCommand(input)[0], "unmark")) {
            todoList.unmark(Integer.parseInt(parseCommand(input)[1]) - 1);
        } else {
            todoList.add(input);
        }
    }
}

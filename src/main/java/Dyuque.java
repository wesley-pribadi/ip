import java.util.Objects;
import java.util.Scanner;

public class Dyuque {

    private static final String EXIT_CODE = "bye";

    public static void main(String[] args) {
        new Dyuque().newChat();
    }

    public void newChat() {
        printGreet();

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
    }

    public void printExit() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    public String scanInput() {
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public void parseInput(String input) {
        printLine();
        if (Objects.equals(input, EXIT_CODE)) {
            printExit();
        } else {
            System.out.println(input);
            printLine();
        }
    }
}

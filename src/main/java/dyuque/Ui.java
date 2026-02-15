package dyuque;

import java.util.Scanner;

/**
 * Handles user interaction via standard input and output.
 */
public class Ui {
    /** ANSI escape code for resetting console color output. */
    private static final String ANSI_RESET = "\u001B[0m";
    /** ANSI escape code for red console color output. */
    private static final String ANSI_RED = "\u001B[31m";

    private final Scanner scanner;

    /**
     * Creates a UI instance that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns the next line of user input.
     *
     * @return User input line.
     */
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Prints the command prompt symbol.
     */
    public void showPrompt() {
        System.out.print("> ");
    }

    /**
     * Prints a horizontal separator line.
     */
    public String showLine() {
        String output = "__________________________________________________\n";
        System.out.print(output);
        return output;
    }

    /**
     * Prints the welcome message and basic usage instructions.
     */
    public String showWelcome() {
        String output = "Hello! I'm Dyuque\n"
                + "What can I do for you?\n"
                + "\n"
                + "Commands:\n"
                + "  \"list\" --- list tasks\n"
                + "  \"find\" --- find tasks by description\n"
                + "             find <keyword>\n"
                + "  \"delete\" - delete a task\n"
                + "  \"mark\" --- mark a task as done\n"
                + "             mark <task number>\n"
                + "  \"unmark\" - unmark a task as done\n"
                + "             unmark <task number>\n"
                + "  \"bye\" ---- quit Dyuque\n"
                + "Task types:\n"
                + "  todo ----- \"todo <description>\"\n"
                + "  deadline - \"deadline <description> /by <YYYY-MM-DD>\"\n"
                + "  event ---- \"event <description> /from <YYYY-MM-DD> /to <YYYY-MM-D\"\n"
                + "\n"
                + "Enter a new task or command:\n";

        System.out.println(output);
        return output;
    }

    /**
     * Prints the goodbye message.
     */
    public String showGoodbye() {
        System.out.println("Goodbye, hope to see you again soon!");
        return "Goodbye, hope to see you again soon!";
    }

    /**
     * Prints the specified message without adding extra formatting.
     *
     * @param message Message to print.
     */
    public String showMessage(String message) {
        System.out.print(message);
        return message;
    }

    /**
     * Prints the specified message as a non-fatal error in red.
     *
     * @param message Error message to print.
     */
    public void showError(String message) {
        System.out.print(ANSI_RED);
        System.out.println("[ERROR] " + message);
        System.out.print(ANSI_RESET);
    }

    /**
     * Prints the specified message as a fatal error in red.
     *
     * @param message Error message to print.
     */
    public void showFatal(String message) {
        System.out.print(ANSI_RED);
        System.out.println("[FATAL] " + message);
        System.out.print(ANSI_RESET);
    }
}

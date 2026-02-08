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
    public void showLine() {
        System.out.print("__________________________________________________");
        System.out.println();
    }

    /**
     * Prints the welcome message and basic usage instructions.
     */
    public void showWelcome() {
        System.out.println("Hello! I'm Dyuque");
        System.out.println("What can I do for you?");
        showLine();
        System.out.println("Commands:");
        System.out.println("  \"list\" --- list tasks");
        System.out.println("  \"find\" --- find tasks by description");
        System.out.println("             find <keyword>");
        System.out.println("  \"delete\" - delete a task");
        System.out.println("  \"mark\" --- mark a task as done");
        System.out.println("             mark <task number>");
        System.out.println("  \"unmark\" - unmark a task as done");
        System.out.println("             unmark <task number>");
        System.out.println("  \"bye\" ---- quit Dyuque");
        System.out.println("dyuque.Task types:");
        System.out.println("  todo ----- \"todo <description>\"");
        System.out.println("  deadline - \"deadline <description> /by <YYYY-MM-DD>\"");
        System.out.println("  event ---- \"event <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD>\"");
        showLine();
        System.out.println("Enter a new task or command:");
    }

    /**
     * Prints the goodbye message.
     */
    public void showGoodbye() {
        System.out.println("Goodbye, hope to see you again soon!");
    }

    /**
     * Prints the specified message without adding extra formatting.
     *
     * @param message Message to print.
     */
    public void showMessage(String message) {
        System.out.print(message);
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

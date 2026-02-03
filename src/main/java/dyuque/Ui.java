package dyuque;

import java.util.Scanner;

public class Ui {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public String readLine() {
        return scanner.nextLine();
    }

    public void showPrompt() {
        System.out.print("> ");
    }

    public void showLine() {
        System.out.print("__________________________________________________");
        System.out.println();
    }

    public void showWelcome() {
        System.out.println("Hello! I'm dyuque.Dyuque");
        System.out.println("What can I do for you?");
        showLine();
        System.out.println("Commands:");
        System.out.println("  \"list\" --- list stored tasks");
        System.out.println("  \"delete\" - delete a task");
        System.out.println("  \"mark\" --- mark a task as done");
        System.out.println("             mark <task number>");
        System.out.println("  \"unmark\" - unmark a task as done");
        System.out.println("             unmark <task number>");
        System.out.println("  \"bye\" ---- quit dyuque.Dyuque");
        System.out.println("dyuque.Task types:");
        System.out.println("  todo ----- \"todo <description>\"");
        System.out.println("  deadline - \"deadline <description> /by <YYYY-MM-DD>\"");
        System.out.println("  event ---- \"event <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD>\"");
        showLine();
        System.out.println("Enter a new task or command:");
    }

    public void showGoodbye() {
        System.out.println("Goodbye, hope to see you again soon!");
    }

    public void showMessage(String message) {
        System.out.print(message);
    }

    public void showError(String message) {
        System.out.print(ANSI_RED);
        System.out.println("[ERROR] " + message);
        System.out.print(ANSI_RESET);
    }

    public void showFatal(String message) {
        System.out.print(ANSI_RED);
        System.out.println("[FATAL] " + message);
        System.out.print(ANSI_RESET);
    }
}

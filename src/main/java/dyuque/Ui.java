package dyuque;

import java.util.List;

/**
 * Formats user-facing messages for display.
 */
public class Ui {
    /** ANSI escape code for resetting console color output. */
    private static final String ANSI_RESET = "\u001B[0m";
    /** ANSI escape code for red console color output. */
    private static final String ANSI_RED = "\u001B[31m";
    /** ANSI escape code for gray console color output. */
    private static final String ANSI_GRAY = "\u001B[90m";

//    private final Scanner scanner;
//
//    /**
//     * Creates a UI instance that reads from standard input.
//     */
//    public Ui() {
//        this.scanner = new Scanner(System.in);
//    }
//    /**
//     * Returns the next line of user input.
//     *
//     * @return User input line.
//     */
//    public String readLine() {
//        return scanner.nextLine();
//    }

    /**
     * Prints a horizontal separator line.
     */
    public void showLine(String input) {
        int count = 0;
        int maxLength = input.lines()
                .mapToInt(String::length)
                .max()
                .orElse(3);

        StringBuilder output = new StringBuilder();
        output.append(">");
        while (count++ < maxLength) {
            output.append("-");
        }
        output.append("<");

        printlnGrayColour(output.toString());
    }

    /**
     * Prints the usage instructions.
     */
    public String showHelp() {
        String output = """
                Commands:
                  "list" --- list tasks
                  "find" --- find tasks by description
                             find <keyword>
                  "delete" - delete a task
                  "mark" --- mark a task as done
                             mark <task number>
                  "unmark" - unmark a task as done
                             unmark <task number>
                  "bye" ---- quit Dyuque
                  "undo" --- undo previous state change
                Task types:
                  todo ----- "todo <description>"
                  deadline - "deadline <description> /by <YYYY-MM-DD>"
                  event ---- "event <description> /from <YYYY-MM-DD> /to <YYYY-MM-D"
                """;

        System.out.println(output);
        return output;
    }

    /**
     * Prints the welcome message and basic usage instructions.
     */
    public String showWelcome() {
        StringBuilder output = new StringBuilder("""
                Hello! I'm Dyuque.
                What can I do for you?
                """);
        output.append(System.lineSeparator());
        output.append(showHelp());
        output.append(System.lineSeparator());
        output.append(showPrompt());

        System.out.println(output);
        return output.toString();
    }

    /**
     * Prints the welcome message and basic usage instructions.
     */
    public String showPrompt() {
        String output = "Enter a new task or command:";

        System.out.println(output);
        return output;
    }

        /**
         * Prints the goodbye message.
         */
    public static String showGoodbye() {
        String message = "Goodbye, hope to see you again soon!";
        System.out.println(message);
        return message;
    }

    /**
     * Formats and displays a list of tasks.
     * Message is varied depending on if tasks is a filtered subset.
     *
     * @param tasks List of tasks to display.
     * @param isFiltered Whether tasks is a filtered subset or not.
     * @return Formatted message.
     */
    public String formatTaskList(List<Task> tasks, boolean isFiltered) {
        StringBuilder output = new StringBuilder();
        output.append("You have (").append(tasks.size());

        if (isFiltered) {
            output.append(") matching tasks:\n\n");
        } else {
            output.append(") tasks:\n\n");
        }

        int i = 1;
        for (Task task : tasks) {
            output.append(i++)
                    .append(". ")
                    .append(task)
                    .append(System.lineSeparator());
        }

        String message = output.toString();
        System.out.print(message);
        return message;
    }

    /**
     * Formats and displays a task addition confirmation.
     *
     * @param addedTask The task that was added.
     * @param totalCount New total number of tasks.
     * @return Formatted message.
     */
    public String formatTaskAdded(Task addedTask, int totalCount) {
        String message = "Added:\n"
                + addedTask
                + formatTotalTaskCount(totalCount);

        System.out.print(message);
        return message;
    }

    /**
     * Formats and displays a task deletion confirmation.
     *
     * @param deletedTask The task that was deleted.
     * @param totalCount New total number of tasks.
     * @return Formatted message.
     */
    public String formatTaskDeleted(Task deletedTask, int totalCount) {
        String message = "Removed:\n"
                + deletedTask
                + formatTotalTaskCount(totalCount);

        System.out.print(message);
        return message;
    }

    private String formatTotalTaskCount(int totalCount) {
        return System.lineSeparator()
        + System.lineSeparator()
        + "You now have (" + totalCount + ") tasks."
        + System.lineSeparator();
    }

    /**
     * Formats and displays a task's state change.
     *
     * @param task The task that was marked.
     * @return Formatted message.
     */
    public String formatTaskChangedState(Task task, Task.State state) {
        StringBuilder message = new StringBuilder();
        switch (state) {
            case MARKED -> message.append("Nice! I've marked this task as done:");
            case UNMARKED -> message.append("OK, I've marked this task as not done yet:");
        }
        message.append(System.lineSeparator())
                .append(task)
                .append(System.lineSeparator());

        System.out.print(message);
        return message.toString();
    }

    /**
     * Prints the specified message as a non-fatal error in red.
     *
     * @param message Error message to print.
     */
    public static String showError(String message) {
        String output = "[ERROR] " + message;
        printlnRedColour(output);
        return output;
    }

    private static void printlnRedColour(String output) {
        System.out.print(ANSI_RED);
        System.out.println(output);
        System.out.print(ANSI_RESET);
    }

    private static void printlnGrayColour(String output) {
        System.out.print(ANSI_GRAY);
        System.out.println(output);
        System.out.print(ANSI_RESET);
    }
}

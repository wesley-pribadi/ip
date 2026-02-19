package dyuque;

import java.util.List;

/**
 * Formats user-facing messages for display.
 */
@SuppressWarnings("SameReturnValue")
public final class Ui {

    private Ui() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Formats and returns the usage instructions.
     *
     * @return Formatted help message.
     */
    public static String showHelp() {
        return """
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
    }

    /**
     * Formats and returns the welcome message and basic usage instructions.
     *
     * @return Formatted welcome message.
     */
    public static String showWelcome() {
        return """
                Hello! I'm Dyuque.
                What can I do for you?
                """
                + System.lineSeparator()
                + showHelp()
                + System.lineSeparator()
                + showPrompt();
    }

    /**
     * Formats and returns the welcome message and basic usage instructions.
     *
     * @return Formatted prompt message.
     */
    public static String showPrompt() {
        return "Enter a new task or command:";
    }

    /**
     * Formats and returns the goodbye message.
     *
     * @return Formatted goodbye message.
     */
    public static String showGoodbye() {
        return "Goodbye, hope to see you again soon!";
    }

    /**
     * Formats and returns the specified message as a non-fatal error in red.
     *
     * @param message Formatted error message.
     */
    public static String showError(String message) {
        return "[ERROR] " + message;
    }

    /**
     * Formats and displays a list of tasks.
     * Message is varied depending on if tasks is a filtered subset.
     *
     * @param tasks List of tasks to display.
     * @param isFiltered Whether tasks is a filtered subset or not.
     * @return Formatted list of tasks.
     */
    public static String formatTaskList(List<Task> tasks, boolean isFiltered) {
        StringBuilder output = new StringBuilder();
        formatTaskListAddHeader(tasks, isFiltered, output);
        formatTaskListAddNumbers(tasks, output);

        return output.toString();
    }

    private static void formatTaskListAddNumbers(List<Task> tasks, StringBuilder output) {
        int i = 1;
        for (Task task : tasks) {
            output.append(i++)
                    .append(". ")
                    .append(task)
                    .append(System.lineSeparator());
        }
    }

    private static void formatTaskListAddHeader(List<Task> tasks, boolean isFiltered, StringBuilder output) {
        output.append("You have (").append(tasks.size()).append(") ");

        if (isFiltered) {
            output.append("matching tasks:\n\n");
        } else {
            output.append("tasks:\n\n");
        }
    }

    /**
     * Formats and displays a task addition confirmation.
     *
     * @param addedTask The task that was added.
     * @param totalCount New total number of tasks.
     * @return Formatted task added message.
     */
    public static String formatTaskAdded(Task addedTask, int totalCount) {
        return "Added:\n"
                + addedTask
                + formatTotalTaskCount(totalCount);
    }

    /**
     * Formats and displays a task deletion confirmation.
     *
     * @param deletedTask The task that was deleted.
     * @param totalCount New total number of tasks.
     * @return Formatted task deleted message.
     */
    public static String formatTaskDeleted(Task deletedTask, int totalCount) {
        return "Removed:\n"
                + deletedTask
                + formatTotalTaskCount(totalCount);
    }

    private static String formatTotalTaskCount(int totalCount) {
        return System.lineSeparator()
                + System.lineSeparator()
                + "You now have (" + totalCount + ") tasks."
                + System.lineSeparator();
    }

    /**
     * Formats and displays a task's state change.
     *
     * @param task The task that was marked.
     * @return Formatted task state changed message.
     */
    public static String formatTaskChangedState(Task task, Task.State state) throws DyuqueException {
        StringBuilder output = new StringBuilder();
        switch (state) {
            case MARKED -> output.append("Nice! I've marked this task as done:");
            case UNMARKED -> output.append("OK, I've marked this task as not done yet:");
            default -> throw new DyuqueException("Received illegal state");
        }
        output.append(System.lineSeparator())
                .append(task)
                .append(System.lineSeparator());

        return output.toString();
    }
}

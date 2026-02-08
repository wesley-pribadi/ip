package dyuque;

import java.util.Optional;
import java.util.Set;

/**
 * Runs the Dyuque chatbot application and coordinates command execution.
 */
public class Dyuque {
    /** Default relative path used to store saved tasks. */
    private static final String SAVE_PATH = "./data/dyuque.txt";

    /** UI component responsible for interacting with the user. */
    private final Ui ui;
    /** Storage component responsible for loading and saving tasks. */
    private final Storage storage;
    /** Parser component responsible for converting user input into commands. */
    private final Parser parser;
    /** In-memory list of tasks managed by the chatbot. */
    private final TaskList taskList;
    /** Whether the main chat loop should continue executing. */
    private boolean shouldContinueExecution;

    /**
     * Represents the supported commands and their accepted keywords.
     */
    protected enum Command {
        List("list", "ls"),
        Delete("delete", "remove"),
        Todo("todo"),
        Deadline("deadline"),
        Event("event"),
        Mark("mark"),
        Unmark("unmark"),
        Exit("bye", "exit");

        /** Keywords that map to this command. */
        private final Set<String> keywords;

        Command(String... keywords) {
            this.keywords = Set.of(keywords);
        }

        /**
         * Returns the command matching the specified keyword, if any.
         *
         * @param keyword Keyword to resolve into a command.
         * @return Matching command if the keyword is recognized.
         */
        protected static Optional<Command> get(String keyword) {
            for (Command command : values()) {
                if (command.keywords.contains(keyword)) {
                    return Optional.of((command));
                }
            }
            return Optional.empty();
        }
    }

    /** Package-private constructor for test classes */
    Dyuque(Ui ui, Storage storage, Parser parser, TaskList taskList) {
        this.ui = ui;
        this.storage = storage;
        this.parser = parser;
        this.taskList = taskList;
        this.shouldContinueExecution = true;
    }

    /**
     * Creates a Dyuque instance configured with default UI, storage, parser, and task list.
     *
     * @return A fully initialized Dyuque instance.
     * @throws DyuqueException If saved tasks cannot be loaded.
     */
    private static Dyuque initialiseDefaults() throws DyuqueException {
        Ui ui = new Ui();
        Storage storage = new Storage(SAVE_PATH);
        Parser parser = new Parser();
        TaskList taskList = new TaskList(storage.load(), storage);

        return new Dyuque(ui, storage, parser, taskList);
    }

    /**
     * Starts the chatbot application (entry point).
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        try {
            Dyuque.initialiseDefaults().newChat();
        } catch (DyuqueException e) {
            Ui ui = new Ui();
            ui.showFatal("Failed to load saved tasks due to error:");
            ui.showFatal(e.getMessage());
        }
    }

    /**
     * Starts a new interactive chat session and continues until an exit command is received.
     */
    public void newChat() {
        ui.showWelcome();
        do {
            ui.showPrompt();
            try {
                shouldContinueExecution = executeCommand(ui.readLine());
            } catch (DyuqueException e) {
                ui.showError(e.getMessage());
            }
        } while (shouldContinueExecution);
    }

    /**
     * Executes the specified user input as a command and returns whether execution should continue.
     *
     * @param input Raw user input line.
     * @return Whether the chat loop should continue after executing the command.
     * @throws DyuqueException If the input cannot be parsed or the command fails to execute.
     */
    public boolean executeCommand(String input) throws DyuqueException {
        ui.showLine();
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
                case Exit -> {
                    ui.showGoodbye();
                    return false;
                }
                case List -> ui.showMessage(taskList.list());

                case Todo -> ui.showMessage(taskList.add(new Todo(arguments[0])));
                case Deadline -> ui.showMessage(taskList.add(new Deadline(arguments[0], arguments[1])));
                case Event -> ui.showMessage(taskList.add(new Event(arguments[0], arguments[1], arguments[2])));

                case Delete -> ui.showMessage(taskList.delete(Integer.parseInt(arguments[0]) - 1));

                case Mark ->
                        ui.showMessage(taskList.setMarkedState(TaskList.markedState.Marked, Integer.parseInt(arguments[0]) - 1));
                case Unmark ->
                        ui.showMessage(taskList.setMarkedState(TaskList.markedState.Unmarked, Integer.parseInt(arguments[0]) - 1));

            }
        } catch (NumberFormatException nfe) {
            throw new DyuqueException("Expected integer but received something else...", nfe);
        }
        return true;
    }
}

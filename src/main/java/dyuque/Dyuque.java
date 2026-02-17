package dyuque;

import java.util.List;
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
        LIST("list",
                "list", "ls"),
        FIND("find <keyword>",
                "find"),
        TODO("todo <description>",
                "todo"),
        DEADLINE("deadline <description> /by <date>",
                "deadline"),
        EVENT("event <description> /from <date> /to <date>",
                "event"),
        DELETE("delete <index>",
                "delete", "remove"),
        MARK("mark <index>",
                "mark"),
        UNMARK("unmark <index>",
                "unmark"),
        EXIT("exit",
                "bye", "exit");

        /** User-facing help string to use the command. */
        private final String usageHelpStr;

        /** Keywords that map to this command. */
        private final Set<String> keywords;

        Command(String usageHelpStr, String... keywords) {
            this.usageHelpStr = usageHelpStr;
            this.keywords = Set.of(keywords);
        }

        /**
         * Returns the user-facing string explaining the command syntax.
         *
         * @return String explaining the command syntax.
         */
        public String getUsageHelpStr() {
            return "Usage: " + usageHelpStr;
        }

        /**
         * Returns the command matching the specified keyword, if any.
         *
         * @param keyword Keyword to resolve into a command.
         * @return Matching command if the keyword is recognized.
         */
        protected static Optional<Command> getCommand(String keyword) {
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
    static Dyuque initialiseDefaults() throws DyuqueException {
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
            Dyuque.initialiseDefaults().newCliChat();
        } catch (DyuqueException e) {
            Ui ui = new Ui();
            ui.showFatal("Failed to load saved tasks due to error:");
            ui.showFatal(e.getMessage());
        }
    }

    /**
     * Starts a new CLI chat session and continues until an exit command is received.
     */
    public void newCliChat() {
        // Method used for debugging, will be removed before release.
        ui.showWelcome();
        do {
            ui.showPrompt();
            try {
                getResponse(ui.readLine()); // updates shouldContinueExecution
            } catch (DyuqueException e) {
                ui.showError(e.getMessage());
            }
        } while (shouldContinueExecution);
    }

    /**
     * Processes user input and returns the response message for display.
     *
     * @param input Raw user input line.
     * @return The user-facing response message.
     * @throws DyuqueException If the input cannot be parsed or the command fails to execute.
     */
    public String getResponse(String input) throws DyuqueException {
        ui.showLine();

        CommandArgumentPair pair = parser.parseCommand(input);
        return executeCommand(pair);
    }

    /**
     * Executes the parsed command and returns the result message.
     *
     * @param pair The parsed command and arguments.
     * @return The user-facing output String generated by the executed command.
     * @throws DyuqueException If the command fails to execute.
     */
    String executeCommand(CommandArgumentPair pair) throws DyuqueException {
        Command command = pair.command();
        assert command != null : "Parser returned null command";

        String[] arguments = pair.argument();
        assert arguments != null : "Parser returned null arguments";
        /*  Elements of arguments:
                delete/mark/unmark: index
                to-do:              description
                deadline:           description, dueDate
                event:              description, fromDate, toDate
         */

        return switch (command) {
            case EXIT -> handleExit();
            case LIST -> handleList();
            case FIND -> handleFind(arguments[0]);
            case TODO -> handleAddTask(new Todo(arguments[0]));
            case DEADLINE -> handleAddTask(new Deadline(arguments[0], arguments[1]));
            case EVENT -> handleAddTask(new Event(arguments[0], arguments[1], arguments[2]));
            case DELETE -> handleDeleteTask(arguments[0]);
            case MARK -> handleMarkTask(arguments[0]);
            case UNMARK -> handleUnmarkTask(arguments[0]);
        };
    }

    private String handleExit() {
        shouldContinueExecution = false;
        return ui.showGoodbye();
    }

    private String handleList() {
        List<Task> allTasks = taskList.getAllTasks();
        return ui.formatTaskList(allTasks, false);
    }

    private String handleFind(String keyword) {
        List<Task> matchingTasks = taskList.find(keyword);
        return ui.formatTaskList(matchingTasks, true);
    }

    private String handleAddTask(Task task) throws DyuqueException {
        Task addedTask = taskList.add(task);
        return ui.formatTaskAdded(addedTask, taskList.size());
    }

    private String handleDeleteTask(String indexStr) throws DyuqueException {
        int index = parseTaskIndex(indexStr);
        Task deletedTask = taskList.delete(index);
        return ui.formatTaskDeleted(deletedTask, taskList.size());
    }

    private String handleMarkTask(String indexStr) throws DyuqueException {
        int index = parseTaskIndex(indexStr);
        Task markedTask = taskList.setMarkedState(TaskList.MarkedState.MARKED, index);
        return ui.formatTaskMarked(markedTask);
    }

    private String handleUnmarkTask(String indexStr) throws DyuqueException {
        int index = parseTaskIndex(indexStr);
        Task unmarkedTask = taskList.setMarkedState(TaskList.MarkedState.UNMARKED, index);
        return ui.formatTaskUnmarked(unmarkedTask);
    }

    private int parseTaskIndex(String indexStr) throws DyuqueException {
        try {
            return Integer.parseInt(indexStr) - 1;
        } catch (NumberFormatException nfe) {
            throw new DyuqueException("Expected integer but received: " + indexStr, nfe);
        }
    }

    String getWelcomeMessage() {
        return ui.showWelcome();
    }

    Boolean getShouldContinueExecution() {
        return shouldContinueExecution;
    }
}

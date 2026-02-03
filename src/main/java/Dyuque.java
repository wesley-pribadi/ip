import java.util.Optional;
import java.util.Set;

public class Dyuque {
    private static final String SAVE_PATH = "./data/dyuque.txt";

    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private final TaskList taskList;
    private Boolean shouldContinueExecution;

    protected enum Command {
        LIST("list", "ls"),
        DELETE("delete", "remove"),
        TODO("todo"),
        DEADLINE("deadline"),
        EVENT("event"),
        MARK("mark"),
        UNMARK("unmark"),
        EXIT_CODE("bye", "exit");

        private final Set<String> keywords;

        Command(String... keywords) {
            this.keywords = Set.of(keywords);
        }

        protected static Optional<Command> get(String keyword) {
            for (Command command : values()) {
                if (command.keywords.contains(keyword)) {
                    return Optional.of((command));
                }
            }
            return Optional.empty();
        }
    }

    public Dyuque() throws DyuqueException {
        this.ui = new Ui();
        this.storage = new Storage(SAVE_PATH);
        this.parser = new Parser();
        this.taskList = new TaskList(storage.load(), storage);
        /* if file/folder missing: Storage creates them and loads empty list
           if reading fails: throw and exit (no overwrite risk) */
        this.shouldContinueExecution = true;
    }

    public static void main(String[] args) {
        try {
            new Dyuque().newChat();
        } catch (DyuqueException e) {
            Ui ui = new Ui();
            ui.showFatal("Failed to load saved tasks due to error:");
            ui.showFatal(e.getMessage());
        }
    }

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

    public Boolean executeCommand(String input) throws DyuqueException {
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
                case EXIT_CODE -> {
                    ui.showGoodbye();
                    return false;
                }
                case LIST -> ui.showMessage(taskList.list());

                case TODO -> ui.showMessage(taskList.add(new Todo(arguments[0])));
                case DEADLINE -> ui.showMessage(taskList.add(new Deadline(arguments[0], arguments[1])));
                case EVENT -> ui.showMessage(taskList.add(new Event(arguments[0], arguments[1], arguments[2])));

                case DELETE -> ui.showMessage(taskList.delete(Integer.parseInt(arguments[0]) - 1));

                case MARK ->
                        ui.showMessage(taskList.setMarkedState(TaskList.markedState.MARKED, Integer.parseInt(arguments[0]) - 1));
                case UNMARK ->
                        ui.showMessage(taskList.setMarkedState(TaskList.markedState.UNMARKED, Integer.parseInt(arguments[0]) - 1));

            }
        } catch (NumberFormatException nfe) {
            throw new DyuqueException("Expected integer but received something else...", nfe);
        }
        return true;
    }
}

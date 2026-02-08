package dyuque;

import java.util.Optional;
import java.util.Set;

public class Dyuque {
    private static final String SAVE_PATH = "./data/dyuque.txt";

    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private final TaskList taskList;
    private boolean shouldContinueExecution;

    protected enum Command {
        List("list", "ls"),
        Delete("delete", "remove"),
        Todo("todo"),
        Deadline("deadline"),
        Event("event"),
        Mark("mark"),
        Unmark("unmark"),
        Exit("bye", "exit");

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

    // Package-private constructor for tests
    Dyuque(Ui ui, Storage storage, Parser parser, TaskList taskList) {
        this.ui = ui;
        this.storage = storage;
        this.parser = parser;
        this.taskList = taskList;
        this.shouldContinueExecution = true;
    }

    private static Dyuque initialiseDefaults() throws DyuqueException {
        Ui ui = new Ui();
        Storage storage = new Storage(SAVE_PATH);
        Parser parser = new Parser();
        TaskList taskList = new TaskList(storage.load(), storage);

        return new Dyuque(ui, storage, parser, taskList);
    }

    public static void main(String[] args) {
        try {
            Dyuque.initialiseDefaults().newChat();
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

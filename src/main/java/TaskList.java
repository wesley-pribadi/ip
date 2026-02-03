import java.util.ArrayList;

public class TaskList {
    private final ArrayList<Task> items;
    private final Storage storage;

    protected enum markedState {
        Marked,
        Unmarked
    }

    public TaskList(ArrayList<Task> initialItems, Storage storage) {
        this.items = initialItems;
        this.storage = storage;
    }

    public String list() {
        StringBuilder output = new StringBuilder();
        output.append("You have (").append(size()).append(") tasks:\n");

        int i = 1;
        for (Task task : items) {
            output.append(i++).append(". ").append(task).append(System.lineSeparator());
        }
        return output.toString();
    }

    public String add(Task task) throws DyuqueException {
        items.add(task);
        saveIfEnabled();

        StringBuilder output = new StringBuilder();
        output.append("Added:\n")
                .append(task)
                .append(System.lineSeparator())
                .append("You now have (")
                .append(size())
                .append(") tasks.")
                .append(System.lineSeparator());

        return output.toString();
    }

    public String delete(int arrayIndex) throws DyuqueException {
        Task removed = get(arrayIndex);  // validate once
        items.remove(arrayIndex);
        saveIfEnabled();

        StringBuilder output = new StringBuilder();
        output.append("Removed:\n")
                .append(removed)
                .append(System.lineSeparator())
                .append("You now have (")
                .append(size())
                .append(") tasks.")
                .append(System.lineSeparator());

        return output.toString();
    }

    protected String setMarkedState(markedState state, int arrayIndex) throws DyuqueException {
        // arrayIndex is 0-based
        Task task = get(arrayIndex);

        String message = switch (state) {
            case Marked -> {
                task.markDone();
                yield "Nice! I've marked this task as done:";
            }
            case Unmarked -> {
                task.markUndone();
                yield "OK, I've marked this task as not done yet:";
            }
        };

        saveIfEnabled();

        return message + System.lineSeparator()
                + task + System.lineSeparator();
    }

    public int size() {
        return items.size();
    }

    private Task get(int arrayIndex) throws DyuqueException {
        // arrayIndex is 0-based

        if (arrayIndex < 0 || (arrayIndex + 1) > size()) {
            throw new DyuqueException("Task " + (arrayIndex + 1) + " does not exist.\nThere are only " + size() + " tasks");
        }
        return items.get(arrayIndex);
    }

    private void saveIfEnabled() throws DyuqueException {
        if (storage != null) {
            storage.save(items);
        }
    }
}

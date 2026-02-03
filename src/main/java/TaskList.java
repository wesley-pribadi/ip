import java.util.ArrayList;

public class TaskList {
    private final ArrayList<Task> items;
    private final Storage storage;

    protected enum markedState {
        MARKED,
        UNMARKED
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

    public void delete(int arrayIndex) throws DyuqueException {
        System.out.println("Removed:\n" + get(arrayIndex));
        items.remove(get(arrayIndex));
        printSize();
        saveIfEnabled();
    }

    protected void setMarkedState(markedState state, int arrayIndex) throws DyuqueException {
        // arrayIndex is 0-based

        Task task = get(arrayIndex);

        switch (state) {
            case MARKED:
                task.markDone();
                System.out.println("Nice! I've marked this task as done:");
                break;
            case UNMARKED:
                task.markUndone();
                System.out.println("OK, I've marked this task as not done yet:");
                break;
        }
        System.out.println(task);
        saveIfEnabled();
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

    private void printSize() {
        System.out.println("You now have (" + size() + ") tasks.");
    }

    private void saveIfEnabled() throws DyuqueException {
        if (storage != null) {
            storage.save(items);
        }
    }
}

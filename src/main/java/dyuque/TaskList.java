package dyuque;

import java.util.ArrayList;

/**
 * Manages an in-memory list of tasks and persists changes when storage is enabled.
 */
public class TaskList {
    /** Mutable list of tasks managed by this task list. */
    private final ArrayList<Task> items;
    /** Storage used to persist task list changes. */
    private final Storage storage;

    /**
     * Represents the completion state to apply when marking or unmarking a task.
     */
    protected enum markedState {
        Marked,
        Unmarked
    }

    /**
     * Creates a task list with the specified initial items and storage handler.
     *
     * @param initialItems Initial tasks to manage.
     * @param storage Storage handler used to persist changes.
     */
    public TaskList(ArrayList<Task> initialItems, Storage storage) {
        this.items = initialItems;
        this.storage = storage;
    }

    /**
     * Returns a formatted list of all tasks in this task list.
     *
     * @return User-facing list of tasks.
     */
    public String list() {
        StringBuilder output = new StringBuilder();
        output.append("You have (").append(size()).append(") tasks:\n");

        int i = 1;
        for (Task task : items) {
            output.append(i++)
                    .append(". ")
                    .append(task)
                    .append(System.lineSeparator());
        }
        return output.toString();
    }

    /**
     * Returns a formatted list of tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Keyword to search for within task descriptions.
     * @return User-facing list of matching tasks.
     */
    public String find(String keyword) {
        String needle = keyword.trim().toLowerCase();
        StringBuilder output = new StringBuilder();
        int matchCount = 0;

        for (Task task : items) {
            String haystack = task.getDescription().toLowerCase();
            if (haystack.contains(needle)) {
                matchCount++;
                output.append(matchCount)
                        .append(". ")
                        .append(task)
                        .append(System.lineSeparator());
            }
        }

        return "You have (" + matchCount + ") matching tasks:\n"
                + output.toString();
    }

    /**
     * Adds the specified task to this task list and returns a confirmation message.
     *
     * @param task Task to add.
     * @return User-facing confirmation message.
     * @throws DyuqueException If the task list cannot be saved.
     */
    public String add(Task task) throws DyuqueException {
        int previousSize = size();
        items.add(task);
        assert size() == previousSize + 1 : "Task list size should increase after adding";

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

    /**
     * Deletes the task at the specified 0-based index and returns a confirmation message.
     *
     * @param arrayIndex 0-based index of the task to delete.
     * @return User-facing confirmation message.
     * @throws DyuqueException If the index is invalid or the task list cannot be saved.
     */
    public String delete(int arrayIndex) throws DyuqueException {
        Task removed = getTask(arrayIndex);

        int previousSize = size();
        items.remove(arrayIndex);
        assert previousSize == size() - 1 : "Task list size should decrease after deleting";

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

    /**
     * Updates the marked state of the task at the specified 0-based index.
     *
     * @param state Marked state to apply.
     * @param arrayIndex 0-based index of the task to update.
     * @return User-facing confirmation message.
     * @throws DyuqueException If the index is invalid or the task list cannot be saved.
     */
    protected String setMarkedState(markedState state, int arrayIndex) throws DyuqueException {
        Task task = getTask(arrayIndex);

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

    private Task getTask(int arrayIndex) throws DyuqueException {
        validateIndexBounds(arrayIndex);
        return items.get(arrayIndex);
    }

    private void saveIfEnabled() throws DyuqueException {
        if (storage != null) {
            storage.save(items);
        }
    }

    private void validateIndexBounds(int arrayIndex) throws DyuqueException {
        // arrayIndex is 0-based
        if (arrayIndex < 0 || arrayIndex >= size()) {
            throw new DyuqueException("dyuque.Task " + (arrayIndex + 1) + " does not exist.\nThere are only " + size() + " tasks");
        }
    }

    private int size() {
        return items.size();
    }
}

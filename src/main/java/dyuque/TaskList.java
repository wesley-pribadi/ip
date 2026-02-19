package dyuque;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages an in-memory list of tasks and persists changes when storage is enabled.
 */
public class TaskList {
    /** Mutable list of tasks managed by this task list. */
    private final ArrayList<Task> items;
    /** Storage used to persist task list changes. */
    private final Storage storage;

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
     * Returns all tasks in this task list.
     *
     * @return Defensive copy of all tasks.
     */
    public List<Task> getAllTasks() {
        return List.copyOf(items);
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Keyword to search for within task descriptions.
     * @return List of matching tasks.
     */
    public List<Task> find(String keyword) {
        String needle = keyword.trim().toLowerCase();
        List<Task> matches = new ArrayList<>();

        for (Task task : items) {
            String haystack = task.getDescription().toLowerCase();
            if (haystack.contains(needle)) {
                matches.add(task);
            }
        }

        return matches;
    }

    /**
     * Adds the specified task to this task list.
     *
     * @param task Task to add.
     * @return The added task.
     * @throws DyuqueException If the task list cannot be saved.
     */
    public Task add(Task task) throws DyuqueException {
        int previousSize = size();
        items.add(task);
        assert size() == previousSize + 1 : "Task list size should increase after adding";

        saveIfEnabled();
        return task;
    }

    /**
     * Adds the specified task at a specific index to this task list.
     *
     * @param index Index of task to add.
     * @param task Task to add.
     * @throws DyuqueException If the task list cannot be saved.
     */
    public void add(int index, Task task) throws DyuqueException {
        int previousSize = size();
        items.add(index, task);
        assert size() == previousSize + 1 : "Task list size should increase after adding";

        saveIfEnabled();
    }

    /**
     * Deletes the task at the specified 0-based index.
     *
     * @param arrayIndex 0-based index of the task to delete.
     * @return The deleted task.
     * @throws DyuqueException If the index is invalid or the task list cannot be saved.
     */
    public Task delete(int arrayIndex) throws DyuqueException {
        Task removedTask = getTask(arrayIndex);

        int previousSize = size();
        items.remove(arrayIndex);
        assert size() == previousSize - 1 : "Task list size should decrease after deleting";

        saveIfEnabled();
        return removedTask;
    }

    /**
     * Updates the marked state of the task at the specified 0-based index.
     *
     * @param state Marked state to apply.
     * @param arrayIndex 0-based index of the task to update.
     * @return The updated task.
     * @throws DyuqueException If the index is invalid or the task list cannot be saved.
     */
    public Task setMarkedState(Task.State state, int arrayIndex) throws DyuqueException {
        Task task = getTask(arrayIndex);
        task.setState(state);
        saveIfEnabled();
        return task;
    }

    /**
     * Gets the marked state of the task at the specified 0-based index.
     *
     * @param arrayIndex 0-based index of the task to update.
     * @return The task state.
     * @throws DyuqueException If the index is invalid or the task list cannot be saved.
     */
    public Task.State getTaskState(int arrayIndex) throws DyuqueException {
        return getTask(arrayIndex).getState();
    }

    /**
     * Returns the current number of tasks in this list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return items.size();
    }

    Task getTask(int arrayIndex) throws DyuqueException {
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
            throw new DyuqueException("Task "
                    + (arrayIndex + 1)
                    + " does not exist.\nThere are only "
                    + size()
                    + " tasks");
        }
    }
}

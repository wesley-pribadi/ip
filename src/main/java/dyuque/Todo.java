package dyuque;

/**
 * Represents a task with no associated date information.
 */
public class Todo extends Task {

    /**
     * Creates a todo task with the specified description.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the user-facing string representation of this todo task.
     */
    @Override
    public String toString() {
        return "[T]"
                + (state.equals(State.MARKED) ? "[X] " : "[ ] ")
                + description;
    }

    /**
     * Returns the storage-formatted string representation of this todo task.
     */
    @Override
    public String toStorageString() {
        return "T | " + doneFlag() + " | " + description;
    }
}

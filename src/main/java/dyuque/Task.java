package dyuque;

/**
 * Represents a task with a description and a completion state.
 * <p>
 * Concrete subclasses define specific task types (e.g. todos, deadlines, events)
 * and must implement {@link #toStorageString()} to support persistence.
 */
public abstract class Task {
    /** Description of the task. */
    protected final String description;
    /** Whether the task state is marked or unmarked. */
    protected State state;

    /**
     * Represents the state of a task.
     */
    public enum State {
        /** The task is finished and will be shown as such. */
        MARKED,
        /** The task is still pending and requires action. */
        UNMARKED
    }

    /**
     * Creates a task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.state = State.UNMARKED;
    }

    /**
     * Sets the task's state.
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Returns the task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the task state.
     */
    public State getState() {
        return this.state;
    }

    /**
     * Returns the storage flag representing whether this task is done.
     */
    protected String doneFlag() {
        return state.equals(State.MARKED) ? "1" : "0";
    }

    /**
     * Returns the storage-formatted string representation of this task.
     */
    public abstract String toStorageString();
}

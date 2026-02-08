package dyuque;

public abstract class Task {
    /** Description of the task. */
    protected String description;
    /** Whether the task is marked as done. */
    protected Boolean isDone;

    /**
     * Creates a task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markUndone() {
        this.isDone = false;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the storage flag representing whether this task is done.
     */
    protected String doneFlag() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns the storage-formatted string representation of this task.
     */
    public abstract String toStorageString();
}

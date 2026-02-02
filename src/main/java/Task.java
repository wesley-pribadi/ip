public abstract class Task {
    protected String description;
    protected Boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void markDone() {
        this.isDone = true;
    }

    public void markUndone() {
        this.isDone = false;
    }

    public String getDescription() {
        return this.description;
    }

    protected String doneFlag() {
        return isDone ? "1" : "0";
    }

    public abstract String toStorageString();
}

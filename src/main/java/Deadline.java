public class Deadline extends Task {
    private String dueDate;

    public Deadline(String description, String dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "[D]"
                + (isDone ? "[X] " : "[ ] ")
                + description
                + " (by: " + dueDate + ")";
    }

    @Override
    public String toStorageString() {
        return "D | " + doneFlag() + " | " + description + " | " + dueDate;
    }
}

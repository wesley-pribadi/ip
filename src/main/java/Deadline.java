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
}

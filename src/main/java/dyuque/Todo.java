package dyuque;

public class Todo extends Task {

    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]"
                + (isDone ? "[X] " : "[ ] ")
                + description;
    }

    @Override
    public String toStorageString() {
        return "T | " + doneFlag() + " | " + description;
    }
}

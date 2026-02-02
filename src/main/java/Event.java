public class Event extends Task{
    private String fromDate;
    private String toDate;

    public Event(String description, String fromDate, String toDate) {
        super(description);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "[E]"
                + (isDone ? "[X] " : "[ ] ")
                + description
                + " (from: " + fromDate + ")"
                + " (to: " + toDate + ")";
    }

    @Override
    public String toStorageString() {
        return "E | " + doneFlag() + " | " + description + " | " + fromDate + " | " + toDate;
    }
}

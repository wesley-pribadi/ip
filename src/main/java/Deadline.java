import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final LocalDate dueDate;

    public Deadline(String description, String dueDate) throws DyuqueException {
        super(description);
        this.dueDate = parseIsoDate(dueDate, "deadline /by");
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    private static LocalDate parseIsoDate(String dueDateStr, String field) throws DyuqueException {
        try {
            return LocalDate.parse(dueDateStr); // ISO-8601: yyyy-MM-dd
        } catch (DateTimeParseException e) {
            throw new DyuqueException(
                    "Invalid date format for " + field + ". Please use YYYY-MM-DD (e.g., 2026-12-30).",
                    e
            );
        }
    }

    @Override
    public String toString() {
        return "[D]"
                + (isDone ? "[X] " : "[ ] ")
                + description
                + " (by: " + dueDate.format(OUTPUT_FORMAT) + ")";
    }

    @Override
    public String toStorageString() {
        return "D | "
                + doneFlag()
                + " | "
                + description
                + " | "
                + dueDate; // Store dueDate in ISO-8601 format
    }
}

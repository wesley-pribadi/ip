package dyuque;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from disk and saves task updates to disk.
 */
public final class Storage {
    // Consulted ChatGPT when writing this class

    private record ParsedLineData(
            String type,
            String isDone,
            String description,
            String by,
            String from,
            String to) {
    }

    /**
     * Holds the result of loading tasks from the save file.
     *
     * @param tasks             Tasks loaded from the save file.
     * @param wasNewFileCreated Whether a new save file was created during loading,
     *                          i.e. no existing save file was found.
     */
    public record SavefileResult(ArrayList<Task> tasks, boolean wasNewFileCreated) {}

    /** Path to the save file used for persistent storage. */
    private final Path filepath;

    /**
     * Creates a storage handler using the specified relative file path.
     *
     * @param filepath Path to the save file.
     */
    public Storage(Path filepath) {
        this.filepath = filepath;
    }

    /**
     * Ensures the save file and its parent directories exist.
     *
     * @return True if new savefile was created in the process
     * @throws DyuqueException If the save directory or file cannot be created.
     */
    private boolean readOrCreateSavefile() throws DyuqueException {
        try {
            Path parent = filepath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(filepath)) {
                Files.createFile(filepath);
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new FatalDyuqueException("Could not write to savefile:\n  " + filepath, e);
        }
    }

    /**
     * Returns the list of tasks loaded from the save file.
     *
     * @return Tasks loaded from storage.
     * @throws DyuqueException If the save file cannot be created or read, or if any stored line is invalid.
     */
    public SavefileResult load() throws DyuqueException {
        boolean wasNewFileCreated = readOrCreateSavefile();
        ArrayList<Task> tasks = readTasksFromFile();
        return new SavefileResult(tasks, wasNewFileCreated);
    }

    private ArrayList<Task> readTasksFromFile() throws DyuqueException {
        ArrayList<Task> tasks = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(filepath, StandardCharsets.UTF_8)) {
            processFileLines(br, tasks);
        } catch (IOException e) {
            throw new FatalDyuqueException("Could not read from savefile:\n  " + filepath, e);
        }
        return tasks;
    }

    private void processFileLines(BufferedReader reader, ArrayList<Task> tasks) throws IOException, DyuqueException {
        String line;
        int lineNo = 0;
        while ((line = reader.readLine()) != null) {
            lineNo++;
            try {
                processFileLine(line, tasks);
            } catch (FatalDyuqueException e) {
                throw new FatalDyuqueException("Corrupted savefile at line " + lineNo + ":\n", e);
            }
        }
    }

    private void processFileLine(String line, ArrayList<Task> tasks) throws DyuqueException {
        line = line.trim();
        if (!line.isEmpty()) {
            tasks.add(parseLineToTask(line));
        }
    }

    /**
     * Saves the specified tasks to the save file.
     *
     * @param tasks Tasks to save.
     * @throws DyuqueException If the save file cannot be created or written.
     */
    public void save(List<Task> tasks) throws DyuqueException {
        readOrCreateSavefile();
        writeToStorageAsLines(tasks);
    }

    private void writeToStorageAsLines(List<Task> tasks) throws DyuqueException {
        try (BufferedWriter bw = Files.newBufferedWriter(filepath, StandardCharsets.UTF_8)) {
            for (Task task : tasks) {
                bw.write(task.toStorageString());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new FatalDyuqueException("Could not write to savefile:\n  " + filepath, e);
        }
    }

    private Task parseLineToTask(String line) throws DyuqueException {
        ParsedLineData parsedData = extractAndValidateLineData(line);
        return buildTaskFromValidData(parsedData);
    }

    private ParsedLineData extractAndValidateLineData(String line) throws DyuqueException {
        String[] parts = extractPartsFromLine(line);
        validatePartsLength(line, parts);

        // Extract basic fields
        String type = parts[0].trim();
        String isDone = parts[1].trim();
        String description = parts[2].trim();

        validateDoneState(line, isDone);

        // Extract additional fields based on task type
        return switch (type) {
            case "T" -> new ParsedLineData(type, isDone, description, null, null, null);
            case "D" -> {
                validateMinParts(parts, 4, "deadline", line);
                yield new ParsedLineData(type, isDone, description, parts[3].trim(), null, null);
            }
            case "E" -> {
                validateMinParts(parts, 5, "event", line);
                yield new ParsedLineData(type, isDone, description, null, parts[3].trim(), parts[4].trim());
            }
            default -> throw new FatalDyuqueException("Unknown task type: " + type + " \n  in line:\n  " + line);
        };
    }

    private void validateMinParts(String[] parts, int expected, String typeName, String line) throws DyuqueException {
        if (parts.length < expected) {
            throw new FatalDyuqueException("Invalid " + typeName + " format in line:\n  " + line);
        }
    }

    private static void validatePartsLength(String line, String[] parts) throws DyuqueException {
        if (parts.length < 3) {
            throw new FatalDyuqueException("Invalid formatting in line:\n  " + line);
        }
    }

    private static void validateDoneState(String line, String isDone) throws DyuqueException {
        if (!"0".equals(isDone) && !"1".equals(isDone)) {
            throw new FatalDyuqueException("Invalid done flag in line:\n  " + line);
        }
    }

    private static String[] extractPartsFromLine(String line) {
        return line.split("\\s*\\|\\s*", -1);
    }

    private Task buildTaskFromValidData(ParsedLineData data) throws DyuqueException {
        Task task = switch (data.type) {
            case "T" -> new Todo(data.description);
            case "D" -> new Deadline(data.description, data.by);
            case "E" -> new Event(data.description, data.from, data.to);
            default -> throw new FatalDyuqueException("Unexpected task type: " + data.type);
        };

        task.setState("1".equals(data.isDone)
                ? Task.State.MARKED
                : Task.State.UNMARKED);

        return task;
    }
}

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
public class Storage {
    // Consulted ChatGPT when writing this class

    private record ParsedLineData(
            String type,
            String isDone,
            String description,
            String by,
            String from,
            String to) {
    }

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
     * @throws DyuqueException If the save directory or file cannot be created.
     */
    public void ensureStorageExists() throws DyuqueException {
        try {
            Path parent = filepath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(filepath)) {
                Files.createFile(filepath);
            }
        } catch (IOException e) {
            throw new DyuqueException("Could not create data folder/file:\n  " + filepath, e);
        }
    }

    /**
     * Returns the list of tasks loaded from the save file.
     *
     * @return Tasks loaded from storage.
     * @throws DyuqueException If the save file cannot be created or read, or if any stored line is invalid.
     */
    public ArrayList<Task> load() throws DyuqueException {
        ensureStorageExists();
        return readTasksFromFile();
    }

    private ArrayList<Task> readTasksFromFile() throws DyuqueException {
        ArrayList<Task> tasks = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(filepath, StandardCharsets.UTF_8)) {
            processFileLines(br, tasks);
        } catch (IOException e) {
            throw new DyuqueException("Could not read data file:\n  " + filepath, e);
        }
        return tasks;
    }

    private void processFileLines(BufferedReader reader, ArrayList<Task> tasks) throws IOException, DyuqueException {
        String line;
        while ((line = reader.readLine()) != null) {
            processFileLine(line, tasks);
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
        ensureStorageExists();
        writeToStorageAsLines(tasks);
    }

    private void writeToStorageAsLines(List<Task> tasks) throws DyuqueException {
        try (BufferedWriter bw = Files.newBufferedWriter(filepath, StandardCharsets.UTF_8)) {
            for (Task task : tasks) {
                bw.write(task.toStorageString());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new DyuqueException("Could not write data file:\n  " + filepath, e);
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
        validateTaskType(line, type);

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
            default -> throw new DyuqueException("Unknown task type: " + type);
        };
    }

    private void validateMinParts(String[] parts, int expected, String typeName, String line) throws DyuqueException {
        if (parts.length < expected) {
            throw new DyuqueException("Invalid " + typeName + " format in line:\n  " + line);
        }
    }

    private static void validateTaskType(String line, String type) throws DyuqueException {
        if (!"T".equals(type) && !"D".equals(type) && !"E".equals(type)) {
            throw new DyuqueException("Unknown task type in line:\n  " + line);
        }
    }

    private static void validatePartsLength(String line, String[] parts) throws DyuqueException {
        if (parts.length < 3) {
            throw new DyuqueException("Invalid formatting in line:\n  " + line);
        }
    }

    private static void validateDoneState(String line, String isDone) throws DyuqueException {
        if (!"0".equals(isDone) && !"1".equals(isDone)) {
            throw new DyuqueException("Invalid done flag in line:\n  " + line);
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
            default -> throw new IllegalStateException("Unexpected task type: " + data.type);
        };

        task.setState("1".equals(data.isDone)
                ? Task.State.MARKED
                : Task.State.UNMARKED);

        return task;
    }

//    private Task parseLineToTask(String line) throws DyuqueException {
//        /* Format:
//         * T | 0/1 | description
//         * D | 0/1 | description | by
//         * E | 0/1 | description | from | to
//         */
//
//        String[] parts = line.split("\\s*\\|\\s*", -1);
//        if (parts.length < 3) {
//            throw new DyuqueException("Invalid formatting in line:\n  " + line);
//        }
//        String type = parts[0].trim();
//        String doneState = parts[1].trim();
//        String description = parts[2].trim();
//
//        // create task
//        Task task = switch (type) {
//            case "T" -> new Todo(description);
//            case "D" -> {
//                if (parts.length < 4) {
//                    throw new DyuqueException("Invalid deadline format:\n  " + line);
//                }
//                yield new Deadline(description, parts[3].trim());
//            }
//            case "E" -> {
//                if (parts.length < 5) {
//                    throw new DyuqueException("Invalid event format:\n  " + line);
//                }
//                yield new Event(description, parts[3].trim(), parts[4].trim());
//            }
//            default -> throw new DyuqueException("Unknown task type in line:\n  " + line);
//        };
//
//        // set task doneState
//        if ("1".equals(doneState)) {
//            task.setState(Task.State.MARKED);
//        } else if ("0".equals(doneState)) {
//            task.setState(Task.State.UNMARKED);
//        } else {
//            throw new DyuqueException("Invalid done flag in line:\n  " + line);
//        }
//
//        return task;
//    }
}

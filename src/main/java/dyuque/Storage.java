package dyuque;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from disk and saves task updates to disk.
 */
public class Storage {
    // Consulted ChatGPT when writing this class

    /** Path to the save file used for persistent storage. */
    private final Path filePath;

    /**
     * Creates a storage handler using the specified relative file path.
     *
     * @param relativePath Relative path to the save file.
     */
    public Storage(String relativePath) {
        this.filePath = Paths.get(relativePath);
    }

    /**
     * Ensures the save file and its parent directories exist.
     *
     * @throws DyuqueException If the save directory or file cannot be created.
     */
    public void ensureStorageExists() throws DyuqueException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new DyuqueException("Could not create data folder/file:\n  " + filePath, e);
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

        ArrayList<Task> tasks = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                tasks.add(parseLineToTask(line));
            }
        } catch (IOException e) {
            throw new DyuqueException("Could not read data file:\n  " + filePath, e);
        }
        return tasks;
    }

    /**
     * Saves the specified tasks to the save file.
     *
     * @param tasks Tasks to save.
     * @throws DyuqueException If the save file cannot be created or written.
     */
    public void save(List<Task> tasks) throws DyuqueException {
        ensureStorageExists();

        try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            for (Task task : tasks) {
                bw.write(task.toStorageString());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new DyuqueException("Could not write data file:\n  " + filePath, e);
        }
    }

    private Task parseLineToTask(String line) throws DyuqueException {
        /* Format:
         * T | 0/1 | description
         * D | 0/1 | description | by
         * E | 0/1 | description | from | to
         */

        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3) {
            throw new DyuqueException("Invalid formatting in line:\n  " + line);
        }
        String type = parts[0].trim();
        String doneState = parts[1].trim();
        String description = parts[2].trim();

        // create task
        Task task = switch (type) {
            case "T" -> new Todo(description);
            case "D" -> {
                if (parts.length < 4) {
                    throw new DyuqueException("Invalid deadline format:\n  " + line);
                }
                yield new Deadline(description, parts[3].trim());
            }
            case "E" -> {
                if (parts.length < 5) {
                    throw new DyuqueException("Invalid event format:\n  " + line);
                }
                yield new Event(description, parts[3].trim(), parts[4].trim());
            }
            default -> throw new DyuqueException("Unknown task type in line:\n  " + line);
        };

        // set task doneState
        if ("1".equals(doneState)) {
            task.setState(Task.State.MARKED);
        } else if ("0".equals(doneState)) {
            task.setState(Task.State.UNMARKED);
        } else {
            throw new DyuqueException("Invalid done flag in line:\n  " + line);
        }

        return task;
    }
}

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    // Consulted ChatGPT when writing this class
    private final Path filePath;

    public Storage(String relativePath) {
        this.filePath = Paths.get(relativePath);
    }

    public void ensureExists() throws DyuqueException {
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

    public ArrayList<Task> load() throws DyuqueException {
        ensureExists();

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

    public void save(List<Task> tasks) throws DyuqueException {
        ensureExists();

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
            task.markDone();
        } else if ("0".equals(doneState)) {
            task.markUndone();
        } else {
            throw new DyuqueException("Invalid done flag in line:\n  " + line);
        }

        return task;
    }
}

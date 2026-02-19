package dyuque;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DyuqueExecuteCommandTest {

    @Test
    public void executeCommand_exit_showsGoodbyeAndReturnsMessage(@TempDir Path tempDir) throws Exception {
        Parser parser = new Parser();
        Storage storage = new Storage(tempDir.resolve("dyuque.txt"));
        TaskList taskList = new TaskList(storage.load(), storage);
        Dyuque dyuque = new Dyuque(parser, taskList);

        CommandArgumentPair pair = parser.parseCommand("bye");
        String output = dyuque.executeCommand(pair);

        assertNotNull(output);
        assertFalse(output.isBlank());
        assertTrue(output.contains(Ui.showGoodbye()));
    }

    @Test
    public void executeCommand_todo_addsTaskAndReturnsMessage(@TempDir Path tempDir) throws Exception {
        Parser parser = new Parser();
        Storage storage = new Storage(tempDir.resolve("dyuque.txt"));
        TaskList taskList = new TaskList(storage.load(), storage);
        Dyuque dyuque = new Dyuque(parser, taskList);

        CommandArgumentPair pair = parser.parseCommand("todo read book");
        String output = dyuque.executeCommand(pair);

        assertNotNull(output);
        assertTrue(output.contains("read book"));
        assertEquals(1, taskList.size());
    }

    @Test
    public void executeCommand_mark_nonInteger_throwsDyuqueExceptionWithNumberFormatCause(@TempDir Path tempDir) throws Exception {
        Parser parser = new Parser();
        Storage storage = new Storage(tempDir.resolve("dyuque.txt"));
        TaskList taskList = new TaskList(storage.load(), storage);
        Dyuque dyuque = new Dyuque(parser, taskList);

        CommandArgumentPair pair = parser.parseCommand("mark abc");

        DyuqueException ex = assertThrows(DyuqueException.class, () -> dyuque.executeCommand(pair));
        assertInstanceOf(NumberFormatException.class, ex.getCause());
        assertTrue(ex.getMessage().contains("Expected integer"));
    }

    @Test
    public void parseCommand_mark_nonInteger_stillParses() throws Exception {
        Parser parser = new Parser();

        CommandArgumentPair pair = parser.parseCommand("mark abc");

        assertNotNull(pair);
        assertEquals(Dyuque.Command.MARK, pair.command());
        assertArrayEquals(new String[]{"abc"}, pair.argument());
    }
}

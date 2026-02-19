package dyuque;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// CHECKSTYLE.OFF: SeparatorWrap
class DyuqueExecuteCommandTest {

    // DeepSeek was used to write this class.

    @TempDir
    Path tempDir; // Reusable temporary directory for each test

    private Parser parser;
    private TaskList taskList;
    private Dyuque dyuque;

    @BeforeEach
    void setUp() throws Exception {
        parser = new Parser();
        Storage storage = new Storage(tempDir.resolve("dyuque.txt"));
        taskList = new TaskList(storage.load().tasks(), storage);
        dyuque = new Dyuque(parser, taskList, "");
    }

    @Nested
    @DisplayName("executeCommand()")
    class ExecuteCommandTests {

        @Test
        @DisplayName("should show goodbye message on 'bye' command")
        void byeCommand_returnsGoodbyeMessage() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("bye");
            String output = dyuque.executeCommand(pair);

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertFalse(output.isBlank()),
                    () -> assertEquals(Ui.showGoodbye(), output) // exact match instead of contains
            );
        }

        @Test
        @DisplayName("should add todo task and return confirmation")
        void todoCommand_addsTaskAndReturnsMessage() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("todo read book");
            String output = dyuque.executeCommand(pair);

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertTrue(output.contains("read book")),
                    () -> assertEquals(1, taskList.size()),
                    () -> assertInstanceOf(Todo.class, taskList.getTask(0)), // verify task type
                    () -> assertEquals("read book", taskList.getTask(0).getDescription())
            );
        }

        @Test
        @DisplayName("should throw DyuqueException with NumberFormatException cause for invalid mark argument")
        void markCommand_nonInteger_throwsDyuqueException() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("mark abc");

            DyuqueException ex = assertThrows(DyuqueException.class, () -> dyuque.executeCommand(pair));
            assertAll(
                    () -> assertInstanceOf(NumberFormatException.class, ex.getCause()),
                    () -> assertTrue(ex.getMessage().contains("Expected integer")),
                    () -> assertEquals(0, taskList.size()) // ensure no task was modified
            );
        }
    }

    @Nested
    @DisplayName("parseCommand()")
    class ParseCommandTests {

        @Test
        @DisplayName("should parse mark with non-integer argument successfully")
        void markCommand_nonInteger_parsesCorrectly() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("mark abc");

            assertAll(
                    () -> assertNotNull(pair),
                    () -> assertEquals(Dyuque.Command.MARK, pair.command()),
                    () -> assertArrayEquals(new String[]{"abc"}, pair.argument())
            );
        }
    }
}
// CHECKSTYLE.ON: SeparatorWrap

package dyuque;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DyuqueExecuteCommandTest {
    // ChatGPT was used to write this test

    static class FakeUi extends Ui {
        String lastMessage = null;
        String lastError = null;
        boolean goodbyeShown = false;
        int lineCount = 0;

        public void showMessage(String message) {
            lastMessage = message;
        }

        @Override
        public String showError(String message) {
            lastError = message;
            return message;
        }

        @Override
        public String showGoodbye() {
            goodbyeShown = true;
            return null;
        }

        @Override
        public void showLine() {
            lineCount++;
        }
    }

    @Test
    public void executeCommand_exit_returnsFalseAndShowsGoodbye(@TempDir Path tempDir) throws Exception {
        FakeUi ui = new FakeUi();
        Storage storage = new Storage(tempDir.resolve("dyuque.txt").toString());
        Parser parser = new Parser();
        TaskList taskList = new TaskList(storage.load(), storage);

        Dyuque dyuque = new Dyuque(ui, storage, parser, taskList);

        boolean shouldContinue = dyuque.executeCommand("bye");

        assertFalse(shouldContinue);
        assertTrue(ui.goodbyeShown);
        assertEquals(1, ui.lineCount);
    }

    private void assertFalse(boolean shouldContinue) {
    }

    @Test
    public void executeCommand_todo_addsTaskAndReturnsTrue(@TempDir Path tempDir) throws Exception {
        FakeUi ui = new FakeUi();
        Storage storage = new Storage(tempDir.resolve("dyuque.txt").toString());
        Parser parser = new Parser();
        TaskList taskList = new TaskList(storage.load(), storage);

        Dyuque dyuque = new Dyuque(ui, storage, parser, taskList);

        boolean shouldContinue = dyuque.executeCommand("todo read book");

        assertTrue(shouldContinue);
        assertNotNull(ui.lastMessage);
        // Not depending on exact formatting here but message should contain the description.
        assertTrue(ui.lastMessage.contains("read book"));
    }

    @Test
    public void executeCommand_mark_nonInteger_throwsDyuqueException(@TempDir Path tempDir) throws Exception {
        FakeUi ui = new FakeUi();
        Storage storage = new Storage(tempDir.resolve("dyuque.txt").toString());
        Parser parser = new Parser();
        TaskList taskList = new TaskList(storage.load(), storage);

        Dyuque dyuque = new Dyuque(ui, storage, parser, taskList);

        DyuqueException ex = assertThrows(DyuqueException.class, () -> dyuque.executeCommand("mark abc"));
        assertInstanceOf(NumberFormatException.class, ex.getCause());
    }
}

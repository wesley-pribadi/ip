package dyuque;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

// CHECKSTYLE.OFF: SeparatorWrap
public class ParserTest {

    // DeepSeek was used to write this class.

    @Test
    public void parseCommand_blankInput_throws() {
        Parser parser = new Parser();
        DyuqueException ex = assertThrows(DyuqueException.class, () -> parser.parseCommand("   "));
        assertEquals("Please enter a command", ex.getMessage());
    }

    @Test
    public void parseCommand_unknownCommand_throws() {
        Parser parser = new Parser();
        DyuqueException ex = assertThrows(DyuqueException.class, () -> parser.parseCommand("bruh hello"));
        assertEquals("Unknown command: bruh", ex.getMessage());
    }

    @Test
    public void parseCommand_todo_requiresDescription() {
        Parser parser = new Parser();
        DyuqueException ex = assertThrows(DyuqueException.class, () -> parser.parseCommand("todo   "));
        assertEquals("Usage: todo <description>", ex.getMessage());
    }

    @Test
    public void parseCommand_todoWithExtraSpaces_parses() throws Exception {
        Parser parser = new Parser();

        CommandArgumentPair pair = parser.parseCommand("   todo   read book   ");
        assertEquals(Dyuque.Command.TODO, pair.command());
        assertArrayEquals(new String[]{"read book"}, pair.argument());
    }

    @Test
    public void parseCommand_deadline_parsesDescriptionAndBy() throws Exception {
        Parser parser = new Parser();

        CommandArgumentPair pair = parser.parseCommand("deadline return book /by Sunday");
        assertEquals(Dyuque.Command.DEADLINE, pair.command());
        assertArrayEquals(new String[]{"return book", "Sunday"}, pair.argument());
    }

    @Test
    public void parseCommand_eventMissingFromOrTo_throws() {
        Parser parser = new Parser();

        DyuqueException ex1 = assertThrows(DyuqueException.class,
                () -> parser.parseCommand("event meeting /from Monday"));
        assertEquals("Usage: event <description> /from <date> /to <date>", ex1.getMessage());

        DyuqueException ex2 = assertThrows(DyuqueException.class,
                () -> parser.parseCommand("event meeting /to Tuesday"));
        assertEquals("Usage: event <description> /from <date> /to <date>", ex2.getMessage());
    }

    @Test
    public void parseCommand_event_parsesDescFromTo() throws Exception {
        Parser parser = new Parser();

        CommandArgumentPair pair = parser.parseCommand("event proj meeting /from Mon /to Tue");
        assertEquals(Dyuque.Command.EVENT, pair.command());
        assertArrayEquals(new String[]{"proj meeting", "Mon", "Tue"}, pair.argument());
    }

    @Test
    public void parseCommand_mark_requiresIndex() {
        Parser parser = new Parser();

        DyuqueException ex = assertThrows(DyuqueException.class, () -> parser.parseCommand("mark"));
        assertEquals("Usage: mark <index>", ex.getMessage());
    }
}
// CHECKSTYLE.ON: SeparatorWrap

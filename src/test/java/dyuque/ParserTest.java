package dyuque;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

// CHECKSTYLE.OFF: SeparatorWrap
// This was triggering on all the single-line lambda expressions
public class ParserTest {
    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    @Nested
    @DisplayName("parseCommand() input validation")
    class InputValidationTests {

        @Test
        @DisplayName("should throw on blank input")
        void blankInput_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("   "));
            assertEquals("Please enter a command", ex.getMessage());
        }

        @Test
        @DisplayName("should throw on empty string")
        void emptyString_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand(""));
            assertEquals("Please enter a command", ex.getMessage());
        }

        @Test
        @DisplayName("should throw on unknown command")
        void unknownCommand_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("bruh hello"));
            assertEquals("Unknown command: bruh", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("parseCommand() for commands with no arguments")
    class NoArgumentCommandsTests {

        @Test
        @DisplayName("should parse 'help'")
        void helpCommand() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("help");
            assertAll(
                    () -> assertEquals(Dyuque.Command.HELP, pair.command()),
                    () -> assertArrayEquals(new String[0], pair.argument())
            );
        }

        @Test
        @DisplayName("should parse 'list'")
        void listCommand() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("list");
            assertAll(
                    () -> assertEquals(Dyuque.Command.LIST, pair.command()),
                    () -> assertArrayEquals(new String[0], pair.argument())
            );
        }

        @Test
        @DisplayName("should parse 'undo'")
        void undoCommand() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("undo");
            assertAll(
                    () -> assertEquals(Dyuque.Command.UNDO, pair.command()),
                    () -> assertArrayEquals(new String[0], pair.argument())
            );
        }

        @Test
        @DisplayName("should parse 'bye' (EXIT)")
        void exitCommand() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("bye");
            assertAll(
                    () -> assertEquals(Dyuque.Command.EXIT, pair.command()),
                    () -> assertArrayEquals(new String[0], pair.argument())
            );
        }

        @Test
        @DisplayName("should ignore extra spaces around command")
        void noArgCommand_withSpaces() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("  help  ");
            assertAll(
                    () -> assertEquals(Dyuque.Command.HELP, pair.command()),
                    () -> assertArrayEquals(new String[0], pair.argument())
            );
        }
    }

    @Nested
    @DisplayName("parseCommand() for single-argument commands")
    class SingleArgCommandsTests {

        @Test
        @DisplayName("should parse 'todo' with description")
        void todo_withDescription() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("todo read book");
            assertAll(
                    () -> assertEquals(Dyuque.Command.TODO, pair.command()),
                    () -> assertArrayEquals(new String[]{"read book"}, pair.argument())
            );
        }

        @Test
        @DisplayName("should handle extra spaces in todo command")
        void todo_withExtraSpaces() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("   todo   read book   ");
            assertAll(
                    () -> assertEquals(Dyuque.Command.TODO, pair.command()),
                    () -> assertArrayEquals(new String[]{"read book"}, pair.argument())
            );
        }

        @Test
        @DisplayName("should throw on todo without description")
        void todo_missingDescription_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("todo   "));
            assertEquals("Usage: todo <description>", ex.getMessage());
        }

        @Test
        @DisplayName("should parse 'delete' with index")
        void delete_withIndex() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("delete 3");
            assertAll(
                    () -> assertEquals(Dyuque.Command.DELETE, pair.command()),
                    () -> assertArrayEquals(new String[]{"3"}, pair.argument())
            );
        }

        @Test
        @DisplayName("should throw on delete without index")
        void delete_missingIndex_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("delete"));
            assertEquals("Usage: delete <index>", ex.getMessage());
        }

        @Test
        @DisplayName("should parse 'mark' with index")
        void mark_withIndex() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("mark 5");
            assertAll(
                    () -> assertEquals(Dyuque.Command.MARK, pair.command()),
                    () -> assertArrayEquals(new String[]{"5"}, pair.argument())
            );
        }

        @Test
        @DisplayName("should throw on mark without index")
        void mark_missingIndex_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("mark"));
            assertEquals("Usage: mark <index>", ex.getMessage());
        }

        @Test
        @DisplayName("should parse 'unmark' with index")
        void unmark_withIndex() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("unmark 2");
            assertAll(
                    () -> assertEquals(Dyuque.Command.UNMARK, pair.command()),
                    () -> assertArrayEquals(new String[]{"2"}, pair.argument())
            );
        }

        @Test
        @DisplayName("should parse 'find' with keyword")
        void find_withKeyword() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("find book");
            assertAll(
                    () -> assertEquals(Dyuque.Command.FIND, pair.command()),
                    () -> assertArrayEquals(new String[]{"book"}, pair.argument())
            );
        }
    }

    @Nested
    @DisplayName("parseCommand() for deadline command")
    class DeadlineCommandTests {

        @Test
        @DisplayName("should parse valid deadline")
        void validDeadline() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("deadline return book /by Sunday");
            assertAll(
                    () -> assertEquals(Dyuque.Command.DEADLINE, pair.command()),
                    () -> assertArrayEquals(new String[]{"return book", "Sunday"}, pair.argument())
            );
        }

        @Test
        @DisplayName("should handle extra spaces around /by")
        void validDeadline_withExtraSpaces() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("deadline   return book   /by   Sunday   ");
            assertAll(
                    () -> assertEquals(Dyuque.Command.DEADLINE, pair.command()),
                    () -> assertArrayEquals(new String[]{"return book", "Sunday"}, pair.argument())
            );
        }

        @Test
        @DisplayName("should throw if /by missing")
        void deadline_missingBy_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("deadline return book"));
            assertEquals("Usage: deadline <description> /by <date>", ex.getMessage());
        }

        @Test
        @DisplayName("should throw if description blank")
        void deadline_blankDescription_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("deadline   /by Sunday"));
            assertEquals("Description or date cannot be blank", ex.getMessage());
        }

        @Test
        @DisplayName("should throw if date blank")
        void deadline_blankDate_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("deadline return book /by   "));
            assertEquals("Description or date cannot be blank", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("parseCommand() for event command")
    class EventCommandTests {

        @Test
        @DisplayName("should parse valid event")
        void validEvent() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("event proj meeting /from Mon /to Tue");
            assertAll(
                    () -> assertEquals(Dyuque.Command.EVENT, pair.command()),
                    () -> assertArrayEquals(new String[]{"proj meeting", "Mon", "Tue"}, pair.argument())
            );
        }

        @Test
        @DisplayName("should handle extra spaces around /from and /to")
        void validEvent_withExtraSpaces() throws DyuqueException {
            CommandArgumentPair pair = parser.parseCommand("event   proj meeting   /from   Mon   /to   Tue   ");
            assertAll(
                    () -> assertEquals(Dyuque.Command.EVENT, pair.command()),
                    () -> assertArrayEquals(new String[]{"proj meeting", "Mon", "Tue"}, pair.argument())
            );
        }

        @Test
        @DisplayName("should throw if /from missing")
        void event_missingFrom_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("event meeting /to Tue"));
            assertEquals("Usage: event <description> /from <date> /to <date>", ex.getMessage());
        }

        @Test
        @DisplayName("should throw if /to missing")
        void event_missingTo_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("event meeting /from Mon"));
            assertEquals("Usage: event <description> /from <date> /to <date>", ex.getMessage());
        }

        @Test
        @DisplayName("should throw if description blank")
        void event_blankDescription_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("event   /from Mon /to Tue"));
            assertEquals("Usage: event <description> /from <date> /to <date>", ex.getMessage());
        }

        @Test
        @DisplayName("should throw if from blank")
        void event_blankFrom_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("event meeting /from   /to Tue"));
            assertEquals("Usage: event <description> /from <date> /to <date>", ex.getMessage());
        }

        @Test
        @DisplayName("should throw if to blank")
        void event_blankTo_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("event meeting /from Mon /to   "));
            assertEquals("Usage: event <description> /from <date> /to <date>", ex.getMessage());
        }

        @Test
        @DisplayName("should throw if /from appears after /to")
        void event_reversedOrder_throws() {
            DyuqueException ex = assertThrows(DyuqueException.class,
                    () -> parser.parseCommand("event meeting /to Tue /from Mon"));
            assertEquals("Usage: event <description> /from <date> /to <date>", ex.getMessage());
        }
    }
}
// CHECKSTYLE.ON: SeparatorWrap

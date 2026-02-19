# Dyuque

Dyuque is a desktop task manager chatbot built with JavaFX. It supports todos, deadlines, and events, with persistent local storage.

### For the **user-facing guide**, see the [User Guide](https://wesley-pribadi.github.io/ip/).

---

## Setting Up for Development

### Prerequisites

- **JDK 17** (other versions unsupported)
- **IntelliJ IDEA** (latest version)

### Steps

1. Open IntelliJ. If a project is already open, go to `File` → `Close Project` to return to the welcome screen.
2. Click `Open`, select the project directory, and click `OK`. Accept any further defaults.
3. Configure the project to use **JDK 17**: [IntelliJ SDK setup guide](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk). Set **Project language level** to `SDK default`.
4. To run the app, locate `src/main/java/dyuque/Launcher.java`, right-click it, and choose `Run Launcher.main()`.

> **Note:** Do not rename or move the `src/main/java` folder — Gradle expects Java sources to be at this path.

---

## Building and Running

The project uses **Gradle** with the Shadow plugin to produce a fat jar.
Use the wrapper scripts (`gradlew` on Unix/macOS, `gradlew.bat` on Windows), no local Gradle installation is required.
Refer to `build.gradle` for configuration.

### Run the app
```
./gradlew run
```

### Build a fat jar
```
./gradlew clean shadowJar
```
The output jar will be at `build/libs/dyuque.jar`. Run it with:
```
java -jar build/libs/dyuque.jar
```

---

## Testing

Tests are written with **JUnit 5** and cover command parsing and execution logic.

```
./gradlew clean test
```

Test results are printed to the console.

---

## Code Style

Checkstyle is configured using the rules in `config/checkstyle/`.
It was adapted from [addressbook-level3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle)
with modifications to switch-case indentations, namely setting `case` indentations from `0` to `4`:
```xml
<module name="Indentation">
  <property name="caseIndent" value="4" />  <!-- for Lambda-style. Suppress traditional cases manually -->
  <property name="throwsIndent" value="8" />
</module>
```
This was done because (from my understanding) Checkstyle cannot differentiate between traditional and lambda-style
switch-case statements when it comes to indentation, as requested in the
[Coding Standard](https://se-education.org/guides/conventions/java/intermediate.html#:~:text=%7D-,Lambda%2Dstyle,-switch%20statements/expressions).
Relevant issue can be found [here](https://github.com/NUS-CS2103-AY2526-S2/forum/issues/87#issuecomment-3821268628).

<br>
To check for violations:

```
./gradlew checkstyleMain checkstyleTest
```

<br>
Suppression rules are defined in `config/checkstyle/suppressions.xml`.

Some classes also have inline suppressions, notably single-line lambda expressions.

---

## Project Structure

```
src/
├── main/
│   ├── java/dyuque/
│   │   ├── Launcher.java        # Entry point (JavaFX workaround)
│   │   ├── Main.java            # JavaFX Application subclass
│   │   ├── MainWindow.java      # Primary window controller (FXML)
│   │   ├── Dyuque.java          # Core chatbot logic and command dispatch
│   │   ├── Parser.java          # Parses raw input into commands
│   │   ├── TaskList.java        # In-memory task management
│   │   ├── Storage.java         # Save file read/write
│   │   ├── Ui.java              # Formats user-facing messages
│   │   ├── Task.java            # Abstract base task
│   │   ├── Todo.java            # Task with no date
│   │   ├── Deadline.java        # Task with a due date
│   │   ├── Event.java           # Task with a date range
│   │   └── ...                  # Exceptions, helpers
│   └── resources/
│       ├── images/              # UI icons
│       └── view/                # FXML layouts and CSS
└── test/
    └── java/dyuque/
        ├── DyuqueExecuteCommandTest.java
        └── ParserTest.java
```

---

## Code Reuse and Credits
* AI was used for a large portion of generating Javadocs to help me learn best practices and save time.
* AI was used extensively in certain classes like Parser, Storage, and JavaFX classes.
* AI was also used in general for refining my high-level design plans and troubleshooting.
* Reused code blocks are tagged with `@@author wesley-pribadi-reused`.

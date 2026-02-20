package dyuque;

import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {

    public static final int EXIT_TRANSITION_DELAY = 1500;

    // Helper record to encapsulate response data
    private record ResponseResult(
                String response,
                boolean isExitRequested,
                String errorMessage) {
    }

    // Images generated via Claude (SVG converted to PNG)
    private final Image userImage = new Image(Objects.requireNonNull(
            getClass().getResource("/images/user-icon.png")).toExternalForm());
    private final Image dyuqueImage = new Image(Objects.requireNonNull(
            getClass().getResource("/images/dyuque-icon.png")).toExternalForm());

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox messageContainer;
    @FXML
    private TextArea userInput;
    @FXML
    private Button sendButton;

    private Dyuque dyuque;

    /**
     * Injects the Dyuque instance and displays the welcome message.
     *
     * @param dyuque The Dyuque instance to use
     */
    public void setDyuque(Dyuque dyuque) {
        this.dyuque = dyuque;

        String notice = dyuque.getSavefileCreatedNotice();
        if (!notice.isBlank()) {
            appendDyuqueMessage(notice);
        }

        appendDyuqueMessage(Ui.showWelcome());

        Platform.runLater(() -> userInput.requestFocus());
    }

    /**
     * Initializes the controller after the FXML layout has been loaded.
     * <p>
     * Configures the scroll pane to fit its content to the available width,
     * and sets up a key press handler on the input field so that pressing
     * {@code Enter} submits the input, while {@code Shift+Enter} inserts a newline.
     */
    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);

        // @@author wesley-pribadi-reused
        // Autoscroll downward after the new messages have been added
        // See https://github.com/NUS-CS2103-AY2526-S2/forum/issues/157
        messageContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        // Handles enter-key
        // Needed after switching from TextField to TextArea
        userInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                event.consume();
                handleUserInput();
            }
        });
    }

    /**
     * Creates two message boxes, one echoing user input and the other containing Dyuque's reply
     * and then appends them to the message container. Then clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        if (dyuque == null) {
            return;
        }

        String input = processUserInput();
        if (input == null) {
            return;
        }
        processDyuqueOutput(input);
    }

    private String processUserInput() {
        String input = getAndValidateUserInput();
        if (input == null) {
            return null;
        }

        updateUiWithUserInput(input);

        return input;
    }

    private String getAndValidateUserInput() {
        String input = userInput.getText();
        String sanitizedInput = Parser.sanitizeNewlinesAndBlanks(input);

        if (sanitizedInput.isBlank()) {
            userInput.clear();
            return null;
        }
        return sanitizedInput;
    }

    private void updateUiWithUserInput(String input) {
        appendUserMessage(input);
        userInput.clear();
    }

    private void processDyuqueOutput(String input) {
        ResponseResult response = generateDyuqueResponse(input);
        handleDyuqueResponse(response);
    }

    private ResponseResult generateDyuqueResponse(String input) {
        try {
            String response = dyuque.getResponse(input);
            boolean isExitRequested = dyuque.isExitRequested();
            return new ResponseResult(response, isExitRequested, null);
        } catch (FatalDyuqueException e) {
            System.err.println("Caught FatalDyuqueException: " + e.getMessage());
            System.err.flush();
            ErrorDialogs.showFatalDialogAndExit(e.getMessage());
            return new ResponseResult(null, false, null);
        } catch (DyuqueException e) {
            return new ResponseResult(null, false, e.getMessage());
        }
    }

    private void handleDyuqueResponse(ResponseResult response) {
        if (response.errorMessage != null) {
            appendDyuqueMessage(Ui.showError(response.errorMessage));
        } else {
            appendDyuqueMessage(response.response);
            if (response.isExitRequested) {
                exitGracefully();
            }
        }
    }

    private void appendUserMessage(String message) {
        try {
            messageContainer.getChildren().add(MessageBox.getUserMessage(message, userImage));
        } catch (IllegalStateException e) {
            // For missing MessageBox.fxml
            ErrorDialogs.showFatalDialogAndExit(e.getMessage());
        }
    }

    private void appendDyuqueMessage(String message) {
        try {
            messageContainer.getChildren().add(MessageBox.getDyuqueMessage(message, dyuqueImage));
        } catch (IllegalStateException e) {
            // For missing MessageBox.fxml
            ErrorDialogs.showFatalDialogAndExit(e.getMessage());
        }
    }

    private void exitGracefully() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        // getResponse handles Ui.showGoodbye()
        PauseTransition delay = new PauseTransition(Duration.millis(EXIT_TRANSITION_DELAY));
        delay.setOnFinished(e -> Platform.exit());
        delay.play();
    }
}

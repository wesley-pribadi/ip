package dyuque;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Dyuque dyuque;

    private final Image userImage = new Image(Objects.requireNonNull(
            this.getClass().getResourceAsStream("/images/DaUser.png")));
    private final Image dukeImage = new Image(Objects.requireNonNull(
            this.getClass().getResourceAsStream("/images/DaDuke.png")));

    /** Injects the Dyuque instance */
    public void setDyuque(Dyuque dyuque) {
        // Initialise Dyuque instance
        this.dyuque = dyuque;

        // Show welcome message
        String welcome = dyuque.getWelcomeMessage();
        dialogContainer.getChildren().add(
                DialogBox.getDyuqueDialog(welcome, dukeImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        dialogContainer.getChildren().add(
                DialogBox.getUserDialog(input, userImage)
        );

        try {
            String response = dyuque.getResponse(input);

            dialogContainer
                    .getChildren()
                    .add(DialogBox.getDyuqueDialog(response, dukeImage));

            if (!dyuque.getShouldContinueExecution()) {
                userInput.setDisable(true);
                sendButton.setDisable(true);

                PauseTransition delay = new PauseTransition(Duration.millis(600));
                delay.setOnFinished(e -> ((Stage) this.getScene().getWindow()).close());
                delay.play();
                Platform.exit();
            }
        } catch (DyuqueException e) {
            dialogContainer
                    .getChildren()
                    .add(DialogBox.getDyuqueDialog(Ui.showError(e.getMessage()), dukeImage));
        }

        userInput.clear();

        // @@author wesley-pribadi-reused
        // Autoscroll downward after the new messages have been added
        // See https://github.com/NUS-CS2103-AY2526-S2/forum/issues/157
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

}

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

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

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
            String response = dyuque.executeCommand(input);

            dialogContainer.getChildren().add(
                    DialogBox.getDyuqueDialog(response, dukeImage)
            );

            if (!dyuque.getShouldContinueExecution()) {
                userInput.setDisable(true);
                sendButton.setDisable(true);

                PauseTransition delay = new PauseTransition(Duration.millis(600));
                delay.setOnFinished(e -> ((Stage) this.getScene().getWindow()).close());
                delay.play();
                Platform.exit();
            }

        } catch (DyuqueException e) {
            dialogContainer.getChildren().add(
                    DialogBox.getDyuqueDialog("Error: " + e.getMessage(), dukeImage)
            );
        }

        userInput.clear();
    }

}

package dyuque;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

/**
 * Utility class for displaying application-level dialog windows.
 *
 * <p>This class provides helper methods for showing modal dialogs
 * such as fatal error notifications. It centralizes dialog creation
 * logic to avoid duplication across different UI controllers.</p>
 *
 * <p>All dialogs created by this class are blocking and must be
 * invoked on the JavaFX Application Thread.</p>
 */
public final class ErrorDialogs {
    private ErrorDialogs() {}

    /**
     * Displays a modal, resizable fatal error dialog and terminates the application.
     *
     * <p>The dialog contains the specified title, header text, and message body.
     * It blocks user interaction with the application until dismissed.
     * After the user closes the dialog, {@link javafx.application.Platform#exit()}
     * is invoked to terminate the application.</p>
     *
     * @param message The detailed error message to display.
     */
    public static void showFatalDialogAndExit(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fatal Error");
        alert.setHeaderText("Dyuque encountered a fatal error and needs to quit");

        throwDialog(message, alert);
    }

    private static void throwDialog(String message, Alert alert) {
        TextArea textArea = new TextArea(message);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setContent(textArea);
        alert.setResizable(true);

        alert.showAndWait();
        Platform.exit();
    }
}

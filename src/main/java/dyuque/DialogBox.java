package dyuque;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
@SuppressWarnings("unused")
public class DialogBox extends HBox {
    final int PICTURE_CORNER_RADIUS = 10;
    final String FXML_FILEPATH = "/view/DialogBox.fxml";

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private StackPane bubble;

    private DialogBox(String text, Image img) {
        loadFxml();
        initializeContent(text, img);
        configureLayout();
        applyRoundCornersToPicture();
    }

    private void loadFxml() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource(FXML_FILEPATH));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + FXML_FILEPATH, e);
        }
    }

    private void initializeContent(String text, Image img) {
        dialog.setText(text);
        displayPicture.setImage(img);
    }

    private void configureLayout() {
        configureBubbleWidth();
        configureBubbleHeight();
    }

    private void configureBubbleWidth() {
        // Bubble width should resize to content
        dialog.setMaxWidth(Double.MAX_VALUE);
        bubble.setMaxWidth(Double.MAX_VALUE);
    }

    private void configureBubbleHeight() {
        // Bubble height should resize to content
        bubble.setMinHeight(Region.USE_PREF_SIZE);
        bubble.setMaxHeight(Region.USE_PREF_SIZE);
    }

    private void applyRoundCornersToPicture() {
        // Rounded profile picture (10px)
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(displayPicture.fitWidthProperty());
        clip.heightProperty().bind(displayPicture.fitHeightProperty());
        clip.setArcWidth(PICTURE_CORNER_RADIUS);
        clip.setArcHeight(PICTURE_CORNER_RADIUS);
        displayPicture.setClip(clip);
    }

    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.applyUserStyling();
        return dialogBox;
    }

    private void applyUserStyling() {
        bubble.getStyleClass().add("user-bubble");
        setAlignment(Pos.TOP_RIGHT);
    }

    public static DialogBox getDyuqueDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.applyDyuqueStyling();
        return dialogBox;
    }

    private void applyDyuqueStyling() {
        flip();
        bubble.getStyleClass().add("dyuque-bubble");
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }
}

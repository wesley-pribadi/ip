package dyuque;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Dyuque using FXML.
 */
public class Main extends Application {
    private static final String FXML_FILEPATH = "/view/MainWindow.fxml";
    private static final String CSS_FILEPATH = "/view/style.css";

    public Main() {
    }

    @Override
    public void start(Stage stage) {
        try {
            Dyuque dyuque = Dyuque.initialiseDefaults();

            FXMLLoader loader = new FXMLLoader(getResource(FXML_FILEPATH));
            AnchorPane ap = loader.load();

            MainWindow controller = loader.getController();
            controller.setDyuque(dyuque);

            Scene scene = new Scene(ap);
            scene.getStylesheets().add(getResource(CSS_FILEPATH).toExternalForm());

            configureStage(stage, scene);
            stage.show();
        } catch (DyuqueException e) {
            Dialogs.showFatalDialogAndExit(e.getMessage());
            Platform.exit();
        } catch (IOException e) {
            Dialogs.showFatalDialogAndExit("Failed to load GUI resources:\n" + e.getMessage());
            Platform.exit();
        }
    }

    // Helper: Converts the null-return of getResource into a proper IOException
    private URL getResource(String path) throws IOException {
        URL resource = Main.class.getResource(path);
        if (resource == null) {
            throw new IOException("Resource not found: " + path);
        }
        return resource;
    }

    private static void configureStage(Stage stage, Scene scene) {
        stage.setTitle("Dyuque");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setWidth(700);
        stage.setHeight(900);
    }
}

package uptc.edu.co.file_explorer_doj;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/uptc/edu/co/file_explorer_doj/fxml/main.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 700);
        scene.getStylesheets().add(
            getClass().getResource("/uptc/edu/co/file_explorer_doj/css/theme.css").toExternalForm());
        stage.setTitle("File Explorer");
        stage.getIcons().add(loadFxImage("/uptc/edu/co/file_explorer_doj/images/folder32.png"));
        setTaskbarIcon("/uptc/edu/co/file_explorer_doj/images/folder256.png");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.show();
    }

    private Image loadFxImage(String resourcePath) {
        return new Image(getClass().getResourceAsStream(resourcePath));
    }

    private void setTaskbarIcon(String resourcePath) {
        if (!Taskbar.isTaskbarSupported()) {
            return;
        }

        URL resourceUrl = getClass().getResource(resourcePath);
        if (resourceUrl == null) {
            return;
        }

        try {
            Taskbar.getTaskbar().setIconImage(Toolkit.getDefaultToolkit().getImage(resourceUrl));
        } catch (UnsupportedOperationException | SecurityException ignored) {
            // Best-effort only; some platforms block taskbar icon changes.
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

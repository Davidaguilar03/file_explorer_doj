package uptc.edu.co.file_explorer_doj;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/uptc/edu/co/file_explorer_doj/fxml/main.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 700);
        scene.getStylesheets().add(
            getClass().getResource("/uptc/edu/co/file_explorer_doj/css/theme.css").toExternalForm());
        stage.setTitle("File Explorer");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

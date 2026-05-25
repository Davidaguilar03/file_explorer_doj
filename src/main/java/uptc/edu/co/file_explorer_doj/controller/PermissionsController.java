package uptc.edu.co.file_explorer_doj.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import uptc.edu.co.file_explorer_doj.service.FileCommandService;

public class PermissionsController {

    @FXML private Label fileLabel;
    @FXML private TextArea attribOutput;
    @FXML private CheckBox cbReadOnly;
    @FXML private CheckBox cbHidden;

    private String filePath;
    private Runnable onClose;
    private final FileCommandService cmdService = new FileCommandService();

    public void setFile(String path) {
        this.filePath = path;
        fileLabel.setText(path);
        loadAttributes();
    }

    public void setOnClose(Runnable callback) {
        this.onClose = callback;
    }

    private void loadAttributes() {
        Thread t = new Thread(() -> {
            FileCommandService.ProcessResult result = cmdService.getAttributes(filePath);
            Platform.runLater(() -> {
                attribOutput.setText(result.output());
                // attrib output example:  A  R    H   C:\path\file.txt
                // flags appear in the first part of the line, before the path
                String out = result.output().toUpperCase();
                int pathStart = out.indexOf(filePath.substring(0, 2).toUpperCase());
                String flags = pathStart > 0 ? out.substring(0, pathStart) : out;
                cbReadOnly.setSelected(flags.contains(" R ") || flags.startsWith("R") || flags.contains(" R\n"));
                cbHidden.setSelected(flags.contains(" H ") || flags.startsWith("H") || flags.contains(" H\n"));
            });
        }, "attrib-loader");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    void onApply() {
        Thread t = new Thread(() -> {
            cmdService.setReadOnly(filePath, cbReadOnly.isSelected());
            cmdService.setHidden(filePath, cbHidden.isSelected());
            Platform.runLater(() -> {
                loadAttributes();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Atributos");
                alert.setHeaderText(null);
                alert.setContentText("Atributos aplicados correctamente.");
                alert.showAndWait();
            });
        }, "attrib-apply");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    void onClose() {
        if (onClose != null) onClose.run();
        ((Stage) fileLabel.getScene().getWindow()).close();
    }
}

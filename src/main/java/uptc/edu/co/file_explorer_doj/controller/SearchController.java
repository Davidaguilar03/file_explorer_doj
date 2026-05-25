package uptc.edu.co.file_explorer_doj.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import uptc.edu.co.file_explorer_doj.service.FileSearchService;

import java.util.List;
import java.util.function.Consumer;

public class SearchController {

    @FXML private Label searchInLabel;
    @FXML private TextField searchField;
    @FXML private Button btnSearch;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private ListView<String> resultList;

    private String searchDirectory;
    private Consumer<String> onResultSelected;
    private final FileSearchService searchService = new FileSearchService();
    private Thread searchThread;

    @FXML
    public void initialize() {
        resultList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) navigateToSelected();
        });
        resultList.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) navigateToSelected();
        });
        searchField.setOnAction(e -> onSearch());
    }

    public void setSearchDirectory(String dir) {
        searchDirectory = dir;
        searchInLabel.setText("Buscar en: " + dir);
    }

    public void setOnResultSelected(Consumer<String> callback) {
        this.onResultSelected = callback;
    }

    @FXML
    void onSearch() {
        String pattern = searchField.getText().trim();
        if (pattern.isEmpty()) return;

        if (searchThread != null && searchThread.isAlive()) searchThread.interrupt();

        resultList.setItems(FXCollections.observableArrayList());
        progressBar.setVisible(true);
        progressBar.setProgress(-1);
        btnSearch.setDisable(true);
        statusLabel.setText("Buscando...");

        var task = searchService.createSearchTask(searchDirectory, pattern);
        task.messageProperty().addListener((obs, old, msg) ->
            Platform.runLater(() -> statusLabel.setText(msg)));

        task.setOnSucceeded(e -> {
            List<String> results = task.getValue();
            resultList.setItems(FXCollections.observableArrayList(results));
            progressBar.setVisible(false);
            btnSearch.setDisable(false);
            statusLabel.setText(results.isEmpty()
                ? "Sin resultados."
                : results.size() + " resultado(s) encontrado(s). Doble click para navegar.");
        });

        task.setOnFailed(e -> {
            progressBar.setVisible(false);
            btnSearch.setDisable(false);
            statusLabel.setText("Error en la busqueda.");
        });

        task.setOnCancelled(e -> {
            progressBar.setVisible(false);
            btnSearch.setDisable(false);
            statusLabel.setText("Busqueda cancelada.");
        });

        searchThread = new Thread(task, "search-thread");
        searchThread.setDaemon(true);
        searchThread.start();
    }

    private void navigateToSelected() {
        String selected = resultList.getSelectionModel().getSelectedItem();
        if (selected != null && onResultSelected != null) {
            onResultSelected.accept(selected);
        }
    }

    @FXML
    void onClose() {
        if (searchThread != null) searchThread.interrupt();
        ((Stage) resultList.getScene().getWindow()).close();
    }
}

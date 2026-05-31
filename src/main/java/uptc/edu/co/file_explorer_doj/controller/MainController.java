package uptc.edu.co.file_explorer_doj.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uptc.edu.co.file_explorer_doj.model.FileItem;
import uptc.edu.co.file_explorer_doj.service.FileCommandService;
import uptc.edu.co.file_explorer_doj.service.FileSearchService;

public class MainController {

    // -------------------------------------------------------------------------
    // FXML bindings
    // -------------------------------------------------------------------------

    @FXML private TreeView<File>              directoryTree;
    @FXML private TableView<FileItem>         fileTable;
    @FXML private TableColumn<FileItem, String> colIcon;
    @FXML private TableColumn<FileItem, String> colName;
    @FXML private TableColumn<FileItem, String> colSize;
    @FXML private TableColumn<FileItem, String> colType;
    @FXML private TableColumn<FileItem, String> colDate;
    @FXML private TextField                   addressBar;
    @FXML private Button                      btnBack;
    @FXML private Button                      btnForward;
    @FXML private Button                      btnUp;
    @FXML private Label                       statusPath;
    @FXML private Label                       statusItems;
    @FXML private Label                       statusDisk;
    @FXML private CheckMenuItem               menuShowHidden;
    @FXML private Button                      btnTheme;

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private static final String DARK_CSS  = "/uptc/edu/co/file_explorer_doj/css/theme.css";
    private static final String LIGHT_CSS = "/uptc/edu/co/file_explorer_doj/css/theme-light.css";
    private static final String ICON_ROOT = "/uptc/edu/co/file_explorer_doj/images/vivid/";
    private static final String FOLDER_ICON = ICON_ROOT + "folder.png";
    private static final String DEFAULT_FILE_ICON = ICON_ROOT + "default.png";
    private static final int TABLE_ICON_SIZE = 20;
    private static final int TREE_ICON_SIZE = 16;

    private static final Map<String, String> NAME_ICON_MAP = Map.ofEntries(
        Map.entry("pom.xml", "pom.png"),
        Map.entry("build.gradle", "gradle.png"),
        Map.entry("build.gradle.kts", "gradle.png"),
        Map.entry("settings.gradle", "gradle.png"),
        Map.entry("settings.gradle.kts", "gradle.png"),
        Map.entry("dockerfile", "config.png"),
        Map.entry("readme.md", "md.png"),
        Map.entry("license", "gpl.png")
    );

    private static final Map<String, String> EXT_ICON_MAP = Map.ofEntries(
        Map.entry("txt", "txt.png"),
        Map.entry("log", "log.png"),
        Map.entry("md", "md.png"),
        Map.entry("pdf", "pdf.png"),
        Map.entry("doc", "doc.png"),
        Map.entry("docx", "docx.png"),
        Map.entry("xls", "xls.png"),
        Map.entry("xlsx", "xlsx.png"),
        Map.entry("ppt", "ppt.png"),
        Map.entry("pptx", "pptx.png"),
        Map.entry("csv", "csv.png"),
        Map.entry("jpg", "image.png"),
        Map.entry("jpeg", "image.png"),
        Map.entry("png", "image.png"),
        Map.entry("gif", "image.png"),
        Map.entry("bmp", "image.png"),
        Map.entry("webp", "image.png"),
        Map.entry("svg", "svg.png"),
        Map.entry("mp3", "mp3.png"),
        Map.entry("wav", "wav.png"),
        Map.entry("flac", "flac.png"),
        Map.entry("ogg", "ogg.png"),
        Map.entry("aac", "aac.png"),
        Map.entry("mp4", "mp4.png"),
        Map.entry("avi", "avi.png"),
        Map.entry("mkv", "mkv.png"),
        Map.entry("mov", "mov.png"),
        Map.entry("wmv", "wmv.png"),
        Map.entry("zip", "zip.png"),
        Map.entry("rar", "rar.png"),
        Map.entry("7z", "7z.png"),
        Map.entry("tar", "tar.png"),
        Map.entry("gz", "gz.png"),
        Map.entry("exe", "exe.png"),
        Map.entry("msi", "msi.png"),
        Map.entry("dll", "dll.png"),
        Map.entry("bat", "bat.png"),
        Map.entry("cmd", "cmd.png"),
        Map.entry("ps1", "ps1.png"),
        Map.entry("sh", "sh.png"),
        Map.entry("java", "java.png"),
        Map.entry("class", "class.png"),
        Map.entry("py", "py.png"),
        Map.entry("js", "js.png"),
        Map.entry("ts", "ts.png"),
        Map.entry("html", "html.png"),
        Map.entry("htm", "html.png"),
        Map.entry("css", "css.png"),
        Map.entry("json", "json.png"),
        Map.entry("xml", "xml.png"),
        Map.entry("yml", "yml.png"),
        Map.entry("yaml", "yaml.png"),
        Map.entry("sql", "sql.png"),
        Map.entry("ini", "ini.png"),
        Map.entry("properties", "config.png"),
        Map.entry("env", "config.png")
    );

    private final Map<String, Image> iconCache = new HashMap<>();
    private boolean darkTheme = true;

    private String currentPath;
    private final Deque<String> backHistory    = new ArrayDeque<>();
    private final Deque<String> forwardHistory = new ArrayDeque<>();
    private List<String> clipboardPaths  = new ArrayList<>();
    private boolean clipboardIsCut       = false;
    private boolean showHidden           = false;
    private boolean updatingTree         = false;
    private final Set<TreeItem<File>> loadedItems = new HashSet<>();

    private final FileCommandService cmdService    = new FileCommandService();
    private final FileSearchService  searchService = new FileSearchService();

    private ContextMenu contextMenu;
    private MenuItem ctxCopy, ctxCut, ctxPaste, ctxDelete, ctxRename, ctxProperties;

    // -------------------------------------------------------------------------
    // Initialization
    // -------------------------------------------------------------------------

    @FXML
    public void initialize() {
        setupTable();
        setupTree();
        setupContextMenu();
        navigateTo(System.getProperty("user.home"));
    }

    // -------------------------------------------------------------------------
    // Table
    // -------------------------------------------------------------------------

    private void setupTable() {
        colIcon.setCellValueFactory(d -> d.getValue().iconProperty());
        colName.setCellValueFactory(d -> d.getValue().nameProperty());
        colSize.setCellValueFactory(d -> d.getValue().sizeProperty());
        colType.setCellValueFactory(d -> d.getValue().typeProperty());
        colDate.setCellValueFactory(d -> d.getValue().dateModifiedProperty());

        colIcon.setCellFactory(col -> new TableCell<>() {
            private final ImageView iconView = createIconView(TABLE_ICON_SIZE);
            @Override
            protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                FileItem item = empty ? null : getTableRow().getItem();
                if (item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Image icon = loadIcon(resolveIconPath(item.getFile()), TABLE_ICON_SIZE);
                if (icon != null) {
                    iconView.setImage(icon);
                    setGraphic(iconView);
                    setText(null);
                    setStyle("-fx-alignment: CENTER;");
                } else {
                    setGraphic(null);
                    setText(v);
                    setStyle("-fx-font-family: 'Segoe UI Emoji', 'Apple Color Emoji', sans-serif; -fx-font-size: 16px; -fx-alignment: CENTER;");
                }
            }
        });

        fileTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        fileTable.setOnMouseClicked(event -> {
            contextMenu.hide();
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                FileItem sel = fileTable.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    if (sel.isDirectory()) navigateTo(sel.getPath());
                    else openFile(sel.getPath());
                }
            }
        });

        fileTable.setOnContextMenuRequested(event -> {
            updateContextMenuState();
            contextMenu.show(fileTable, event.getScreenX(), event.getScreenY());
        });

        fileTable.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DELETE -> { handleDelete(); event.consume(); }
                case F2     -> { handleRename(); event.consume(); }
                case F5     -> { refreshTable(); event.consume(); }
                case BACK_SPACE -> { goUp(); event.consume(); }
                case ENTER  -> {
                    FileItem sel = fileTable.getSelectionModel().getSelectedItem();
                    if (sel != null) {
                        if (sel.isDirectory()) navigateTo(sel.getPath());
                        else openFile(sel.getPath());
                    }
                    event.consume();
                }
                default -> {}
            }
        });
    }

    // -------------------------------------------------------------------------
    // Tree
    // -------------------------------------------------------------------------

    private void setupTree() {
        TreeItem<File> root = new TreeItem<>(new File(""));
        root.setExpanded(true);
        File[] drives = File.listRoots();
        if (drives != null) {
            for (File d : drives) root.getChildren().add(createTreeItem(d));
        }
        directoryTree.setRoot(root);
        directoryTree.setShowRoot(false);

        directoryTree.setCellFactory(tv -> new TreeCell<>() {
            private final ImageView iconView = createIconView(TREE_ICON_SIZE);
            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                if (empty || file == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                String n = file.getName();
                setText(n.isEmpty() ? file.getPath() : n);
                Image icon = loadIcon(resolveIconPath(file), TREE_ICON_SIZE);
                if (icon != null) {
                    iconView.setImage(icon);
                    setGraphic(iconView);
                } else {
                    setGraphic(null);
                }
            }
        });

        directoryTree.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (!updatingTree && sel != null && sel.getValue() != null) {
                String path = sel.getValue().getAbsolutePath();
                if (!path.equalsIgnoreCase(currentPath)) navigateTo(path);
            }
        });
    }

    private TreeItem<File> createTreeItem(File dir) {
        TreeItem<File> item = new TreeItem<>(dir);

        try {
            File[] subs = dir.listFiles(f -> f.isDirectory() && (showHidden || !f.isHidden()));
            if (subs != null && subs.length > 0)
                item.getChildren().add(new TreeItem<>(null)); // placeholder for expand arrow
        } catch (Exception ignored) {}

        item.expandedProperty().addListener((obs, was, expanded) -> {
            if (expanded && !loadedItems.contains(item)) {
                loadedItems.add(item);
                loadTreeChildren(item);
            }
        });
        return item;
    }

    private void loadTreeChildren(TreeItem<File> parent) {
        File dir = parent.getValue();
        if (dir == null || !dir.isDirectory()) return;
        parent.getChildren().clear();
        File[] subs = dir.listFiles(f -> f.isDirectory() && (showHidden || !f.isHidden()));
        if (subs != null) {
            Arrays.sort(subs, Comparator.comparing(f -> f.getName().toLowerCase()));
            for (File s : subs) parent.getChildren().add(createTreeItem(s));
        }
    }

    private void refreshTree() {
        loadedItems.clear();
        TreeItem<File> root = directoryTree.getRoot();
        root.getChildren().clear();
        File[] drives = File.listRoots();
        if (drives != null) {
            for (File d : drives) root.getChildren().add(createTreeItem(d));
        }
    }

    private void selectInTree(String path) {
        updatingTree = true;
        try {
            doSelectInTree(directoryTree.getRoot(), new File(path));
        } finally {
            updatingTree = false;
        }
    }

    private boolean doSelectInTree(TreeItem<File> parent, File target) {
        for (TreeItem<File> item : new ArrayList<>(parent.getChildren())) {
            File f = item.getValue();
            if (f == null) continue;
            String iPath = f.getAbsolutePath().toLowerCase();
            String tPath = target.getAbsolutePath().toLowerCase();

            if (iPath.equals(tPath)) {
                directoryTree.getSelectionModel().select(item);
                int row = directoryTree.getRow(item);
                if (row >= 0) directoryTree.scrollTo(row);
                return true;
            }

            String iPathSep = iPath.endsWith("\\") ? iPath : iPath + "\\";
            if (tPath.startsWith(iPathSep)) {
                if (!loadedItems.contains(item)) {
                    loadedItems.add(item);
                    loadTreeChildren(item);
                }
                item.setExpanded(true);
                if (doSelectInTree(item, target)) return true;
            }
        }
        return false;
    }

    private String resolveIconPath(File file) {
        if (file == null) return null;
        if (file.isDirectory()) return FOLDER_ICON;

        String name = file.getName().toLowerCase(Locale.ROOT);
        String nameIcon = NAME_ICON_MAP.get(name);
        if (nameIcon != null) {
            String byName = ICON_ROOT + nameIcon;
            if (resourceExists(byName)) return byName;
        }

        String ext = getExtension(name);
        if (!ext.isEmpty()) {
                String mapped = EXT_ICON_MAP.getOrDefault(ext, ext + ".png");
            String byExt = ICON_ROOT + mapped;
            if (resourceExists(byExt)) return byExt;
        }

        return resourceExists(DEFAULT_FILE_ICON) ? DEFAULT_FILE_ICON : null;
    }

    private String getExtension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) return "";
        return name.substring(dot + 1);
    }

    private boolean resourceExists(String resourcePath) {
        return getClass().getResource(resourcePath) != null;
    }

    private Image loadIcon(String resourcePath, int size) {
        if (resourcePath == null) return null;
        String cacheKey = resourcePath + "#" + size;
        Image cached = iconCache.get(cacheKey);
        if (cached != null) return cached;
        URL url = getClass().getResource(resourcePath);
        if (url == null) return null;
        Image image = new Image(url.toExternalForm(), size, size, true, true);
        iconCache.put(cacheKey, image);
        return image;
    }

    private ImageView createIconView(int size) {
        ImageView view = new ImageView();
        view.setFitWidth(size);
        view.setFitHeight(size);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        return view;
    }

    // -------------------------------------------------------------------------
    // Context menu
    // -------------------------------------------------------------------------

    private void setupContextMenu() {
        ctxCopy  = new MenuItem("Copiar");
        ctxCut   = new MenuItem("Cortar");
        ctxPaste = new MenuItem("Pegar");
        ctxDelete = new MenuItem("Eliminar");
        ctxRename = new MenuItem("Renombrar");
        ctxProperties = new MenuItem("Propiedades");
        MenuItem ctxNewFolder = new MenuItem("Nueva carpeta");
        MenuItem ctxNewFile   = new MenuItem("Nuevo archivo");

        ctxCopy.setOnAction(e -> handleCopy());
        ctxCut.setOnAction(e -> handleCut());
        ctxPaste.setOnAction(e -> handlePaste());
        ctxDelete.setOnAction(e -> handleDelete());
        ctxRename.setOnAction(e -> handleRename());
        ctxProperties.setOnAction(e -> handleProperties());
        ctxNewFolder.setOnAction(e -> handleNewFolder());
        ctxNewFile.setOnAction(e -> handleNewFile());

        contextMenu = new ContextMenu(
            ctxCopy, ctxCut, ctxPaste, new SeparatorMenuItem(),
            ctxDelete, ctxRename, new SeparatorMenuItem(),
            ctxNewFolder, ctxNewFile, new SeparatorMenuItem(),
            ctxProperties
        );
    }

    private void updateContextMenuState() {
        int count = fileTable.getSelectionModel().getSelectedItems().size();
        ctxCopy.setDisable(count == 0);
        ctxCut.setDisable(count == 0);
        ctxPaste.setDisable(clipboardPaths.isEmpty());
        ctxDelete.setDisable(count == 0);
        ctxRename.setDisable(count != 1);
        ctxProperties.setDisable(count != 1);
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    void navigateTo(String path) {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            showError("Directorio no encontrado", "No se puede navegar a:\n" + path);
            return;
        }
        if (currentPath != null && !currentPath.equalsIgnoreCase(dir.getAbsolutePath())) {
            backHistory.push(currentPath);
            forwardHistory.clear();
        }
        currentPath = dir.getAbsolutePath();
        addressBar.setText(currentPath);
        btnBack.setDisable(backHistory.isEmpty());
        btnForward.setDisable(forwardHistory.isEmpty());
        btnUp.setDisable(dir.getParentFile() == null);
        refreshTable();
        selectInTree(currentPath);
        updateStatusBar();
    }

    @FXML void goBack() {
        if (!backHistory.isEmpty()) {
            forwardHistory.push(currentPath);
            navigateTo(backHistory.pop());
        }
    }

    @FXML void goForward() {
        if (!forwardHistory.isEmpty()) {
            backHistory.push(currentPath);
            navigateTo(forwardHistory.pop());
        }
    }

    @FXML void goUp() {
        if (currentPath != null) {
            File parent = new File(currentPath).getParentFile();
            if (parent != null) navigateTo(parent.getAbsolutePath());
        }
    }

    @FXML void handleAddressEnter() {
        String p = addressBar.getText().trim();
        if (!p.isEmpty()) navigateTo(p);
    }

    // -------------------------------------------------------------------------
    // File operations
    // -------------------------------------------------------------------------

    @FXML void handleCopy() {
        clipboardIsCut = false;
        clipboardPaths = selectedPaths();
        if (!clipboardPaths.isEmpty())
            statusPath.setText("Copiado(s): " + clipboardPaths.size() + " elemento(s)");
    }

    @FXML void handleCut() {
        clipboardIsCut = true;
        clipboardPaths = selectedPaths();
        if (!clipboardPaths.isEmpty())
            statusPath.setText("Cortado(s): " + clipboardPaths.size() + " elemento(s)");
    }

    @FXML void handlePaste() {
        if (clipboardPaths.isEmpty() || currentPath == null) return;
        List<String> paths = new ArrayList<>(clipboardPaths);
        boolean isCut = clipboardIsCut;
        runTask(new Task<Void>() {
            @Override
            protected Void call() {
                for (String src : paths) {
                    File srcFile = new File(src);
                    String dest = currentPath + File.separator + srcFile.getName();
                    FileCommandService.ProcessResult r = isCut
                        ? cmdService.move(src, dest)
                        : (srcFile.isDirectory() ? cmdService.copyFolder(src, dest) : cmdService.copyFile(src, dest));
                    if (!r.isSuccess()) {
                        String msg = r.output();
                        Platform.runLater(() -> showError("Error al pegar", msg));
                    }
                }
                return null;
            }

            @Override
            protected void succeeded() {
                if (isCut) clipboardPaths.clear();
                refreshTable();
            }

            @Override
            protected void failed() {
                showError("Error", getException().getMessage());
            }
        });
    }

    @FXML void handleDelete() {
        List<FileItem> sel = new ArrayList<>(fileTable.getSelectionModel().getSelectedItems());
        if (sel.isEmpty()) return;
        String msg = sel.size() == 1
            ? "Eliminar '" + sel.get(0).getName() + "'?"
            : "Eliminar " + sel.size() + " elementos?";
        if (!showConfirm("Eliminar", msg)) return;
        runTask(new Task<Void>() {
            @Override
            protected Void call() {
                for (FileItem item : sel) {
                    FileCommandService.ProcessResult r = item.isDirectory()
                        ? cmdService.deleteFolder(item.getPath())
                        : cmdService.deleteFile(item.getPath());
                    if (!r.isSuccess()) {
                        String out = r.output();
                        Platform.runLater(() -> showError("Error al eliminar", out));
                    }
                }
                return null;
            }

            @Override protected void succeeded() { refreshTable(); }
            @Override protected void failed() { showError("Error", getException().getMessage()); refreshTable(); }
        });
    }

    @FXML void handleRename() {
        FileItem sel = fileTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        TextInputDialog dlg = new TextInputDialog(sel.getName());
        dlg.setTitle("Renombrar");
        dlg.setHeaderText(null);
        dlg.setContentText("Nuevo nombre:");
        applyDialogStyle(dlg.getDialogPane());
        dlg.showAndWait().ifPresent(name -> {
            if (name.isBlank() || name.equals(sel.getName())) return;
            runTask(new Task<Void>() {
                @Override
                protected Void call() {
                    FileCommandService.ProcessResult r = cmdService.rename(sel.getPath(), name);
                    if (!r.isSuccess()) Platform.runLater(() -> showError("Error al renombrar", r.output()));
                    return null;
                }
                @Override protected void succeeded() { refreshTable(); }
            });
        });
    }

    @FXML void handleNewFolder() {
        TextInputDialog dlg = new TextInputDialog("Nueva carpeta");
        dlg.setTitle("Nueva carpeta");
        dlg.setHeaderText(null);
        dlg.setContentText("Nombre:");
        applyDialogStyle(dlg.getDialogPane());
        dlg.showAndWait().ifPresent(name -> {
            if (name.isBlank()) return;
            runTask(new Task<Void>() {
                @Override
                protected Void call() {
                    FileCommandService.ProcessResult r = cmdService.createFolder(currentPath, name);
                    if (!r.isSuccess()) Platform.runLater(() -> showError("Error al crear carpeta", r.output()));
                    return null;
                }
                @Override protected void succeeded() { refreshTable(); }
            });
        });
    }

    @FXML void handleNewFile() {
        TextInputDialog dlg = new TextInputDialog("nuevo_archivo.txt");
        dlg.setTitle("Nuevo archivo");
        dlg.setHeaderText(null);
        dlg.setContentText("Nombre del archivo (con extension):");
        applyDialogStyle(dlg.getDialogPane());
        dlg.showAndWait().ifPresent(name -> {
            if (name.isBlank()) return;
            runTask(new Task<Void>() {
                @Override
                protected Void call() {
                    FileCommandService.ProcessResult r = cmdService.createFile(currentPath, name);
                    if (!r.isSuccess()) Platform.runLater(() -> showError("Error al crear archivo", r.output()));
                    return null;
                }
                @Override protected void succeeded() { refreshTable(); }
            });
        });
    }

    @FXML void handleSearch() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/uptc/edu/co/file_explorer_doj/fxml/search.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load(), 640, 480);
            applyStylesheet(scene);
            stage.setTitle("Buscar archivos");
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(addressBar.getScene().getWindow());

            SearchController ctrl = loader.getController();
            ctrl.setSearchDirectory(currentPath);
            ctrl.setOnResultSelected(path -> {
                stage.close();
                File f = new File(path);
                String parent = f.isDirectory() ? f.getAbsolutePath() : f.getParent();
                navigateTo(parent);
                Platform.runLater(() -> {
                    for (FileItem item : fileTable.getItems()) {
                        if (item.getPath().equalsIgnoreCase(path)) {
                            fileTable.getSelectionModel().select(item);
                            fileTable.scrollTo(item);
                            break;
                        }
                    }
                });
            });
            stage.show();
        } catch (IOException e) {
            showError("Error", "No se pudo abrir la ventana de busqueda:\n" + e.getMessage());
        }
    }

    private void handleProperties() {
        FileItem sel = fileTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/uptc/edu/co/file_explorer_doj/fxml/permissions.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load(), 480, 340);
            applyStylesheet(scene);
            stage.setTitle("Propiedades");
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(addressBar.getScene().getWindow());

            PermissionsController ctrl = loader.getController();
            ctrl.setFile(sel.getPath());
            ctrl.setOnClose(() -> refreshTable());
            stage.show();
        } catch (IOException e) {
            showError("Error", e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Menu actions
    // -------------------------------------------------------------------------

    @FXML void handleRefresh()      { refreshTable(); }
    @FXML void handleSelectAll()    { fileTable.getSelectionModel().selectAll(); }
    @FXML void handleExit()         { Platform.exit(); }

    @FXML void handleToggleTheme() {
        darkTheme = !darkTheme;
        Scene scene = addressBar.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource(currentThemeCss()).toExternalForm());
        btnTheme.setText(darkTheme ? "☀" : "☾");
    }

    private String currentThemeCss() {
        return darkTheme ? DARK_CSS : LIGHT_CSS;
    }

    @FXML void handleToggleHidden() {
        showHidden = menuShowHidden.isSelected();
        refreshTable();
        refreshTree();
        selectInTree(currentPath);
    }

    @FXML void handleAbout() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Acerca de");
        a.setHeaderText("File Explorer v1.0");
        a.setContentText("Explorador de archivos — Java 21 + JavaFX\nOperaciones via ProcessBuilder / cmd.exe");
        applyDialogStyle(a.getDialogPane());
        a.showAndWait();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    void refreshTable() {
        if (currentPath == null) return;
        File dir = new File(currentPath);
        File[] files = dir.listFiles(f -> showHidden || !f.isHidden());
        ObservableList<FileItem> items = FXCollections.observableArrayList();
        if (files != null) {
            Arrays.sort(files, (a, b) -> {
                if (a.isDirectory() != b.isDirectory()) return a.isDirectory() ? -1 : 1;
                return a.getName().compareToIgnoreCase(b.getName());
            });
            for (File f : files) items.add(new FileItem(f));
        }
        fileTable.setItems(items);
        updateStatusBar();
    }

    private void updateStatusBar() {
        if (currentPath == null) return;
        statusPath.setText(currentPath);
        int count = fileTable.getItems().size();
        statusItems.setText(count + (count == 1 ? " elemento" : " elementos"));
        try {
            File root = new File(currentPath);
            while (root.getParentFile() != null) root = root.getParentFile();
            statusDisk.setText("Libre: " + fmtSize(root.getFreeSpace()) + " / " + fmtSize(root.getTotalSpace()));
        } catch (Exception ignored) {
            statusDisk.setText("");
        }
    }

    private String fmtSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private List<String> selectedPaths() {
        return fileTable.getSelectionModel().getSelectedItems().stream()
            .map(FileItem::getPath).collect(Collectors.toList());
    }

    private void openFile(String path) {
        try { new ProcessBuilder("cmd", "/c", "start", "", "\"" + path + "\"").start(); }
        catch (IOException e) { showError("Error", "No se pudo abrir el archivo:\n" + e.getMessage()); }
    }

    private void runTask(Task<?> task) {
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void showError(String title, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        applyDialogStyle(a.getDialogPane());
        a.showAndWait();
    }

    private boolean showConfirm(String title, String content) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        applyDialogStyle(a.getDialogPane());
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void applyDialogStyle(DialogPane pane) {
        try {
            pane.getStylesheets().add(getClass().getResource(currentThemeCss()).toExternalForm());
        } catch (Exception ignored) {}
    }

    private void applyStylesheet(Scene scene) {
        try {
            scene.getStylesheets().add(getClass().getResource(currentThemeCss()).toExternalForm());
        } catch (Exception ignored) {}
    }
}

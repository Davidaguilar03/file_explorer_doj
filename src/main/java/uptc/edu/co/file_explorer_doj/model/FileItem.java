package uptc.edu.co.file_explorer_doj.model;

import javafx.beans.property.SimpleStringProperty;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileItem {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final File file;
    private final SimpleStringProperty name;
    private final SimpleStringProperty size;
    private final SimpleStringProperty type;
    private final SimpleStringProperty dateModified;
    private final SimpleStringProperty icon;

    public FileItem(File file) {
        this.file = file;
        String displayName = file.getName();
        if (displayName.isEmpty()) displayName = file.getPath();
        this.name = new SimpleStringProperty(displayName);
        this.dateModified = new SimpleStringProperty(DATE_FORMAT.format(new Date(file.lastModified())));

        if (file.isDirectory()) {
            this.size = new SimpleStringProperty("");
            this.type = new SimpleStringProperty("Carpeta de archivos");
            this.icon = new SimpleStringProperty("📁");
        } else {
            this.size = new SimpleStringProperty(formatSize(file.length()));
            this.type = new SimpleStringProperty(getFileType(file.getName()));
            this.icon = new SimpleStringProperty(getFileIcon(file.getName()));
        }
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private String getFileType(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0) return "Archivo";
        return switch (filename.substring(dot + 1).toLowerCase()) {
            case "txt", "log" -> "Documento de texto";
            case "pdf"        -> "Documento PDF";
            case "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg" -> "Imagen";
            case "mp3", "wav", "flac", "ogg", "aac" -> "Audio";
            case "mp4", "avi", "mkv", "mov", "wmv"  -> "Video";
            case "zip", "rar", "7z", "tar", "gz"    -> "Archivo comprimido";
            case "exe", "msi" -> "Aplicacion";
            case "java"       -> "Archivo Java";
            case "class"      -> "Clase Java";
            case "xml"        -> "Documento XML";
            case "json"       -> "Documento JSON";
            case "html", "htm"-> "Documento HTML";
            case "css"        -> "Hoja de estilos";
            case "js", "ts"   -> "Script";
            case "py"         -> "Script Python";
            case "docx", "doc"-> "Documento Word";
            case "xlsx", "xls"-> "Libro Excel";
            case "pptx", "ppt"-> "Presentacion";
            case "bat", "cmd" -> "Script de comandos";
            case "dll"        -> "Biblioteca DLL";
            default -> filename.substring(dot + 1).toUpperCase() + " File";
        };
    }

    private String getFileIcon(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0) return "📄";
        return switch (filename.substring(dot + 1).toLowerCase()) {
            case "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg" -> "🖼";
            case "mp3", "wav", "flac", "ogg", "aac"                -> "🎵";
            case "mp4", "avi", "mkv", "mov", "wmv"                 -> "🎬";
            case "zip", "rar", "7z", "tar", "gz"                   -> "📦";
            case "exe", "msi"                                       -> "⚙";
            case "pdf"                                              -> "📕";
            case "txt", "log"                                       -> "📝";
            case "java"                                             -> "☕";
            case "py"                                               -> "🐍";
            case "js", "ts"                                         -> "📜";
            case "html", "htm"                                      -> "🌐";
            case "css"                                              -> "🎨";
            case "xml", "json"                                      -> "📋";
            case "docx", "doc"                                      -> "📘";
            case "xlsx", "xls"                                      -> "📊";
            case "pptx", "ppt"                                      -> "📑";
            case "bat", "cmd"                                       -> "🖥";
            case "dll"                                              -> "🔧";
            case "class"                                            -> "☕";
            default -> "📄";
        };
    }

    public File getFile()            { return file; }
    public String getName()          { return name.get(); }
    public String getSize()          { return size.get(); }
    public String getType()          { return type.get(); }
    public String getDateModified()  { return dateModified.get(); }
    public String getIcon()          { return icon.get(); }
    public String getPath()          { return file.getAbsolutePath(); }
    public boolean isDirectory()     { return file.isDirectory(); }

    public SimpleStringProperty nameProperty()          { return name; }
    public SimpleStringProperty sizeProperty()          { return size; }
    public SimpleStringProperty typeProperty()          { return type; }
    public SimpleStringProperty dateModifiedProperty()  { return dateModified; }
    public SimpleStringProperty iconProperty()          { return icon; }

    @Override public String toString() { return getName(); }
}

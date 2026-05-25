package uptc.edu.co.file_explorer_doj.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class FileCommandService {

    public record ProcessResult(int exitCode, String output) {
        public boolean isSuccess() { return exitCode == 0; }
    }

    private ProcessResult runShell(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), Charset.forName("CP850")))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            return new ProcessResult(process.waitFor(), sb.toString().trim());
        } catch (Exception e) {
            return new ProcessResult(-1, e.getMessage() != null ? e.getMessage() : "Error desconocido");
        }
    }

    public ProcessResult copyFile(String src, String dest) {
        return runShell("copy /Y \"" + src + "\" \"" + dest + "\"");
    }

    public ProcessResult copyFolder(String src, String dest) {
        return runShell("xcopy /E /I /Y /H \"" + src + "\" \"" + dest + "\"");
    }

    public ProcessResult move(String src, String dest) {
        return runShell("move /Y \"" + src + "\" \"" + dest + "\"");
    }

    public ProcessResult deleteFile(String path) {
        return runShell("del /F /Q \"" + path + "\"");
    }

    public ProcessResult deleteFolder(String path) {
        return runShell("rmdir /S /Q \"" + path + "\"");
    }

    public ProcessResult rename(String path, String newName) {
        return runShell("ren \"" + path + "\" \"" + newName + "\"");
    }

    public ProcessResult createFile(String dirPath, String fileName) {
        return runShell("copy /b nul \"" + dirPath + "\\" + fileName + "\"");
    }

    public ProcessResult createFolder(String dirPath, String folderName) {
        return runShell("mkdir \"" + dirPath + "\\" + folderName + "\"");
    }

    public ProcessResult getAttributes(String path) {
        return runShell("attrib \"" + path + "\"");
    }

    public ProcessResult setReadOnly(String path, boolean readOnly) {
        return runShell("attrib " + (readOnly ? "+R" : "-R") + " \"" + path + "\"");
    }

    public ProcessResult setHidden(String path, boolean hidden) {
        return runShell("attrib " + (hidden ? "+H" : "-H") + " \"" + path + "\"");
    }
}

package uptc.edu.co.file_explorer_doj.service;

import javafx.concurrent.Task;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FileSearchService {

    public Task<List<String>> createSearchTask(String directory, String pattern) {
        return new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                List<String> results = new ArrayList<>();
                String command = "dir /s /b \"" + directory + "\\*" + pattern + "*\"";
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", command);
                pb.redirectErrorStream(true);
                Process process = pb.start();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), Charset.forName("CP850")))) {
                    String line;
                    while ((line = reader.readLine()) != null && !isCancelled()) {
                        String trimmed = line.trim();
                        if (!trimmed.isEmpty()
                                && !trimmed.startsWith("El volumen")
                                && !trimmed.startsWith("Volume")
                                && !trimmed.equalsIgnoreCase("File Not Found")) {
                            results.add(trimmed);
                            updateMessage("Encontrados: " + results.size());
                        }
                    }
                }
                process.waitFor();
                return results;
            }
        };
    }
}

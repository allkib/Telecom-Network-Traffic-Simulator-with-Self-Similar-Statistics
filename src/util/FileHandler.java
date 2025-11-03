// All
package util;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// File handler utility 
public class FileHandler {
    public boolean writeAggregateTrafficCSV(String filename, java.util.List<Double> timeSeries) {
        if (filename == null || filename.isEmpty() || timeSeries == null) return false;
        Path path = Paths.get(filename);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            // Simple one-value-per-line CSV
            for (Double v : timeSeries) {
                writer.write(v == null ? "" : v.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean writeEventLogCSV(String filename, java.util.List<model.Event> log) {
        if (filename == null || filename.isEmpty() || log == null) return false;
        Path path = Paths.get(filename);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("Timestamp,SourceID,Event Type");
            writer.newLine();
            for (model.Event record : log) {
                writer.write(record.getTimestamp() + "," + record.getSourceID() + "," + record.getType());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String readConfig(String filename) {
        if (filename == null || filename.isEmpty()) return null;
        Path path = Paths.get(filename);
        try {
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }
    
    public boolean writeConfig(String filename, String content) {
        if (filename == null || filename.isEmpty()) return false;
        Path path = Paths.get(filename);
        byte[] bytes = content == null ? new byte[0] : content.getBytes(StandardCharsets.UTF_8);
        try {
            Files.write(path, bytes);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
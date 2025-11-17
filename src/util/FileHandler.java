// All
package util;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// File handler utility 
public class FileHandler {
    public boolean writeAggregateTrafficCSV(String filename, java.util.List<Double> timeSeries, 
                                            double samplingInterval, boolean includeMetadata, 
                                            model.SimulationParameters params) {
        if (filename == null || filename.isEmpty() || timeSeries == null) return false;
        Path path = Paths.get(filename);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            // Write metadata header if requested
            if (includeMetadata) {
                writer.write("# Aggregate Traffic Rate CSV");
                writer.newLine();
                writer.write("# Generated: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                writer.newLine();
                if (params != null) {
                    writer.write("# Simulation Duration: " + params.getSimDuration());
                    writer.newLine();
                    writer.write("# Number of Sources: " + params.getNumSources());
                    writer.newLine();
                    writer.write("# Sampling Interval: " + params.getSamplingInt());
                    writer.newLine();
                }
                writer.write("# Sample Count: " + timeSeries.size());
                writer.newLine();
            }
            
            writer.write("Time,TrafficRate");
            writer.newLine();
            
            double currentTime = 0.0;
            for (Double v : timeSeries) {
                writer.write(String.format("%.6f,%.6f", currentTime, v == null ? 0.0 : v));
                writer.newLine();
                currentTime += samplingInterval;
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public boolean writeAggregateTrafficCSV(String filename, java.util.List<Double> timeSeries) {
        // Default: no metadata, assume sampling interval of 1.0
        return writeAggregateTrafficCSV(filename, timeSeries, 1.0, false, null);
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
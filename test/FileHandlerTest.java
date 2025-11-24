package test;

import util.FileHandler;
import model.SimulationParameters;
import model.Event;
import model.QueueStatistics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileHandlerTest {
    private FileHandler fileHandler;
    private List<String> tempFiles;
    
    @BeforeEach
    void setUp() {
        fileHandler = new FileHandler();
        tempFiles = new ArrayList<>();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up temporary test files
        for (String filename : tempFiles) {
            try {
                Files.deleteIfExists(Paths.get(filename));
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }
    
    private String getTempFile(String name) {
        String filename = "test_" + name + "_" + System.currentTimeMillis() + ".tmp";
        tempFiles.add(filename);
        return filename;
    }
    
    // Test writeAggregateTrafficCSV with metadata
    @Test
    void testWriteAggregateTrafficCSVWithMetadata() throws Exception {
        List<Double> timeSeries = new ArrayList<>();
        timeSeries.add(10.5);
        timeSeries.add(15.2);
        timeSeries.add(12.8);
        
        SimulationParameters params = SimulationParameters.defaults();
        params.setSimDuration(100.0);
        params.setSamplingInt(1.0);
        
        String filename = getTempFile("traffic");
        boolean result = fileHandler.writeAggregateTrafficCSV(filename, timeSeries, 1.0, true, params);
        
        assertTrue(result);
        assertTrue(Files.exists(Paths.get(filename)));
        
        List<String> lines = Files.readAllLines(Paths.get(filename));
        assertTrue(lines.get(0).startsWith("# Aggregate Traffic Rate CSV"));
        assertEquals("Time,TrafficRate", lines.get(6));
        assertEquals("0.000000,10.500000", lines.get(7));
    }
    
    // Test writeEventLogCSV
    @Test
    void testWriteEventLogCSV() throws Exception {
        List<Event> events = new ArrayList<>();
        events.add(Event.createOnEvent(1.5, 1));
        events.add(Event.createOffEvent(2.3, 1));
        
        String filename = getTempFile("events");
        boolean result = fileHandler.writeEventLogCSV(filename, events);
        
        assertTrue(result);
        List<String> lines = Files.readAllLines(Paths.get(filename));
        assertEquals("Timestamp,SourceID,Event Type", lines.get(0));
        assertEquals("1.5,1,ON", lines.get(1));
        assertEquals("2.3,1,OFF", lines.get(2));
    }
    
    // Test writeQueueStatsCSV
    @Test
    void testWriteQueueStatsCSV() throws Exception {
        QueueStatistics stats = new QueueStatistics(5.5, 10.0, 25, 2.5, 1000.0);
        
        String filename = getTempFile("queue");
        boolean result = fileHandler.writeQueueStatsCSV(filename, stats);
        
        assertTrue(result);
        List<String> lines = Files.readAllLines(Paths.get(filename));
        assertEquals("Metric,Value", lines.get(0));
        assertEquals("Average Queue Length,5.500000", lines.get(1));
        assertEquals("Overflow Count,25", lines.get(3));
    }
    
    // Test writeConfig and readConfig
    @Test
    void testWriteConfigAndReadConfig() throws Exception {
        String content = "simDuration=1000.0\nnumSources=100\nsamplingInt=1.0";
        String filename = getTempFile("config");
        
        boolean writeResult = fileHandler.writeConfig(filename, content);
        assertTrue(writeResult);
        assertTrue(Files.exists(Paths.get(filename)));
        
        String readContent = fileHandler.readConfig(filename);
        assertEquals(content, readContent);
    }
    
    // Test error handling - null filename
    @Test
    void testWriteAggregateTrafficCSVNullFilename() {
        List<Double> timeSeries = new ArrayList<>();
        timeSeries.add(10.0);
        
        boolean result = fileHandler.writeAggregateTrafficCSV(null, timeSeries, 1.0, false, null);
        assertFalse(result);
    }
    
    // Test error handling - null parameters
    @Test
    void testWriteEventLogCSVNullLog() {
        String filename = getTempFile("test");
        boolean result = fileHandler.writeEventLogCSV(filename, null);
        assertFalse(result);
    }
}
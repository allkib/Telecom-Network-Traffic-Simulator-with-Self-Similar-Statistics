// All
package test;

import controller.SimulationController;
import controller.EventController;
import controller.TrafficController;
import model.SimulationParameters;
import model.TrafficStatistics;
import model.QueueStatistics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * System test for end-to-end simulation flow.
 * Tests complete simulation runs with various parameter combinations.
 */
public class SystemTest {
    private SimulationController controller;
    private List<String> outputFiles;
    
    @BeforeEach
    void setUp() {
        controller = new SimulationController();
        outputFiles = new ArrayList<>();
        outputFiles.add("Aggregate_Traffic.csv");
        outputFiles.add("Event_Log.csv");
        outputFiles.add("Queue_Stats.csv");
    }
    
    @AfterEach
    void tearDown() {
        // Clean up all output files
        for (String filename : outputFiles) {
            try {
                Files.deleteIfExists(Paths.get(filename));
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }
    
    // Full simulation run from start to finish
    @Test
    void testFullSimulationRun() {
        SimulationParameters params = SimulationParameters.defaults();
        params.setSimDuration(20.0);
        params.setSamplingInt(1.0);
        
        EventController eventController = new EventController();
        TrafficController trafficController = new TrafficController();
        
        controller.setEventController(eventController);
        controller.setTrafficController(trafficController);
        
        boolean result = controller.runSimulation(params);
        
        assertTrue(result);
        assertFalse(controller.isRunning());
        
        TrafficStatistics stats = controller.getResults();
        assertNotNull(stats);
        assertFalse(stats.getTimeSeries().isEmpty());
        assertTrue(stats.getAvgRate() >= 0);
        
        QueueStatistics queueStats = controller.getQueueStatistics();
        assertNotNull(queueStats);
        assertTrue(queueStats.getTotalArrivals() >= 0);
    }
    
    // Verify all outputs (CSV files)
    @Test
    void testAllOutputFilesCreated() throws Exception {
        SimulationParameters params = SimulationParameters.defaults();
        params.setSimDuration(10.0);
        params.setSamplingInt(1.0);
        
        EventController eventController = new EventController();
        TrafficController trafficController = new TrafficController();
        
        controller.setEventController(eventController);
        controller.setTrafficController(trafficController);
        
        controller.runSimulation(params);
        
        // Verify all CSV files were created
        assertTrue(Files.exists(Paths.get("Aggregate_Traffic.csv")));
        assertTrue(Files.exists(Paths.get("Event_Log.csv")));
        assertTrue(Files.exists(Paths.get("Queue_Stats.csv")));
        
        // Verify files have content
        List<String> trafficLines = Files.readAllLines(Paths.get("Aggregate_Traffic.csv"));
        assertTrue(trafficLines.size() > 1);
    }
    
    // Test with different parameter combinations
    @Test
    void testDifferentParameterCombinations() {
        SimulationParameters params = SimulationParameters.defaults();
        params.setSimDuration(15.0);
        params.setSamplingInt(2.0);
        params.setQueueBufferSize(500);
        params.setQueueServiceRate(25.0);
        
        EventController eventController = new EventController();
        TrafficController trafficController = new TrafficController();
        
        controller.setEventController(eventController);
        controller.setTrafficController(trafficController);
        
        boolean result = controller.runSimulation(params);
        
        assertTrue(result);
        TrafficStatistics stats = controller.getResults();
        assertTrue(stats.getTimeSeries().size() >= 5);
    }
    
    // Test error scenarios end-to-end
    @Test
    void testErrorScenarios() {
        // Test invalid parameters
        SimulationParameters params1 = SimulationParameters.defaults();
        params1.setSimDuration(-10.0);
        
        EventController eventController = new EventController();
        TrafficController trafficController = new TrafficController();
        
        controller.setEventController(eventController);
        controller.setTrafficController(trafficController);
        
        boolean result1 = controller.runSimulation(params1);
        assertFalse(result1);
        assertFalse(Files.exists(Paths.get("Aggregate_Traffic.csv")));
        
        // Test missing controllers
        SimulationParameters params2 = SimulationParameters.defaults();
        params2.setSimDuration(10.0);
        
        controller = new SimulationController(); // Reset
        boolean result2 = controller.runSimulation(params2);
        assertFalse(result2);
    }
}
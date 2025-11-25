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

public class SimulationControllerTest {
    private SimulationController controller;
    private List<String> tempFiles;
    
    @BeforeEach
    void setUp() {
        controller = new SimulationController();
        tempFiles = new ArrayList<>();
        tempFiles.add("Aggregate_Traffic.csv");
        tempFiles.add("Event_Log.csv");
        tempFiles.add("Queue_Stats.csv");
    }
    
    @AfterEach
    void tearDown() {
        // Clean up CSV files created by simulation
        for (String filename : tempFiles) {
            try {
                Files.deleteIfExists(Paths.get(filename));
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }
    
    // Test runSimulation() with valid parameters
    @Test
    void testRunSimulationWithValidParameters() {
        SimulationParameters params = SimulationParameters.defaults();
        params.setSimDuration(10.0);
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
    }
    
    // Test validation failure handling
    @Test
    void testRunSimulationWithInvalidParameters() {
        SimulationParameters params = SimulationParameters.defaults();
        params.setSimDuration(-1.0);
        
        boolean result = controller.runSimulation(params);
        
        assertFalse(result);
        assertFalse(controller.isRunning());
    }
    
    // Test missing controllers (null checks)
    @Test
    void testRunSimulationWithNullControllers() {
        SimulationParameters params = SimulationParameters.defaults();
        params.setSimDuration(10.0);
        
        // No controllers set (both null)
        boolean result = controller.runSimulation(params);
        
        assertFalse(result);
        assertFalse(controller.isRunning());
    }
    
    // Test simulation loop execution
    @Test
    void testSimulationLoopExecutes() {
        SimulationParameters params = SimulationParameters.defaults();
        params.setSimDuration(5.0);
        params.setSamplingInt(1.0);
        
        EventController eventController = new EventController();
        TrafficController trafficController = new TrafficController();
        
        controller.setEventController(eventController);
        controller.setTrafficController(trafficController);
        
        controller.runSimulation(params);
        
        TrafficStatistics stats = controller.getResults();
        assertTrue(stats.getTimeSeries().size() >= 4);
    }
    
    // Test integration with queue, Hurst calculator, CSV export
    @Test
    void testSimulationIntegration() throws Exception {
        SimulationParameters params = SimulationParameters.defaults();
        params.setSimDuration(10.0);
        params.setSamplingInt(1.0);
        
        EventController eventController = new EventController();
        TrafficController trafficController = new TrafficController();
        
        controller.setEventController(eventController);
        controller.setTrafficController(trafficController);
        
        controller.runSimulation(params);
        
        // Verify queue statistics
        QueueStatistics queueStats = controller.getQueueStatistics();
        assertNotNull(queueStats);
        
        // Verify CSV files were created
        assertTrue(Files.exists(Paths.get("Aggregate_Traffic.csv")));
        assertTrue(Files.exists(Paths.get("Event_Log.csv")));
        assertTrue(Files.exists(Paths.get("Queue_Stats.csv")));
    }
    
    // Test getResults() and getQueueStatistics()
    @Test
    void testGetResultsAndQueueStatistics() {
        SimulationParameters params = SimulationParameters.defaults();
        params.setSimDuration(5.0);
        params.setSamplingInt(1.0);
        
        EventController eventController = new EventController();
        TrafficController trafficController = new TrafficController();
        
        controller.setEventController(eventController);
        controller.setTrafficController(trafficController);
        
        // Before simulation
        TrafficStatistics statsBefore = controller.getResults();
        assertNotNull(statsBefore);
        assertTrue(statsBefore.getTimeSeries().isEmpty());
        assertNull(controller.getQueueStatistics());
        
        controller.runSimulation(params);
        
        // After simulation
        TrafficStatistics statsAfter = controller.getResults();
        assertNotNull(statsAfter);
        assertFalse(statsAfter.getTimeSeries().isEmpty());
        
        QueueStatistics queueStats = controller.getQueueStatistics();
        assertNotNull(queueStats);
        assertTrue(queueStats.getTotalArrivals() >= 0);
    }
}
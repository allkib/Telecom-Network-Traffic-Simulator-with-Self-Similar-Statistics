package test;

import controller.ParameterController;
import model.SimulationParameters;
import model.TrafficModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ParameterControllerTest {
    private ParameterController controller;
    private List<String> tempFiles;
    
    @BeforeEach
    void setUp() {
        controller = new ParameterController();
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
    
    // Test setDefaults() - default parameter setting
    @Test
    void testSetDefaults() {
        SimulationParameters params = new SimulationParameters();
        controller.setDefaults(params);
        
        assertEquals(1000.0, params.getSimDuration());
        assertEquals(1.0, params.getSamplingInt());
        assertEquals(TrafficModel.ON_OFF, params.getTrafficModel());
        assertEquals(1000, params.getQueueBufferSize());
        assertEquals(50.0, params.getQueueServiceRate());
    }
    
    // Test validateParameters() - valid inputs
    @Test
    void testValidateParametersValid() {
        SimulationParameters params = SimulationParameters.defaults();
        boolean result = controller.validateParameters(params);
        
        assertTrue(result);
        assertTrue(controller.getValidationErrors().isEmpty());
    }
    
    // Test validateParameters() - invalid inputs
    @Test
    void testValidateParametersInvalid() {
        SimulationParameters params = SimulationParameters.defaults();
        params.setSimDuration(-1.0);
        
        boolean result = controller.validateParameters(params);
        
        assertFalse(result);
        List<String> errors = controller.getValidationErrors();
        assertTrue(errors.contains("simulationDuration must be > 0"));
    }
    
    // Test getValidationErrors() - error collection
    @Test
    void testGetValidationErrors() {
        SimulationParameters params = SimulationParameters.defaults();
        params.setSimDuration(-1.0);
        controller.validateParameters(params);
        
        List<String> errors = controller.getValidationErrors();
        assertFalse(errors.isEmpty());
        assertTrue(errors.contains("simulationDuration must be > 0"));
    }
    
    // Test saveParametersToFile() - file creation and format
    @Test
    void testSaveParametersToFile() throws Exception {
        SimulationParameters params = SimulationParameters.defaults();
        String filename = getTempFile("params");
        
        boolean result = controller.saveParametersToFile(params, filename);
        
        assertTrue(result);
        assertTrue(Files.exists(Paths.get(filename)));
        
        String content = Files.readString(Paths.get(filename));
        assertTrue(content.contains("simDuration="));
        assertTrue(content.contains("trafficModel=ON_OFF"));
    }
    
    // Test loadParametersFromFile() - valid file
    @Test
    void testLoadParametersFromFileValid() throws Exception {
        String paramsFile = getTempFile("load_params");
        String paramsContent = "Header\n" +
                              "simDuration,1000.0\n" +
                              "samplingInt,1.0\n" +
                              "trafficModel,FGN\n" +
                              "queueBufferSize,1000\n" +
                              "queueServiceRate,50.0\n" +
                              "hurstParameter,0.75\n";
        Files.write(Paths.get(paramsFile), paramsContent.getBytes());
        
        SimulationParameters params = controller.loadParametersFromFile(paramsFile, null);
        
        assertNotNull(params);
        assertEquals(1000.0, params.getSimDuration());
        assertEquals(TrafficModel.FGN, params.getTrafficModel());
        assertEquals(0.75, params.getHurstParameter());
    }
    
    // Test loadParametersFromFile() - invalid file
    @Test
    void testLoadParametersFromFileInvalid() {
        SimulationParameters params = controller.loadParametersFromFile("nonexistent_file_12345.txt", null);
        
        assertNull(params);
        List<String> errors = controller.getValidationErrors();
        assertFalse(errors.isEmpty());
    }
}
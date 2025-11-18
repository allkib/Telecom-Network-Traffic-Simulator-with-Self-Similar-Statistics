// All
package controller;

import model.Simulation;
import model.SimulationParameters;
import model.TrafficStatistics;
import util.FileHandler;
import util.HurstParameterCalculator;
import view.OutputFormatter;
import java.util.List;
import java.util.ArrayList;
import model.Event;
import model.TrafficModel;

// Main controller that orchestrates the simulation process.
public class SimulationController {
    private Simulation simulation;
    private ParameterController parameterController;
    private EventController eventController;
    private TrafficController trafficController;
    private final FileHandler fileHandler = new FileHandler();
    
    public SimulationController() {
        this.simulation = new Simulation();
        this.parameterController = new ParameterController();
    }
    
    public void setEventController(EventController eventController) {
        this.eventController = eventController;
    }
    
    public void setTrafficController(TrafficController trafficController) {
        this.trafficController = trafficController;
    }
    
    public boolean runSimulation(SimulationParameters params) {
        List<Event> eventLog = new ArrayList<>();
        // Validate parameters
        if (!parameterController.validateParameters(params)) {
            // Leave errors in ParameterController for view to display
            return false;
        }
        
        // Initialize simulation
        simulation.start(params.getSimDuration());
        
        if (trafficController == null || eventController == null) {
            simulation.stop();
            return false;
        }
        
        trafficController.initializeTraffic(params);

        if (params.getTrafficModel() == TrafficModel.ON_OFF) {
            eventController.processInitialEvents(trafficController.getTrafficSources());
        }
        
        // Main simulation loop
        double samplingInt = params.getSamplingInt();
        double nextSampleTime = 0.0;
        
        while (simulation.isRunning()) {
            // Add all events to an event log. Only applicable to event based models (e.g. ON_OFF).
            if (params.getTrafficModel() == TrafficModel.ON_OFF){
                while (eventController.hasEvents() && 
                        eventController.getEventQueue().peek() != null &&
                        eventController.getEventQueue().peek().getTimestamp() <= simulation.getCurrTime()) {
                    Event nextEvent = eventController.getEventQueue().peek();
                    eventController.processNextEvent(trafficController.getTrafficSources());
                    eventLog.add(nextEvent);
                }
            }

            // Sample traffic at intervals
            if (simulation.getCurrTime() >= nextSampleTime) {
                double aggregateTraffic = trafficController.calculateAggregateTraffic(simulation.getCurrTime());
                simulation.getStats().addMeasurement(simulation.getCurrTime(), aggregateTraffic);
                nextSampleTime += samplingInt;
            }

            // Determine next target time (next event, next sample, or small step)
            double curr = simulation.getCurrTime();
            double nextTarget = curr + samplingInt / 10.0; // fallback small step
            if (eventController.hasEvents() && eventController.getEventQueue().peek() != null) {
                double nextEventTime = eventController.getEventQueue().peek().getTimestamp();
                if (nextEventTime > curr) {
                    nextTarget = Math.min(nextTarget, nextEventTime);
                }
            }
            if (nextSampleTime > curr) {
                nextTarget = Math.min(nextTarget, nextSampleTime);
            }

            double timeStep = nextTarget - curr;
            if (timeStep <= 0) {
                timeStep = Math.max(1e-6, samplingInt / 1000.0);
            }
            simulation.tick(timeStep);
        }
        
        // Calculate final statistics
        simulation.getStats().calculateStatistics();
        
        HurstParameterCalculator hurstCalculator = new HurstParameterCalculator();
        List<Double> timeSeries = simulation.getStats().getTimeSeries();
        
        if (timeSeries.size() >= 10) {
            // Use R/S analysis as primary method
            double hRS = hurstCalculator.calculateHurstRS(timeSeries);
            boolean isSelfSimilar = hurstCalculator.isSelfSimilar(hRS);
            String confidence = hurstCalculator.getConfidenceLevel(hRS);
            
            OutputFormatter.printHurstParameter(hRS, "R/S Analysis", isSelfSimilar, confidence);
            
            double hVar = hurstCalculator.calculateHurstVariance(timeSeries);
            if (Math.abs(hRS - hVar) > 0.1) {
                // If methods disagree significantly, show both
                System.out.println("Note: Variance-Time method gives H = " + String.format("%.4f", hVar));
                System.out.println("      (R/S method used as primary estimate)");
            }
        }
        
        // Export CSV with timestamps and metadata
        fileHandler.writeAggregateTrafficCSV("Aggregate_Traffic.csv", 
            simulation.getStats().getTimeSeries(), 
            params.getSamplingInt(), 
            true, 
            params);
        fileHandler.writeEventLogCSV("Event_Log.csv", eventLog);
        simulation.stop();
        return true;
    }
    
    public TrafficStatistics getResults() {
        return simulation.getStats();
    }
    
    public boolean isRunning() {
        return simulation.isRunning();
    }
}
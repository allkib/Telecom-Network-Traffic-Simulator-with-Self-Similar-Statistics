// All
package controller;

import model.Simulation;
import model.SimulationParameters;
import model.TrafficStatistics;
import util.FileHandler;
import java.util.List;
import java.util.ArrayList;
import model.Event;

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
        
        trafficController.initializeSources(params);
        eventController.processInitialEvents(trafficController.getTrafficSources());
        
        // Main simulation loop
        double samplingInt = params.getSamplingInt();
        double nextSampleTime = 0.0;
        
        while (simulation.isRunning()) {
            // Process all events up to current time
            while (eventController.hasEvents() && 
                   eventController.getEventQueue().peek() != null &&
                   eventController.getEventQueue().peek().getTimestamp() <= simulation.getCurrTime()) {
                Event nextEvent = eventController.getEventQueue().peek();
                eventController.processNextEvent(trafficController.getTrafficSources());
                eventLog.add(nextEvent);
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
        
        // Export CSV (aggregate traffic only)
        fileHandler.writeAggregateTrafficCSV("output.csv", simulation.getStats().getTimeSeries());

        // Stop simulation
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
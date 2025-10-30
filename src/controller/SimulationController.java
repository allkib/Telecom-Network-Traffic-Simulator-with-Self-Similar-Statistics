package controller;

import model.Simulation;
import model.SimulationParameters;
import model.TrafficStatistics;

// Main controller that orchestrates the simulation process.
public class SimulationController {
    private Simulation simulation;
    private ParameterController parameterController;
    private EventController eventController;
    private TrafficController trafficController;
    
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
    
    public void runSimulation(SimulationParameters params) {
        // Validate parameters
        if (!parameterController.validateParameters(params)) {
            return;
        }
        
        // Initialize simulation
        simulation.start(params.getSimDuration());
        
        // Initialize Person B's components if available
        if (trafficController != null) {
            trafficController.initializeSources(params);
        }
        if (eventController != null) {
            eventController.scheduleInitialEvents(trafficController.getTrafficSources());
        }
        
        // Main simulation loop
        double samplingInt = params.getSamplingInt();
        double nextSampleTime = 0.0;
        
        while (simulation.isRunning()) {
            // Process events 
            if (eventController != null && eventController.hasEvents()) {
                while (eventController.hasEvents() && 
                       eventController.getEventQueue().peek() != null &&
                       eventController.getEventQueue().peek().getTimestamp() <= simulation.getCurrTime()) {
                    eventController.processNextEvent(trafficController.getTrafficSources());
                }
            }
            
            // Sample traffic at intervals
            if (simulation.getCurrTime() >= nextSampleTime) {
                if (trafficController != null) {
                    double aggregateTraffic = trafficController.calculateAggregateTraffic(simulation.getCurrTime());
                    simulation.getStats().addMeasurement(aggregateTraffic);
                }
                nextSampleTime += samplingInt;
            }
            
            // Advance simulation time
            // Use a small time step or advance to next event
            double timeStep = samplingInt / 10.0; // Small step
            if (eventController != null && eventController.hasEvents() && 
                eventController.getEventQueue().peek() != null) {
                double nextEventTime = eventController.getEventQueue().peek().getTimestamp();
                if (nextEventTime < simulation.getCurrTime() + timeStep) {
                    timeStep = nextEventTime - simulation.getCurrTime();
                }
            }
            simulation.tick(timeStep);
            
            // Check if simulation should end
            if (!simulation.isRunning()) {
                break;
            }
        }
        
        // Calculate final statistics
        simulation.getStats().calculateStatistics();
        
        // Stop simulation
        simulation.stop();
    }
    
    public TrafficStatistics getResults() {
        return simulation.getStats();
    }
    
    public boolean isRunning() {
        return simulation.isRunning();
    }
}
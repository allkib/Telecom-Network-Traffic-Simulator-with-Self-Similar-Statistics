// All
package model;

// Model for simulation
public class Simulation {
    private double currTime;
    private double endTime;
    private boolean running;
    private TrafficStatistics stats;

    public Simulation() {
        this.currTime = 0.0;
        this.endTime = 0.0;
        this.running = false;
        this.stats = new TrafficStatistics();
    }

    public void start(double duration) {
        this.currTime = 0.0;
        this.endTime = duration;
        this.running = true;
        this.stats.clear();
    }

    public void stop() {
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public double getCurrTime() {
        return currTime;
    }

    public void tick(double dt) {
        if (!running) return;
        currTime += dt;
        if (currTime >= endTime) {
            currTime = endTime;
            running = false;
        }
    }

    public TrafficStatistics getStats() {
        return stats;
    }
}
// All
package model;

// Model for simulation parameters
public class SimulationParameters {
    private double simDuration;
    private int numSources;
    private double paretoAlpha;
    private double paretoMinVal;
    private double samplingInt;
    private Long seed; // optional; null means no fixed seed
    private TrafficModel trafficModel;
    private double hurstParameter;

    public SimulationParameters() {
    }

    public static SimulationParameters defaults() {
        SimulationParameters params = new SimulationParameters();
        params.setSimDuration(1000.0);
        params.setNumSources(100);
        params.setParetoAlpha(1.5);
        params.setParetoMinVal(1.0);
        params.setSamplingInt(1.0);
        params.setSeed(null);
        params.setTrafficModel(TrafficModel.ON_OFF);
        params.setHurstParameter(0.75);
        return params;
    }

    public double getSimDuration() {
        return simDuration;
    }

    public void setSimDuration(double simDuration) {
        this.simDuration = simDuration;
    }

    public int getNumSources() {
        return numSources;
    }

    public void setNumSources(int numSources) {
        this.numSources = numSources;
    }

    public double getParetoAlpha() {
        return paretoAlpha;
    }

    public void setParetoAlpha(double paretoAlpha) {
        this.paretoAlpha = paretoAlpha;
    }

    public double getParetoMinVal() {
        return paretoMinVal;
    }

    public TrafficModel getTrafficModel() {
        return trafficModel;
    }

    public double getHurstParameter() {
        return hurstParameter;
    }

    public void setParetoMinVal(double paretoMinVal) {
        this.paretoMinVal = paretoMinVal;
    }

    public double getSamplingInt() {
        return samplingInt;
    }

    public void setSamplingInt(double samplingInt) {
        this.samplingInt = samplingInt;
    }

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public void setTrafficModel(TrafficModel trafficModel) {
        this.trafficModel = trafficModel;
    }

    public void setHurstParameter(double hurstParameter) {
        this.hurstParameter = hurstParameter;
    }
}
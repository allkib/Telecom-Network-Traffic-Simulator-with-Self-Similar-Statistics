// All
package model;

import java.util.List;

// Model for simulation parameters
public class SimulationParameters {
    private double simDuration;
    private double samplingInt;
    private Long seed; // optional; null means no fixed seed
    private TrafficModel trafficModel;
    private double hurstParameter;
    private List<SourceProfile> sourceProfiles;

    public SimulationParameters() {
        this.sourceProfiles = new java.util.ArrayList<>();
    }

    public static SimulationParameters defaults() {
        SimulationParameters params = new SimulationParameters();
        params.setSimDuration(1000.0);
        params.setSamplingInt(1.0);
        params.setSeed(null);
        params.setTrafficModel(TrafficModel.ON_OFF);
        params.setHurstParameter(0.75);
        params.addSourceProfile(new SourceProfile("Default Profile", 10, 0.1, 1.5, 1.0, 1.5, 1.0));
        return params;
    }

    public List<SourceProfile> getSourceProfiles() {
        return sourceProfiles;
    }

    public void addSourceProfile(SourceProfile profile) {
        this.sourceProfiles.add(profile);
    }

    public void clearSourceProfiles() {
        this.sourceProfiles.clear();
    }
    
    public double getSimDuration() {
        return simDuration;
    }

    public void setSimDuration(double simDuration) {
        this.simDuration = simDuration;
    }

    public int getNumSources() {
        if (sourceProfiles == null) return 0;
        return sourceProfiles.stream().mapToInt(SourceProfile::getNumberOfSources).sum();
    }

    public TrafficModel getTrafficModel() {
        return trafficModel;
    }

    public double getHurstParameter() {
        return hurstParameter;
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
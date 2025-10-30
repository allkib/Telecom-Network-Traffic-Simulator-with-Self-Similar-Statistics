// All
package model;

import java.util.ArrayList;
import java.util.List;

// Model for traffic statistics
public class TrafficStatistics {
    private final List<Double> timeSeries = new ArrayList<>();
    private double avgRate;
    private double peakRate;
    private double totTraffic;

    public TrafficStatistics() {
    }

    public void addMeasurement(double rate) {
        timeSeries.add(rate);
    }

    // Time-aware overload for future expansion; currently stores rate only
    public void addMeasurement(double time, double rate) {
        timeSeries.add(rate);
    }

    public void calculateStatistics() {
        if (timeSeries.isEmpty()) {
            avgRate = 0.0;
            peakRate = 0.0;
            totTraffic = 0.0;
            return;
        }

        double sum = 0.0;
        double max = Double.NEGATIVE_INFINITY;
        for (double r : timeSeries) {
            sum += r;
            if (r > max) {
                max = r;
            }
        }
        totTraffic = sum;
        avgRate = sum / timeSeries.size();
        peakRate = max;
    }

    public void clear() {
        timeSeries.clear();
        avgRate = 0.0;
        peakRate = 0.0;
        totTraffic = 0.0;
    }

    public List<Double> getTimeSeries() {
        return timeSeries;
    }

    public double getAvgRate() {
        return avgRate;
    }

    public double getPeakRate() {
        return peakRate;
    }

    public double getTotTraffic() {
        return totTraffic;
    }
}
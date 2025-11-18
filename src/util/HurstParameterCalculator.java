package util;

import java.util.List;

// Calculates Hurst parameter using various methods to measure self-similarity in traffic data.
// Hurst parameter H ranges from 0 to 1:
// - H = 0.5: Random process (no self-similarity)
// - H > 0.5: Self-similar (long-range dependence)
// - H < 0.5: Anti-persistent (mean-reverting)
public class HurstParameterCalculator {
    
    public double calculateHurstRS(List<Double> timeSeries) {
        if (timeSeries == null || timeSeries.size() < 10) {
            return 0.5;
        }
        
        int n = timeSeries.size();
        int maxWindowSize = Math.min(n / 4, 100);
        int minWindowSize = Math.max(4, n / 50);
        
        List<Double> logRS = new java.util.ArrayList<>();
        List<Double> logN = new java.util.ArrayList<>();
        
        for (int windowSize = minWindowSize; windowSize <= maxWindowSize; windowSize += Math.max(1, windowSize / 10)) {
            double rs = calculateRSForWindow(timeSeries, windowSize);
            if (rs > 0 && Double.isFinite(rs)) {
                logRS.add(Math.log(rs));
                logN.add(Math.log(windowSize));
            }
        }
        
        if (logRS.size() < 3) {
            return 0.5; 
        }
        
        return calculateSlope(logN, logRS);
    }
    
    private double calculateRSForWindow(List<Double> timeSeries, int windowSize) {
        int n = timeSeries.size();
        int numWindows = n / windowSize;
        if (numWindows < 1) return 0.0;
        
        double sumRS = 0.0;
        int validWindows = 0;
        
        for (int i = 0; i < numWindows; i++) {
            int start = i * windowSize;
            int end = Math.min(start + windowSize, n);
            
            double rs = calculateRSForSubseries(timeSeries.subList(start, end));
            if (rs > 0 && Double.isFinite(rs)) {
                sumRS += rs;
                validWindows++;
            }
        }
        
        return validWindows > 0 ? sumRS / validWindows : 0.0;
    }
    
    private double calculateRSForSubseries(List<Double> series) {
        if (series.isEmpty()) return 0.0;
        
        double mean = 0.0;
        for (Double value : series) {
            mean += value;
        }
        mean /= series.size();
        
        double[] deviations = new double[series.size()];
        double[] cumulative = new double[series.size()];
        
        for (int i = 0; i < series.size(); i++) {
            deviations[i] = series.get(i) - mean;
            cumulative[i] = (i == 0) ? deviations[i] : cumulative[i-1] + deviations[i];
        }
        
        double minCum = cumulative[0];
        double maxCum = cumulative[0];
        for (double c : cumulative) {
            minCum = Math.min(minCum, c);
            maxCum = Math.max(maxCum, c);
        }
        double R = maxCum - minCum;
        
        double variance = 0.0;
        for (double d : deviations) {
            variance += d * d;
        }
        variance /= series.size();
        double S = Math.sqrt(variance);
        
        if (S == 0.0 || R == 0.0) {
            return 0.0;
        }
        
        return R / S;
    }
    
    private double calculateSlope(List<Double> x, List<Double> y) {
        if (x.size() != y.size() || x.size() < 2) {
            return 0.5;
        }
        
        int n = x.size();
        double sumX = 0.0, sumY = 0.0, sumXY = 0.0, sumX2 = 0.0;
        
        for (int i = 0; i < n; i++) {
            double xi = x.get(i);
            double yi = y.get(i);
            sumX += xi;
            sumY += yi;
            sumXY += xi * yi;
            sumX2 += xi * xi;
        }
        
        double denominator = n * sumX2 - sumX * sumX;
        if (Math.abs(denominator) < 1e-10) {
            return 0.5;
        }
        
        double slope = (n * sumXY - sumX * sumY) / denominator;
        
        return Math.max(0.0, Math.min(1.0, slope));
    }
    
    // Calculates Hurst parameter using variance-time plot method
    // Aggregates data at different scales, calculates variance, fits log-log plot
    public double calculateHurstVariance(List<Double> timeSeries) {
        if (timeSeries == null || timeSeries.size() < 10) {
            return 0.5;
        }
        
        int n = timeSeries.size();
        int maxAggregation = Math.min(n / 4, 64);
        int minAggregation = 2;
        
        List<Double> logVariance = new java.util.ArrayList<>();
        List<Double> logM = new java.util.ArrayList<>();
        
        for (int m = minAggregation; m <= maxAggregation; m *= 2) {
            double variance = calculateVarianceForAggregation(timeSeries, m);
            if (variance > 0 && Double.isFinite(variance)) {
                logVariance.add(Math.log(variance));
                logM.add(Math.log(m));
            }
        }
        
        if (logVariance.size() < 2) {
            return 0.5;
        }
        
        // Slope = 2H - 2, so H = (slope + 2) / 2
        double slope = calculateSlope(logM, logVariance);
        double h = (slope + 2.0) / 2.0;
        
        return Math.max(0.0, Math.min(1.0, h));
    }
    
    // Calculates variance of aggregated time-series (averages m consecutive values)
    private double calculateVarianceForAggregation(List<Double> timeSeries, int m) {
        int n = timeSeries.size();
        int numAggregated = n / m;
        if (numAggregated < 2) return 0.0;
        
        List<Double> aggregated = new java.util.ArrayList<>();
        for (int i = 0; i < numAggregated; i++) {
            double sum = 0.0;
            for (int j = 0; j < m; j++) {
                int idx = i * m + j;
                if (idx < n) {
                    sum += timeSeries.get(idx);
                }
            }
            aggregated.add(sum / m);
        }
        
        if (aggregated.isEmpty()) return 0.0;
        
        double mean = 0.0;
        for (Double value : aggregated) {
            mean += value;
        }
        mean /= aggregated.size();
        
        double variance = 0.0;
        for (Double value : aggregated) {
            double diff = value - mean;
            variance += diff * diff;
        }
        variance /= aggregated.size();
        
        return variance;
    }
    
    public boolean isSelfSimilar(double hurstParameter) {
        return hurstParameter > 0.5;
    }
    
    public String getConfidenceLevel(double hurstParameter) {
        double distance = Math.abs(hurstParameter - 0.5);
        if (distance > 0.2) {
            return "High";
        } else if (distance > 0.1) {
            return "Medium";
        } else {
            return "Low";
        }
    }
}
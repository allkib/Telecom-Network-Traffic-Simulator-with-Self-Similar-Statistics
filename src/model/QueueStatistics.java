// All
package model;

// Statistics for network queue performance
public class QueueStatistics {
    private double avgQueueLength;
    private double maxQueueLength;
    private long overflowCount;
    private double overflowPercentage;
    private double totalArrivals;
    
    public QueueStatistics(double avgQueueLength, double maxQueueLength, 
                          long overflowCount, double overflowPercentage, 
                          double totalArrivals) {
        this.avgQueueLength = avgQueueLength;
        this.maxQueueLength = maxQueueLength;
        this.overflowCount = overflowCount;
        this.overflowPercentage = overflowPercentage;
        this.totalArrivals = totalArrivals;
    }
    
    public double getAvgQueueLen() {
        return avgQueueLength;
    }
    
    public double getMaxQueueLen() {
        return maxQueueLength;
    }
    
    public long getOverflowCount() {
        return overflowCount;
    }
    
    public double getOverflowPercent() {
        return overflowPercentage;
    }
    
    public double getTotalArrivals() {
        return totalArrivals;
    }
}
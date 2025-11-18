// All
package model;

// Network queue model for observing traffic impact
public class NetworkQueue {
    private int bufferSize;
    private double serviceRate;
    private double currQueueLength;
    private double totalArrivals;
    private long totalOverflows;
    private double maxQueueLength;
    private double sumQueueLength;
    private long sampleCount;
    
    public NetworkQueue(int bufferSize, double serviceRate) {
        this.bufferSize = bufferSize;
        this.serviceRate = serviceRate;
        this.currQueueLength = 0.0;
        this.totalArrivals = 0.0;
        this.totalOverflows = 0;
        this.maxQueueLength = 0.0;
        this.sumQueueLength = 0.0;
        this.sampleCount = 0;
    }
    
    // Process arrivals and service for a time step
    // trafficRate: incoming traffic rate (packets per time unit)
    // timeStep: duration of this time step
    public void processArrival(double trafficRate, double timeStep) {
        double arrivals = trafficRate * timeStep;
        totalArrivals += arrivals;
        
        double availableSpace = bufferSize - currQueueLength;
        if (arrivals <= availableSpace) {
            currQueueLength += arrivals;
        } else {
            // Buffer overflow
            currQueueLength = bufferSize;
            double overflow = arrivals - availableSpace;
            totalOverflows += (long)Math.ceil(overflow);
        }
        
        double serviced = Math.min(serviceRate * timeStep, currQueueLength);
        currQueueLength -= serviced;
        
        if (currQueueLength > maxQueueLength) {
            maxQueueLength = currQueueLength;
        }
        sumQueueLength += currQueueLength;
        sampleCount++;
    }
    
    public QueueStatistics getStats() {
        double avgQueueLength = sampleCount > 0 ? sumQueueLength / sampleCount : 0.0;
        double overflowPercentage = totalArrivals > 0 ? (totalOverflows / totalArrivals) * 100.0 : 0.0;
        
        return new QueueStatistics(
            avgQueueLength,
            maxQueueLength,
            totalOverflows,
            overflowPercentage,
            totalArrivals
        );
    }
    
    public void reset() {
        currQueueLength = 0.0;
        totalArrivals = 0.0;
        totalOverflows = 0;
        maxQueueLength = 0.0;
        sumQueueLength = 0.0;
        sampleCount = 0;
    }
    
    public int getBufSize() {
        return bufferSize;
    }
    
    public double getServiceRate() {
        return serviceRate;
    }
    
    public double getCurrQueueLength() {
        return currQueueLength;
    }
}
/** 
 * @author: Clarence
 * 
 * Models one ON/OFF source with duration driven by distributions.
 */

package model;

import util.RandomNumberGenerator;

public class TrafficSource {

    private final int sourceID;
    private final Distribution onDuration;
    private final Distribution offDuration;
    private final RandomNumberGenerator rng;
    private final double onRate;

    private boolean isOn;
    private double nextTransitionTime;

    public TrafficSource(int sourceID, Distribution onDuration, Distribution offDuration, RandomNumberGenerator rng, double onRate) {
        this.sourceID = sourceID;
        this.onDuration = onDuration;
        this.offDuration = offDuration;
        this.onRate = onRate;
        this.rng = rng;
        this.isOn = false;
        this.nextTransitionTime = 0.0;
    }

    public void initialState(boolean startOn, double startTime) {
        this.isOn = startOn;
        this.nextTransitionTime = startTime;
    }

    public int getSourceID() {
        return sourceID;
    }

    public double getOnRate() {
        return onRate;
    }

    public boolean isOn() {
        return isOn;
    }

    public double getNextTransitionTime() {
        return nextTransitionTime;
    }

    public double getInstantRate() {
        return isOn ? onRate : 0.0;
    }

    // Create the first event/next event
    public Event createNextEvent(double currentTime) {
        double dt = nextDuration();
        double t = currentTime + dt;
        return isOn ? Event.createOffEvent(t, sourceID) : Event.createOnEvent(t, sourceID);
    }

    // Apply an event when it reaches its scheduled time
    public void applyEvent(Event e) {
        if (e == null) {
            throw new IllegalArgumentException("Event is null");
        }

        if (e.getSourceID() != this.sourceID) {
            throw new IllegalArgumentException("Event sourceID mismatch");
        }

        this.isOn = (e.getType() == Event.EventType.ON);
        this.nextTransitionTime = e.getTimestamp();
    }

    // Helper
    public double nextDuration() {
        double d = isOn ? offDuration.sample(rng) : onDuration.sample(rng);

        if (d <= 0.0 || Double.isInfinite(d) || Double.isNaN(d)) {
            d = 1e-5;  // Arbitrary small positive value
        }

        return d;
    }
}
/**
 * @author: Clarence
 * 
 * Immutable events ordered by timestamp, then sourceID, then type. 
 */
package model;

public class Event implements Comparable<Event> {
    private double timestamp;
    private final int sourceID;
    private final EventType type;

    public enum EventType {
        ON,
        OFF
    }

    public Event(double timestamp, int sourceId, EventType type) {
        this.timestamp = timestamp;
        this.sourceID = sourceId;
        this.type = type;
    }

    public static Event createOnEvent(double timestamp, int sourceId) {
        return new Event(timestamp, sourceId, EventType.ON);
    }

    public static Event createOffEvent(double timestamp, int sourceId) {
        return new Event(timestamp, sourceId, EventType.OFF);
    }
    
    public double getTimestamp() {
        return timestamp;
    }

    public int getSourceID() {
        return sourceID;
    }

    public EventType getType() {
        return type;
    }

    public String eventToString(){
        return "Event " + "timestamp=" + timestamp + ", sourceID=" + sourceID + ", type=" + type ;
    }

    @Override
    public int compareTo(Event other) {
        if (this.timestamp != other.timestamp) {
            return Double.compare(this.timestamp, other.timestamp);
        }
        if (this.sourceID != other.sourceID) {
            return Integer.compare(this.sourceID, other.sourceID);
        }
        return this.type.compareTo(other.type);
    }

    @Override
    public boolean equals(Object o){
        Event that = (Event) o;
        return this.timestamp == that.timestamp && this.sourceID == that.sourceID && this.type == that.type;
    }
}
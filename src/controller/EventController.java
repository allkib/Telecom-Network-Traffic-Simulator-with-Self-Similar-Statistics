/**
 *  @author: Clarence
 * 
 * Manages event flow
 * 
 * @processInitialEvents: for each traffic source, create its initial event and add this event to the event queue
 * @processNextEvent: pop the next event from the event queue, apply it to the corresponding Traffic Source, then create and add the next event for that source
 */

package controller;

import java.util.List;

import model.Event;
import model.EventQueue;
import model.TrafficSource;

public class EventController {

    private final EventQueue queue = new EventQueue();

    public boolean hasEvents() {
        return !queue.isEmpty();
    }

    public EventQueue getEventQueue() {
        return queue;
    }

    public void processInitialEvents(List<TrafficSource> sources){
        if (sources == null) {
            throw new IllegalArgumentException("Sources list is null");
        }

        final double t0 = 0.0;

        for (TrafficSource s : sources) {
            s.initialState(false, t0);
            Event firstEvent = s.createNextEvent(t0);
            queue.addEvent(firstEvent);
        }

    }

    public void processNextEvent(List<TrafficSource> sources) {
        if (queue.isEmpty()) return;
        Event e = queue.poll();
        if (e == null) return;

        int sourceID = e.getSourceID();
        if (sourceID < 0 || sourceID >= sources.size()) throw new IllegalArgumentException("Invalid sourceID");
    
        TrafficSource s = sources.get(sourceID);
        s.applyEvent(e);

        Event nextEvent = s.createNextEvent(e.getTimestamp());
        queue.addEvent(nextEvent);
    }
}
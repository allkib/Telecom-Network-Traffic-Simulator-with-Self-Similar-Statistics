/*
 * @author: Clarence
 * 
 * Event queue to manage simulation events in chronological order.
 * PriorityQueue was chosen as it takes O(log n) time for adding and removing events since it uses a binary heap.
 * PriorityQueue accepts equal keys.
 */

package model;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Collection;

public class EventQueue {

    private final Queue<Event> pq = new PriorityQueue<>();

    public void addEvent(Event e) {
        pq.add(e);
    }

    public void addAllEvents(Collection<Event> events) {
        for (Event e : events) {
            pq.add(e);
        }
    }

    public Event peek() {
        return pq.peek();
    }

    public Event poll() {
        return pq.poll();
    }

    public boolean isEmpty() {
        return pq.isEmpty();
    }

    public int size() {
        return pq.size();
    }

    public void clear() {
        pq.clear();
    }
}


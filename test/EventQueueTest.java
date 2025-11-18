/*
 * @author: Clarence
 */

package test;

import model.Event;
import model.EventQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EventQueueTest {

    private EventQueue eventQueue;

    @BeforeEach
    void setup() {
        eventQueue = new EventQueue();
    }

    @Test
    void testPollOrderByTimestamp() {
        Event e1 = Event.createOnEvent(10.0, 1);
        Event e2 = Event.createOffEvent(5.0, 2);
        Event e3 = Event.createOnEvent(15.0, 3);

        eventQueue.addEvent(e1);
        eventQueue.addEvent(e2);
        eventQueue.addEvent(e3);

        assertEquals(e2, eventQueue.poll());
        assertEquals(e1, eventQueue.poll());
        assertEquals(e3, eventQueue.poll());
    }

    @Test
    void testPollOrderByID() {
        Event e1 = Event.createOnEvent(5.0, 1);
        Event e2 = Event.createOffEvent(5.0, 3);
        Event e3 = Event.createOnEvent(5.0, 2);

        eventQueue.addEvent(e1);
        eventQueue.addEvent(e2);
        eventQueue.addEvent(e3);

        assertEquals(e1, eventQueue.poll());
        assertEquals(e3, eventQueue.poll());
        assertEquals(e2, eventQueue.poll());
    }

    @Test
    void testPollOrderByEventType() {
        Event e1 = new Event(10.0, 1, Event.EventType.ON);
        Event e2 = new Event(10.0, 1, Event.EventType.OFF);

        eventQueue.addEvent(e2);
        eventQueue.addEvent(e1);

        assertEquals(e1, eventQueue.poll());
        assertEquals(e2, eventQueue.poll());
    }

    @Test
    void testClear() {
        eventQueue.addEvent(Event.createOnEvent(1.0,1));
        eventQueue.addEvent(Event.createOffEvent(2.0,2));

        eventQueue.clear();

        assertTrue(eventQueue.isEmpty());
    }
}
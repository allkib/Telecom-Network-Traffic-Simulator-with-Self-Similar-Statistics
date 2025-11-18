/**
 * @author: Clarence
 */

package test;

import controller.EventController;
import model.Distribution;
import model.Event;
import model.TrafficSource;
import util.RandomNumberGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class EventControllerTest {

    static class TestRNG extends RandomNumberGenerator {
        public TestRNG() {
            super(123L);
        }

        @Override
        public double randomDouble() {
            return 0.5; 
        }
    }

    static class TestDistribution implements Distribution {
        private final double fixedValue;

        public TestDistribution(double fixedValue) {
            this.fixedValue = fixedValue;
        }

        @Override
        public double sample(RandomNumberGenerator rng) {
            return fixedValue;
        }
    }

    @Test
    void testProcessInitialEvents() {
        EventController controller = new EventController();
        List<TrafficSource> sources = new ArrayList<>();

        sources.add(new TrafficSource(0, new TestDistribution(10.0), new TestDistribution(5.0), new TestRNG(), 100.0));
        sources.add(new TrafficSource(1, new TestDistribution(20.0), new TestDistribution(10.0), new TestRNG(), 200.0));

        controller.processInitialEvents(sources);

        assertEquals(2, controller.getEventQueue().size());

        Event e1 = controller.getEventQueue().poll();
        Event e2 = controller.getEventQueue().poll();

        // Order of events dependent on timestamp, then ID
        assertEquals(10.0, e1.getTimestamp());
        assertEquals(0, e1.getSourceID());
        assertEquals(Event.EventType.ON, e1.getType());

        assertEquals(20.0, e2.getTimestamp());
        assertEquals(1, e2.getSourceID());
        assertEquals(Event.EventType.ON, e2.getType());
    }

    @Test
    void testProcessNextEvent() {
        EventController controller = new EventController();
        List<TrafficSource> sources = new ArrayList<>();

        sources.add(new TrafficSource(0, new TestDistribution(10), new TestDistribution(5), new TestRNG(), 100.0));

        controller.processInitialEvents(sources);

        controller.processNextEvent(sources);

        assertEquals(1, controller.getEventQueue().size());
        Event nextEvent = controller.getEventQueue().poll();

        assertEquals(15.0, nextEvent.getTimestamp());
        assertEquals(Event.EventType.OFF, nextEvent.getType());
        assertEquals(0, nextEvent.getSourceID());

        assertTrue(sources.get(0).isOn());
    }

    @Test
    void testProcessNextEventEmptyQueue() {
        EventController controller = new EventController();
        List<TrafficSource> sources = new ArrayList<>();

        assertDoesNotThrow(() -> controller.processNextEvent(sources));
    }

    @Test 
    void testInvalidID() {
        EventController controller = new EventController();
        List<TrafficSource> sources = new ArrayList<>();

        sources.add(new TrafficSource(0, new TestDistribution(10), new TestDistribution(5), new TestRNG(), 100.0));
        controller.getEventQueue().addEvent(Event.createOnEvent(10.0, 50));

        assertThrows(IllegalArgumentException.class, () -> controller.processNextEvent(sources));
    }

    @Test 
    void testNullSourcesList() {
        EventController controller = new EventController();

        assertThrows(IllegalArgumentException.class, () -> controller.processInitialEvents(null));
    }

    @Test
    void testPriorityQueueOrderByID() {
        EventController controller = new EventController();
        List<TrafficSource> sources = new ArrayList<>();

        sources.add(new TrafficSource(2, new TestDistribution(10.0), new TestDistribution(5.0), new TestRNG(), 100.0));
        sources.add(new TrafficSource(1, new TestDistribution(10.0), new TestDistribution(5.0), new TestRNG(), 100.0));

        controller.processInitialEvents(sources);

        Event e1 = controller.getEventQueue().poll();
        assertEquals(1, e1.getSourceID());
        assertEquals(10.0, e1.getTimestamp());

        Event e2 = controller.getEventQueue().poll();
        assertEquals(2, e2.getSourceID());
        assertEquals(10.0, e2.getTimestamp());
    }

    @Test
    void testPriorityQueueOrderByTimestamp() {
        EventController controller = new EventController();
        List<TrafficSource> sources = new ArrayList<>();

        sources.add(new TrafficSource(0, new TestDistribution(20.0), new TestDistribution(5.0), new TestRNG(), 100.0));
        sources.add(new TrafficSource(1, new TestDistribution(10.0), new TestDistribution(5.0), new TestRNG(), 100.0));

        controller.processInitialEvents(sources);

        Event e1 = controller.getEventQueue().poll();
        assertEquals(1, e1.getSourceID());
        assertEquals(10.0, e1.getTimestamp());

        Event e2 = controller.getEventQueue().poll();
        assertEquals(0, e2.getSourceID());
        assertEquals(20.0, e2.getTimestamp());
    }
}
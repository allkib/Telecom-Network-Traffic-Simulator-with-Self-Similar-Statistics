/**
 * @author: Clarence
 */

package test;

import model.Distribution;
import model.Event;
import model.TrafficSource;
import util.RandomNumberGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TrafficSourceTest {

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
    void testConstructorDefaults() {
        int id = 3;
        double onRate = 100.0;

        TestDistribution onDist = new TestDistribution(10.0);
        TestDistribution offDist = new TestDistribution(5.0);

        TrafficSource source = new TrafficSource(id, onDist, offDist, new TestRNG(), onRate);

        assertEquals(id, source.getSourceID());
        assertEquals(onRate, source.getOnRate());
        assertFalse(source.isOn());
        assertEquals(0.0, source.getNextTransitionTime());
    }

    @Test
    void testCreateNextOffEvent() {
        TestDistribution onDist = new TestDistribution(10.0);
        TestDistribution offDist = new TestDistribution(5.0);
        TrafficSource source = new TrafficSource(1, onDist, offDist, new TestRNG(), 100.0);

        source.initialState(true, 0.0);
        double currentTime = 100.0;

        Event e = source.createNextEvent(currentTime);

        assertEquals(Event.EventType.OFF, e.getType());

        assertEquals(5.0 + currentTime, e.getTimestamp());
    }

    @Test
    void testCreateNextOnEvent() {
        TestDistribution onDist = new TestDistribution(10.0);
        TestDistribution offDist = new TestDistribution(5.0);
        TrafficSource source = new TrafficSource(1, onDist, offDist, new TestRNG(), 100.0);

        source.initialState(false, 0.0);
        double currentTime = 100.0;

        Event e = source.createNextEvent(currentTime);

        assertEquals(Event.EventType.ON, e.getType());

        assertEquals(10.0 + currentTime, e.getTimestamp());
    }

    @Test
    void testApplyEvent() {
        TrafficSource source = new TrafficSource(1, new TestDistribution(10), new TestDistribution(5), new TestRNG(), 100.0);

        Event onEvent = Event.createOnEvent(20.0, 1);
        source.applyEvent(onEvent);

        assertTrue(source.isOn());
        assertEquals(20.0, source.getNextTransitionTime());

        Event offEvent = Event.createOffEvent(50.0, 1);
        source.applyEvent(offEvent);

        assertFalse(source.isOn());
        assertEquals(50.0, source.getNextTransitionTime());
    }

    @Test
    void testGetInstantRate(){
        double onRate = 50.0;
        TrafficSource source = new TrafficSource(2, new TestDistribution(8), new TestDistribution(4), new TestRNG(), onRate);

        assertEquals(0.0, source.getInstantRate(0.0));

        source.initialState(true, 0.0);
        assertEquals(onRate, source.getInstantRate(50.0));
    }
    
    @Test
    void testInvalidEventID() {
        TrafficSource source = new TrafficSource(1, new TestDistribution(10), new TestDistribution(5), new TestRNG(), 100.0);

        Event wrongIdEvent = Event.createOnEvent(10.0, 2);
        assertThrows(IllegalArgumentException.class, () -> source.applyEvent(wrongIdEvent));
    }

    @Test
    void testNextDurationConstraints() {
        TestDistribution invalidDist = new TestDistribution(-1.0);
        TrafficSource source = new TrafficSource(1, invalidDist, new TestDistribution(5), new TestRNG(), 100.0);

        assertEquals(1e-5, source.nextDuration(), 1e-9);
    }

    @Test
    void testInvalidEvents() {
        TrafficSource source = new TrafficSource(1, new TestDistribution(10), new TestDistribution(5), new TestRNG(), 100.0);

        assertThrows(IllegalArgumentException.class, () -> source.applyEvent(null));
    }
}
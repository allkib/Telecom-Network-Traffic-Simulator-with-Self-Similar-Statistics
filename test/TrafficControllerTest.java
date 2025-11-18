/*
 * @author: Clarence
 */

package test;

import org.junit.jupiter.api.BeforeEach;

import controller.TrafficController;
import model.SimulationParameters;
import model.SourceProfile;
import model.TrafficModel;
import model.TrafficSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TrafficControllerTest {

    private TrafficController trafficController;
    private SimulationParameters params;

    @BeforeEach
    void setup() {
        trafficController = new TrafficController();
        params = new SimulationParameters();

        params.setSimDuration(10.0);
        params.setSeed(123L);
        params.setSamplingInt(1.0);
    }

    @Test
    void testInitializeOnOffTraffic() {
        params.setTrafficModel(TrafficModel.ON_OFF);

        params.addSourceProfile(new SourceProfile("TestProfile", 5, 10.0, 1.5, 5.0, 1.5, 100.0));

        trafficController.initializeTraffic(params);

        List<TrafficSource> sources = trafficController.getTrafficSources();
        assertEquals(5, sources.size());
        assertEquals(5, sources.size());
    }

    @Test
    void testInitializeFgnTraffic() {
        params.setTrafficModel(TrafficModel.FGN);
        params.setHurstParameter(0.8);

        trafficController.initializeTraffic(params);

        List<TrafficSource> sources = trafficController.getTrafficSources();
        assertNotNull(sources);
        assertTrue(sources.isEmpty());
    }

    @Test
    void testCalculateAggregateOnOffTraffic() {
        params.setTrafficModel(TrafficModel.ON_OFF);

        double rate1 = 10.0;
        double rate2 = 20.0;
        params.clearSourceProfiles();
        params.addSourceProfile(new SourceProfile("Source1", 1, rate1, 1.5, 1.0, 1.5, 1.0));
        params.addSourceProfile(new SourceProfile("Source2", 1, rate2, 1.5, 1.0, 1.5, 1.0));

        trafficController.initializeTraffic(params);
        List<TrafficSource> sources = trafficController.getTrafficSources();
        assertEquals(2, sources.size());

        sources.get(0).initialState(true, 0.0);
        sources.get(1).initialState(false, 0.0);

        assertEquals(rate1, trafficController.calculateAggregateTraffic(0.0), 1e-6);

        sources.get(0).initialState(true, 0.0);
        sources.get(1).initialState(true, 0.0);

        assertEquals(rate1 + rate2, trafficController.calculateAggregateTraffic(0.0), 1e-6);
    }

    @Test
    void testCalculateAggregateFgnTraffic() {
        params.setTrafficModel(TrafficModel.FGN);
        params.setHurstParameter(0.7);
        params.setSimDuration(100.0);
        params.setSamplingInt(1.0);
        params.setSeed(123L);

        trafficController.initializeTraffic(params);

        double val1 = trafficController.calculateAggregateTraffic(5.1);
        double val2 = trafficController.calculateAggregateTraffic(5.9);
        assertEquals(val1, val2);
    }
    
    @Test
    void testCalculateAggregateFgnTrafficBoundaries() {
        params.setTrafficModel(TrafficModel.FGN);
        params.setHurstParameter(0.7);
        params.setSimDuration(100.0);
        params.setSamplingInt(1.0);
        params.setSeed(123L);

        trafficController.initializeTraffic(params);

        assertDoesNotThrow(() -> trafficController.calculateAggregateTraffic(0.0));
        assertDoesNotThrow(() -> trafficController.calculateAggregateTraffic(100.0 - 1e-9));

        assertThrows(IllegalArgumentException.class, () -> trafficController.calculateAggregateTraffic(101.0));
    }

    @Test
    void testTransformFgnToTrafficRates() {
        double[] seq = {-1.0, 1.0};
        double averageRate = 50.0;

        double[] result = trafficController.transformFgnToTrafficRates(seq, averageRate);

        assertEquals(2, result.length);
        assertEquals(0.0, result[0], 1e-6);
        assertEquals(100.0, result[1], 1e-6);
    }

    @Test
    void testTransformFgnToTrafficRatesZeros() {
        double[] seq = {0.0, 0.0};
        double averageRate = 50.0;

        double[] result = trafficController.transformFgnToTrafficRates(seq, averageRate);

        assertEquals(0.0, result[0], 1e-6 );
    }
}

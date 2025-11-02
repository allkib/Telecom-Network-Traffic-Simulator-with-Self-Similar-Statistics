/**
 * @author: Clarence
 * 
 * Manages TrafficSource objects and provide aggregate traffic.
 */

 package controller;

 import java.util.ArrayList;
 import java.util.List;

 import model.Distribution;
 import model.ParetoDistribution;
 import model.TrafficSource;
 import model.SimulationParameters;
 import util.RandomNumberGenerator;

public class TrafficController {

    private final List<TrafficSource> sources = new ArrayList<>();

    public List<TrafficSource> getTrafficSources() {
        return sources;
    }

    // Create n sources using Pareto distribution.
    public void initializeSources(SimulationParameters params) {
        sources.clear();

        if (params.getNumSources() <= 0) {
            throw new IllegalArgumentException("Number of sources must be positive");
        }

        final int n = params.getNumSources();
        final double xm = params.getParetoMinVal();
        final double alpha = params.getParetoAlpha();
        final Long seed = params.getSeed();

        Distribution onDist = new ParetoDistribution(xm, alpha);
        Distribution offDist = new ParetoDistribution(xm, alpha);

        // onRate can be refactored into simulation parameters in the future
        final double onRate = 1.0;

        for (int i  = 0; i < n; i++) {
            Long seedPerSource = (seed == null) ? null : (seed + i);
            RandomNumberGenerator rng = new RandomNumberGenerator(seedPerSource);

            TrafficSource src = new TrafficSource(i, onDist, offDist, rng, onRate);
            sources.add(src);
        }
    }

    // Sum instantaneous rates across all sources. Basically, the number of ON sources multiplied by the onRate.
    public double calculateAggregateTraffic(double timestamp) {
        double totalRate = 0.0;
        for (TrafficSource s : sources) {
            totalRate += s.getInstantRate(timestamp);
        }
        return totalRate;
    }
}
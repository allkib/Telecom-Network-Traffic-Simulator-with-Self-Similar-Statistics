/**
 * @author: Clarence
 * 
 * Manages TrafficSource objects and provide aggregate traffic.
 */

package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import model.Distribution;
import model.ParetoDistribution;
import model.TrafficSource;
import model.SimulationParameters;
import model.TrafficModel;
import model.SourceProfile;
import util.RandomNumberGenerator;
import util.FractionalGaussianNoiseGenerator;

public class TrafficController {

    private final List<TrafficSource> sources = new ArrayList<>();
    private SimulationParameters params;
    private TrafficModel currentModel;
    private double[] fgnTrafficRates;

    public List<TrafficSource> getTrafficSources() {
        return sources;
    }

    public void initializeTraffic(SimulationParameters params) {
        this.params = params;
        this.currentModel = params.getTrafficModel();

        if (currentModel == TrafficModel.FGN) {
            initializeFgnTraffic();
        } else {
            initializeOnOffSources(params);
        }
    }

    public void initializeOnOffSources(SimulationParameters params) {
        sources.clear();

        final Long seed = params.getSeed();
        int sourceIDCounter = 0;

        for (SourceProfile profile : params.getSourceProfiles()) {
            Distribution onDist = new ParetoDistribution(profile.getOnXm(), profile.getOnAlpha());
            Distribution offDist = new ParetoDistribution(profile.getOffXm(), profile.getOffAlpha());

            for (int i = 0; i < profile.getNumberOfSources(); i++) {
                Long seedPerSource = (seed == null) ? null : (seed + sourceIDCounter);
                RandomNumberGenerator rng = new RandomNumberGenerator(seedPerSource);

                TrafficSource src = new TrafficSource(sourceIDCounter, onDist, offDist, rng, profile.getOnRate());
                sources.add(src);
                sourceIDCounter++;
            }
        }
    }

    public void initializeFgnTraffic() {
        sources.clear();

        int numPoints = (int) (params.getSimDuration() / params.getSamplingInt());

        int n = 1;
        while (n < numPoints) {
            n *= 2;
        }

        long seed = (params.getSeed() == null) ? System.currentTimeMillis() : params.getSeed();

        double[] fgnSequence = FractionalGaussianNoiseGenerator.generateFGN(n, params.getHurstParameter(), seed);

        this.fgnTrafficRates = transformFgnToTrafficRates(fgnSequence, 50.0);
        this.fgnTrafficRates = Arrays.copyOf(fgnTrafficRates, numPoints);  // Data generated for a power of two, which may be more than numPoints
    }

    // Sum instantaneous rates across all sources
    public double calculateAggregateTraffic(double timestamp) {
        if (currentModel == TrafficModel.FGN) {
            int index = (int) (timestamp / params.getSamplingInt());
            if (index < 0 || index >= fgnTrafficRates.length) {
                throw new IllegalArgumentException("Timestamp out of range for FGN traffic rates");
            }
            return fgnTrafficRates[index];
        } else{
            double totalRate = 0.0;
            for (TrafficSource s : sources) {
                totalRate += s.getInstantRate(timestamp);
            }
            return totalRate;
        }
    }

    public double[] transformFgnToTrafficRates(double[] fgnSequence, double averageRate) {
        double[] traffic = new double[fgnSequence.length];
        double cumulativeValue = 0.0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (int i = 0; i < fgnSequence.length; i++) {
            cumulativeValue += fgnSequence[i];
            traffic[i] = cumulativeValue;
            if (traffic[i] < min) min = traffic[i];
            if (traffic[i] > max) max = traffic[i];
        }

        double range = max - min;
        if (range == 0) range = 1; 

        // Normalization to scale rates (that could contain negative values) to [0, 2 * averageRate]
        for (int i = 0; i < traffic.length; i++) {
            double normalized = (traffic[i] - min) / range; 
            traffic[i] = normalized * (2 * averageRate);
        }
        return traffic;
    }
}
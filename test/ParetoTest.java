/**
 * @author: Clarence
 */

package test;

import model.ParetoDistribution;
import util.RandomNumberGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParetoTest {

    static class TestRNG extends RandomNumberGenerator {
        private final double[] values;
        private int idx = 0;
        
        public TestRNG(double... values) {
            super(123L);
            this.values = values;
        }

        @Override
        public double randomDouble() {
            double v = values[Math.min(idx, values.length - 1)];
            idx++;
            return v;
        }
    }

    // Ensures math of the Pareto function is implemented correctly 
    @Test
    void exactnessTest() {
        double xm = 2.0;
        double alpha = 1.5;
        ParetoDistribution pareto = new ParetoDistribution(xm, alpha);
        double[] testUniforms = {0.25, 0.5, 0.75, 0.9};
        TestRNG rng = new TestRNG(testUniforms);

        for (double uniform : testUniforms) {
            double sample = pareto.sample(rng);
            double expected = xm / Math.pow(uniform, 1.0 / alpha);
            assertEquals(expected, sample, 1e-6);
        }
    }

    // No samples should be below Xm (lower bound parameter). If there samples above Xm, the formula used could be wrong or the RNG is returning values outside (0,1).
    @Test
    void samplesNotBelowXm() {
        double xm = 2.0;
        double alpha = 1.5;
        ParetoDistribution pareto = new ParetoDistribution(xm, alpha);
        TestRNG rng = new TestRNG(1e-2, 1e-6, 0.01, 0.1, 0.5, 0.9);
        for (int i = 0; i < 6; i++) {
            double sample = pareto.sample(rng);
            assertTrue(sample >= xm);
        }
    }

    // For smaller uniform u, the sampled value should be larger.
    @Test
    void monotonicityInU() {
        double xm = 2.0;
        double alpha = 1.5;
        ParetoDistribution pareto = new ParetoDistribution(xm, alpha);
        TestRNG rng = new TestRNG(0.1, 10);
        double x1 = pareto.sample(rng);
        double x2 = pareto.sample(rng);
        assertTrue(x2 < x1);
    }

    // Ensure that the distribution is reproducible with the same seed.
    @Test
    void reproducibilityWithSeed() {
        double xm = 2.0;
        double alpha = 1.5;
        ParetoDistribution pareto1 = new ParetoDistribution(xm, alpha);
        ParetoDistribution pareto2 = new ParetoDistribution(xm, alpha);
        RandomNumberGenerator rng1 = new RandomNumberGenerator(12345L);
        RandomNumberGenerator rng2 = new RandomNumberGenerator(12345L);

        for (int i = 0; i < 20; i++) {
            assertEquals(pareto1.sample(rng1), pareto2.sample(rng2), 0.0);
        }
    }
    
    // Validates power tail law by checking that the probability that a sample exceeds k*Xm is approximately k ^ (-alpha) within 3% tolerance.
    // Checks that the sample distribution produces the correct tail heavy behavior.
    @Test
    void tailProbability() {
        double xm = 2.0;
        double alpha = 1.5;
        double k = 5.0;
        double expectedTrailProbability = Math.pow(k, -alpha);

        ParetoDistribution pareto = new ParetoDistribution(xm, alpha);
        RandomNumberGenerator rng = new RandomNumberGenerator(123L);

        int n = 10000;
        int tail = 0;
        for (int i = 0; i < n; i++) {
            if (pareto.sample(rng) > k * xm) {
                tail++;
            }
        }

        double empiricalTailProb = tail / (double) n;

        assertTrue(Math.abs(empiricalTailProb - expectedTrailProbability) < 0.03);
    }
}

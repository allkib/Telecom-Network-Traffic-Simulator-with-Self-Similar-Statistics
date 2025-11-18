package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import util.FractionalGaussianNoiseGenerator;

public class FractionalGaussianNoiseGeneratorTest {
    
    @Test
    void testIsPowerOfTwo() {
        assertTrue(FractionalGaussianNoiseGenerator.isPowerOfTwo(1));
        assertTrue(FractionalGaussianNoiseGenerator.isPowerOfTwo(2));
        assertTrue(FractionalGaussianNoiseGenerator.isPowerOfTwo(16));
        assertTrue(FractionalGaussianNoiseGenerator.isPowerOfTwo(1024));

        assertFalse(FractionalGaussianNoiseGenerator.isPowerOfTwo(0));
        assertFalse(FractionalGaussianNoiseGenerator.isPowerOfTwo(3));
        assertFalse(FractionalGaussianNoiseGenerator.isPowerOfTwo(18));
        assertFalse(FractionalGaussianNoiseGenerator.isPowerOfTwo(1000));
    }

    @Test
    void testGenerateFgnLength() {
        int n = 128;
        double hurst = 0.75;
        long seed = 123L;

        double[] result = FractionalGaussianNoiseGenerator.generateFGN(n, hurst, seed);

        assertEquals(n, result.length);
    }

    @Test
    void testGenerateInvalidFgn() {
        int n = 18;
        double hurst = 0.75;
        long seed = 123L;

        assertThrows(IllegalArgumentException.class, () -> {FractionalGaussianNoiseGenerator.generateFGN(n, hurst, seed);});
    }

    @Test
    void testSameSeedSameResult() {
        int n = 16;
        double hurst = 0.75;
        long seed = 123L;

        double[] result1 = FractionalGaussianNoiseGenerator.generateFGN(n, hurst, seed);
        double[] result2 = FractionalGaussianNoiseGenerator.generateFGN(n, hurst, seed);

        assertArrayEquals(result1, result2);
    }

    @Test
    void testStatisticalMean() {
        int n = 1024;
        double hurst = 0.75;
        long seed = 123L;

        double[] fgn = FractionalGaussianNoiseGenerator.generateFGN(n, hurst, seed);

        double sum = 0.0;
        for (double v : fgn) {
            sum += v;
        }

        double mean = sum / n;

        assertEquals(0.0, mean, 0.1);
    }

    @Test
    void testStatisticalVariance() {
        int n = 1024;
        double hurst = 0.75;
        long seed = 123L;

        double[] fgn = FractionalGaussianNoiseGenerator.generateFGN(n, hurst, seed);

        double sum = 0.0;
        for (double v : fgn) {
            sum += v;
        }
        double mean = sum / n;

        double varianceSum = 0.0;
        for (double v : fgn) {
            varianceSum += (v - mean) * (v - mean);
        }
        double variance = varianceSum / (n - 1);

        assertTrue(variance > 0.0);
    }
}
/**
 * @author: Clarence
 * 
 * Generates Fractional Gaussian Noise using the Random Midpoint Displacement method.
 */

package util;

import java.util.Random;

public class FractionalGaussianNoiseGenerator {

    public static double[] generateFGN(int n, double hurst, long seed) {
        if (!isPowerOfTwo(n)) {
            throw new IllegalArgumentException("n must be a power of 2.");
        }

        Random rand = new Random(seed);
        double[] points = new double[n + 1];
        points[0] = rand.nextDouble();
        points[n] = rand.nextDouble();

        // Recursively displace midpoints
        for (int step = n; step > 1; step /= 2) {
            for (int i = 0; i < n; i += step) {
                int mid = i + step / 2;
                double displacement = rand.nextGaussian() * Math.sqrt(0.5 * Math.pow(step, 2 * hurst));
                points[mid] = (points[i] + points[i + step]) / 2.0 + displacement;
            }
        }

        double[] fgn = new double[n];
        for (int i = 0; i < n; i++) {
            fgn[i] = points[i + 1] - points[i];
        }

        return fgn;
    }

    public static boolean isPowerOfTwo(int n) {
        if (n <= 0) {
            return false;
        }
        
        while (n % 2 ==0) {
            n = n / 2;
        }

        return n == 1;
    }

}

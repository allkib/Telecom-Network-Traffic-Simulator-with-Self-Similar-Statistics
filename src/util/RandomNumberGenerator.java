/**
 * @author: Clarence
 * 
 * Sample random numbers from various distributions. 
 * Inverse transform sampling is used. 
 */

package util;

import java.util.Random;
public class RandomNumberGenerator {
    private final Random rnd;

    public RandomNumberGenerator(Long seed) {
        if (seed == null) {
            rnd = new Random();
        } else {
            rnd = new Random(seed);
        }
    }

    public double randomDouble() {
        double u;
        do {
            u = rnd.nextDouble();
        } while (u <= 0.0 || u >= 1.0);
        return u;
    }
 }
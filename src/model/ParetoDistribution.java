/**
 * @author: Clarence
 * 
 * Pareto Distribution implementation of the Distribution interface.
 */

package model;

import util.RandomNumberGenerator;

public class ParetoDistribution implements Distribution {
    private final double xm;
    private final double alpha;

    public ParetoDistribution(double xm, double alpha) {
        if (xm <= 0.0 || alpha <= 0.0) {
            throw new IllegalArgumentException("Invalid Pareto params: xm > 0, alpha>0 required");
        }
        this.xm = xm;
        this.alpha = alpha;
    }

    @Override
    public double sample(RandomNumberGenerator rng) {
        double u = rng.randomDouble(); 
        return xm / Math.pow(u, 1.0 / alpha);
    }
}
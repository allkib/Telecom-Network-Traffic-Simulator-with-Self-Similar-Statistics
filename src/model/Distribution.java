/**
 * @author: Clarence
 * 
 * Distribution interface to be implemented by statistical distribution classes, including Pareto.
 */

package model;

import util.RandomNumberGenerator;

public interface Distribution {
    double sample(RandomNumberGenerator rng);
}
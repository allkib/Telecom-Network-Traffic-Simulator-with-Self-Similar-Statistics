// All
package util;
import model.SimulationParameters;

// Validator for simulation parameters
public class SimulationValidator {

    public boolean validateDuration(double duration) {
        return duration > 0.0;
    }

    public boolean validateNumSources(int numSources) {
        return numSources > 0;
    }

    public boolean validateParetoAlpha(double alpha) {
        // Accept any positive alpha; users may choose >1 for finite mean.
        return alpha > 0.0;
    }

    public boolean validateParetoMinValue(double minValue) {
        return minValue > 0.0;
    }

    public boolean validateSamplingInt(double samplingInt) {
        return samplingInt > 0.0;
    }

    public boolean validateAllParameters(SimulationParameters params) {
        if (params == null) return false;
        boolean ok = true;
        ok &= validateDuration(params.getSimDuration());
        ok &= validateNumSources(params.getNumSources());
        ok &= validateParetoAlpha(params.getParetoAlpha());
        ok &= validateParetoMinValue(params.getParetoMinVal());
        ok &= validateSamplingInt(params.getSamplingInt());
        return ok;
    }
}
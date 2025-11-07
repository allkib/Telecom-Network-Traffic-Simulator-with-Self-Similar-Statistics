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

    public boolean validateHurst(double hurst) {
        return hurst > 0.5 && hurst < 1.0;
    }

    public boolean validateAllParameters(SimulationParameters params) {
        if (params == null) return false;
        boolean ok = true;
        ok &= validateDuration(params.getSimDuration());
        ok &= validateSamplingInt(params.getSamplingInt());

        if (params.getTrafficModel() == model.TrafficModel.ON_OFF) {
            ok &= validateNumSources(params.getNumSources());
            for (var profile : params.getSourceProfiles()) {
                ok &= validateParetoAlpha(profile.getOnAlpha());
                ok &= validateParetoAlpha(profile.getOffAlpha());
                ok &= validateParetoMinValue(profile.getOnXm());
                ok &= validateParetoMinValue(profile.getOffXm());
            }
        } else if (params.getTrafficModel() == model.TrafficModel.FGN) {
            ok &= validateHurst(params.getHurstParameter());
        }
        
        return ok;
    }
}
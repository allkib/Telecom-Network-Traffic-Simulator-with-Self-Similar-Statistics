// All
package controller;

import java.util.ArrayList;
import java.util.List;

import model.SimulationParameters;
import util.FileHandler;
import util.SimulationValidator;

// Controller for simulation parameters
public class ParameterController {
    private final SimulationValidator validator = new SimulationValidator();
    private final FileHandler fileHandler = new FileHandler();
    private final List<String> validationErrors = new ArrayList<>();

    public void setDefaults(SimulationParameters params) {
        if (params == null) return;
        SimulationParameters defaults = SimulationParameters.defaults();
        params.setSimDuration(defaults.getSimDuration());
        params.setNumSources(defaults.getNumSources());
        params.setParetoAlpha(defaults.getParetoAlpha());
        params.setParetoMinVal(defaults.getParetoMinVal());
        params.setSamplingInt(defaults.getSamplingInt());
        params.setSeed(defaults.getSeed());
    }

    public boolean validateParameters(SimulationParameters params) {
        validationErrors.clear();
        if (params == null) {
            validationErrors.add("Parameters object is null");
            return false;
        }

        boolean ok = true;
        if (!validator.validateDuration(params.getSimDuration())) {
            ok = false; validationErrors.add("simulationDuration must be > 0");
        }
        if (!validator.validateNumSources(params.getNumSources())) {
            ok = false; validationErrors.add("numSources must be > 0");
        }
        if (!validator.validateParetoAlpha(params.getParetoAlpha())) {
            ok = false; validationErrors.add("paretoAlpha must be > 0");
        }
        if (!validator.validateParetoMinValue(params.getParetoMinVal())) {
            ok = false; validationErrors.add("paretoMinVal must be > 0");
        }
        if (!validator.validateSamplingInt(params.getSamplingInt())) {
            ok = false; validationErrors.add("samplingInt must be > 0");
        }
        return ok;
    }

    public List<String> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }

    public SimulationParameters loadParametersFromFile(String filename) {
        // Stub: delegate to FileHandler; actual parsing to be implemented later
        String raw = fileHandler.readConfiguration(filename);
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        // TODO: parse raw into SimulationParameters; return null for now
        return null;
    }

    public boolean saveParametersToFile(SimulationParameters params, String filename) {
        // Stub: delegate to FileHandler; actual serialization to be implemented later
        if (params == null) return false;
        String serialized = ""; // TODO: serialize params to a simple key=value format
        return fileHandler.writeConfiguration(filename, serialized);
    }
}
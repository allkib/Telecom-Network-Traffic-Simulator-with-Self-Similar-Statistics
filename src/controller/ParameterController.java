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
        validationErrors.clear();
        String raw = fileHandler.readConfig(filename);
        if (raw == null || raw.isEmpty()) {
            validationErrors.add("Configuration file is empty or unreadable");
            return null;
        }
        SimulationParameters params = new SimulationParameters();
        // Start from defaults to allow partial files
        setDefaults(params);

        String[] lines = raw.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            int eq = trimmed.indexOf('=');
            if (eq <= 0 || eq == trimmed.length() - 1) {
                validationErrors.add("Invalid line: " + trimmed);
                continue;
            }
            String key = trimmed.substring(0, eq).trim();
            String val = trimmed.substring(eq + 1).trim();
            try {
                switch (key) {
                    case "simDuration":
                        params.setSimDuration(Double.parseDouble(val));
                        break;
                    case "numSources":
                        params.setNumSources(Integer.parseInt(val));
                        break;
                    case "paretoAlpha":
                        params.setParetoAlpha(Double.parseDouble(val));
                        break;
                    case "paretoMinVal":
                        params.setParetoMinVal(Double.parseDouble(val));
                        break;
                    case "samplingInt":
                        params.setSamplingInt(Double.parseDouble(val));
                        break;
                    case "seed":
                        if (val.isEmpty() || val.equalsIgnoreCase("null")) {
                            params.setSeed(null);
                        } else {
                            params.setSeed(Long.parseLong(val));
                        }
                        break;
                    default:
                        validationErrors.add("Unknown key: " + key);
                }
            } catch (NumberFormatException e) {
                validationErrors.add("Invalid number for key '" + key + "': " + val);
            }
        }

        // Validate parsed params
        if (!validateParameters(params)) {
            // retain validationErrors populated by validateParameters
            return null;
        }
        return params;
    }

    public boolean saveParametersToFile(SimulationParameters params, String filename) {
        if (params == null) {
            validationErrors.clear();
            validationErrors.add("Parameters object is null");
            return false;
        }
        // Ensure parameters are valid before saving
        if (!validateParameters(params)) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("simDuration=").append(params.getSimDuration()).append('\n');
        sb.append("numSources=").append(params.getNumSources()).append('\n');
        sb.append("paretoAlpha=").append(params.getParetoAlpha()).append('\n');
        sb.append("paretoMinVal=").append(params.getParetoMinVal()).append('\n');
        sb.append("samplingInt=").append(params.getSamplingInt()).append('\n');
        sb.append("seed=").append(params.getSeed() == null ? "" : params.getSeed()).append('\n');
        return fileHandler.writeConfig(filename, sb.toString());
    }
}
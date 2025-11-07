// All
package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import model.SimulationParameters;
import model.SourceProfile;
import model.TrafficModel;
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
        params.setSamplingInt(defaults.getSamplingInt());
        params.setSeed(defaults.getSeed());
        params.setTrafficModel(defaults.getTrafficModel());
        params.setHurstParameter(defaults.getHurstParameter());
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
        if (!validator.validateSamplingInt(params.getSamplingInt())) {
            ok = false; validationErrors.add("samplingInt must be > 0");
        }

        if (params.getTrafficModel() == model.TrafficModel.ON_OFF) {
            if (!validator.validateNumSources(params.getNumSources())) {
                ok = false; validationErrors.add("Total number of sources must be > 0");
            }

            for (SourceProfile profile : params.getSourceProfiles()) {
                if (!validator.validateParetoAlpha(profile.getOnAlpha()) || !validator.validateParetoAlpha(profile.getOffAlpha())) {
                    ok = false; validationErrors.add("Pareto alphas must be > 0");
                }
                if (!validator.validateParetoMinValue(profile.getOnXm()) || !validator.validateParetoMinValue(profile.getOffXm())) {
                    ok = false; validationErrors.add("Pareto xm must be > 0");
                }
            }
        } else if (params.getTrafficModel() == model.TrafficModel.FGN) {
            if (!validator.validateHurst(params.getHurstParameter())) {
                ok = false; validationErrors.add("Hurst parameter must be in (0.5, 1.0)");
            }
        }
        return ok;
    }

    public List<String> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }

    /**
     * @param paramsFile Main file with parameters that apply to both FGN and ON_OFF models
     * @param profilesFile Secondary file with source profiles (only for ON_OFF model)
     */
    public SimulationParameters loadParametersFromFile(String paramsFile, String profilesFile) {
        validationErrors.clear();

        // Load main parameters for all models
        String paramsCsv = fileHandler.readConfig(paramsFile);
        if (paramsCsv == null || paramsCsv.isEmpty()) {
            validationErrors.add("Parameters CSV is empty or could not be read");
            return null;
        }

        Map<String, String> configMap = new HashMap<>();
        String[] lines = paramsCsv.split("\n");
        for (int i = 1; i < lines.length; i++) {
            String[] parts = lines[i].trim().split(",");
            if (parts.length == 2) {
                configMap.put(parts[0].trim(), parts[1].trim());
            }
        }

        SimulationParameters params = new SimulationParameters();
        params.clearSourceProfiles();

        TrafficModel model = TrafficModel.valueOf(configMap.getOrDefault("trafficModel", "ON_OFF").toUpperCase());
        params.setTrafficModel(model);

        params.setSimDuration(Double.parseDouble(configMap.getOrDefault("simDuration", "1000.0")));
        params.setSamplingInt(Double.parseDouble(configMap.getOrDefault("samplingInt", "1.0")));
        if (configMap.containsKey("seed")) {
            params.setSeed(Long.parseLong(configMap.get("seed")));
        }

        // Load model-specific parameters
        if (model == TrafficModel.ON_OFF) {
            loadOnOffProfiles(params, profilesFile);
        } else if (model == TrafficModel.FGN) {
            params.setHurstParameter(Double.parseDouble(configMap.getOrDefault("hurstParameter", "0.75")));
        }

        // Validate parsed params
        if (!validateParameters(params)) {
            // retain validationErrors populated by validateParameters
            return null;
        }
        return params;
    }

    public void loadOnOffProfiles (SimulationParameters params, String profilesFile) {
        if (profilesFile == null) {
            validationErrors.add("Source profiles file is required for ON_OFF model");
        }
        String profilesCsv = fileHandler.readConfig(profilesFile);
        if (profilesCsv.isEmpty()) {
            validationErrors.add("Source profiles CSV is empty");
        }
        
        String[] profileLines = profilesCsv.split("\n");
        for (int i = 1; i < profileLines.length; i++) {
            String[] parts = profileLines[i].trim().split(",");
            if (parts.length == 7) {
                SourceProfile profile = new SourceProfile(
                    parts[0].trim(),
                    Integer.parseInt(parts[1].trim()),
                    Double.parseDouble(parts[2].trim()),
                    Double.parseDouble(parts[3].trim()),
                    Double.parseDouble(parts[4].trim()),
                    Double.parseDouble(parts[5].trim()),
                    Double.parseDouble(parts[6].trim())
                );
                params.addSourceProfile(profile);
            }
        }
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
        sb.append("samplingInt=").append(params.getSamplingInt()).append('\n');
        sb.append("trafficModel=").append(params.getTrafficModel().name()).append('\n');
        sb.append("seed=").append(params.getSeed() == null ? "" : params.getSeed()).append('\n');

        if (params.getTrafficModel() == TrafficModel.ON_OFF) {
            sb.append("numProfiles=").append(params.getSourceProfiles().size()).append('\n');
            int profileIndex = 1;
            for (SourceProfile profile : params.getSourceProfiles()) {
                String prefix = "profile" + profileIndex + ".";
                sb.append(prefix).append("name=").append(profile.getName()).append('\n');
                sb.append(prefix).append("numSources=").append(profile.getNumberOfSources()).append('\n');
                sb.append(prefix).append("onRate=").append(profile.getOnRate()).append('\n');
                sb.append(prefix).append("onAlpha=").append(profile.getOnAlpha()).append('\n');
                sb.append(prefix).append("onXm=").append(profile.getOnXm()).append('\n');
                sb.append(prefix).append("offAlpha=").append(profile.getOffAlpha()).append('\n');
                sb.append(prefix).append("offXm=").append(profile.getOffXm()).append('\n');
                profileIndex++;
            }
        } else if (params.getTrafficModel() == TrafficModel.FGN) {
            sb.append("hurstParameter=").append(params.getHurstParameter()).append('\n');
        }

        return fileHandler.writeConfig(filename, sb.toString());
    }
}
/**
 * @author: Clarence
 */

 package view;

 import controller.EventController;
 import controller.ParameterController;
 import controller.SimulationController;
 import controller.TrafficController;
 import model.SimulationParameters;
 import model.TrafficStatistics;

 public class ConsoleView {
    private final InputHandler input = new InputHandler();
    private final ParameterController paramController = new ParameterController();
    private final SimulationController simController;

    public ConsoleView() {
        simController = new SimulationController();
        simController.setTrafficController(new TrafficController());
        simController.setEventController(new EventController());
    }

    public void startSimulation() {
        try {
            SimulationParameters params = promptForParameters();

            if (!paramController.validateParameters(params)) {
                System.out.println("Parameter validation failed with the following errors:");
                for (String err : paramController.getValidationErrors()) {
                    System.out.println(err);
                }
                System.out.println("Exiting simulation due to invalid parameters.");
                return;
            }

            simController.runSimulation(params);

            TrafficStatistics resultStats = simController.getResults();
            System.out.println("Simulation completed successfully.");
            OutputFormatter.printSummary(resultStats);
        } catch (QuitHandler qh) {
            System.out.println("Exiting simulation.");
        }
    }

    public SimulationParameters promptForParameters() {
        SimulationParameters params = SimulationParameters.defaults();

            System.out.println("Welcome to the Telecom Traffic Simulation.");
            System.out.println("Please enter simulation parameters. Or press Enter to use defaults.");
            System.out.println("At any time, enter 'q', 'quit', or 'exit' to exit the simulation.");
            System.out.println("------------------------------------");

            params.setSimDuration(input.readDouble("Total Simulation Duration", params.getSimDuration()));
            params.setSamplingInt(input.readDouble("Sampling Interval", params.getSamplingInt()));
            Long seed = input.readLongOrNull("Seed", params.getSeed());
            params.setSeed(seed);

            params.setTrafficModel(input.readTrafficModel("Traffic Model (ON_OFF/FGN)", params.getTrafficModel()));

            if (params.getTrafficModel() == model.TrafficModel.FGN) {
                params.setHurstParameter(input.readDouble("Hurst Parameter", params.getHurstParameter()));
            } else {
                params.setNumSources(input.readInt("Number of Traffic Sources", params.getNumSources()));
                params.setParetoAlpha(input.readDouble("Pareto Alpha", params.getParetoAlpha()));
                params.setParetoMinVal(input.readDouble("Pareto Minimum Value", params.getParetoMinVal()));
            }

            return params;
    }
 }
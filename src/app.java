import controller.ParameterController;
import controller.SimulationController;
import controller.EventController;
import controller.TrafficController;
import model.SimulationParameters;

public class app {
    public static void main(String[] args) {
        ParameterController paramController = new ParameterController();

        SimulationParameters params = SimulationParameters.defaults();
        paramController.setDefaults(params);

        if (!paramController.validateParameters(params)) {
            for (String err : paramController.getValidationErrors()) {
                System.out.println("Parameter error: " + err);
            }
            System.out.println("Exiting due to invalid parameters.");
            return;
        }

        SimulationController simController = new SimulationController();
        simController.setTrafficController(new TrafficController());
        simController.setEventController(new EventController());
        simController.runSimulation(params);

        System.out.println("Simulation completed. Samples collected: " + simController.getResults().getTimeSeries().size());
    }
}
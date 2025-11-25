## Telecom Network Traffic Simulator – Project Report

**Student:** All Kibria & Clarence Soh
**Group:** 13
**Project:** Telecom Network Traffic Simulator with Self-Similar Statistics

---

### 1. Self-Assessment Checklist

| Feature | Status | 
| --- | --- |
| Input Parameters | ✅ |
| Traffic Model Implementation | ✅ |
| Event-Driven Simulation | ✅ | 
| Simulation Execution | ✅ | 
| Output Data | ✅ | 
| Console Interaction | ✅ | 
| Quit Command | ✅ |
| Error Handling | ✅ | 
| Advanced: Fractional Gaussian Noise model | ✅ | `
| Advanced: Hurst parameter estimation | ✅ | 
| Advanced: Implementing basic network elements | ✅ | 
| Advanced: Generating output for plotting | ✅ | 
| Advanced: Load/save parameters from file | ✅ | 
| Advanced: Multiple ON/OFF source profiles | ✅ | 

_All required and agreed advanced/bonus features are implemented and working._

---

### 2. System Overview & Testing Summary

- **Architecture:** MVC separation (`model`, `controller`, `view`, `util`). `SimulationController` orchestrates the loop, sampling, and queue integration. `TrafficController` handles sources (ON/OFF or FGN). `EventController` uses `EventQueue` to process ON/OFF transitions. `NetworkQueue` measures downstream impact.
- **Console Workflow:** `ConsoleView` gathers parameters (manual entry, defaults, or load from file), validates via `ParameterController`, executes the simulation, prints statistics, and exports CSVs.
- **Testing Approach:**
  - **Unit Tests:** Coverage for key classes (e.g., `TrafficSourceTest`, `EventQueueTest`, `TrafficControllerTest`, `FileHandlerTest`, `ParameterControllerTest`, `SimulationControllerTest`).
  - **System Tests:** `SystemTest` runs end-to-end scenarios (successful runs, varied parameters, error paths).
  - Tests run with JUnit 5 (`junit-platform-console-standalone.jar`).

---

### 4. Build & Run Instructions

```bash
cd .../Telecom-Network-Traffic-Simulator-with-Self-Similar-Statistics

# Compile (requires junit-platform-console-standalone.jar in lib/)
find src test -name "*.java" > sources.txt
javac -cp "lib/junit-platform-console-standalone.jar:src:test" -d out @sources.txt

# Run application
java -cp out app

# Run all JUnit tests
java -jar lib/junit-platform-console-standalone.jar -cp "out:src:test" --scan-class-path
```

Simulation outputs (`Aggregate_Traffic.csv`, `Event_Log.csv`, `Queue_Stats.csv`) are written to the project root after each run.
# Telecom-Network-Traffic-Simulator-with-Self-Similar-Statistics
**Project Title:** Telecom Network Traffic Simulator with Self-Similar Statistics

**Assignment Goal:** The goal of this assignment is for students to design, implement, and test a
console-based Java program that simulates telecom network traffic exhibiting self-similar
characteristics. This project is designed to assess your ability to:

1. Apply **Object-Oriented Programming** principles to model components of a simulator
    (e.g., traffic sources, events, event queue).
2. Design a software system that generates events according to a defined statistical
    process, considering the implementation of a **complex functional requirement** (self-
    similar traffic generation).
3. Implement the simulation logic, handling input/output via the console, managing the
    simulation state over time, and generating output data.
4. Perform unit and system **testing** to verify the correctness of simulator components and
    the overall simulation process.
5. Develop code that adheres to principles of **code quality** , including readability,
    meaningful naming, appropriate class and method design, and use of suitable data
    structures.
6. _(For a potential group version, as per the module structure)_ Implement the system as
    part of a team, managing division of labour and using **source control** effectively.

**Required Features (Functional Requirements):** The program must be a console-based Java
application. It should implement a simulator for generating network traffic. Specifically:

- **Input Parameters:** Accept simulation parameters from the console, such as total
    simulation time, number of traffic sources, and parameters defining the self-similar
    nature of the traffic (e.g., parameters for generating heavy-tailed ON/OFF periods if
    using an aggregated ON/OFF source model – see below).
- **Traffic Model Implementation:** Implement a method for generating self-similar
    traffic. A common approach is to aggregate a large number of simple ON/OFF sources,
    where the ON and OFF times follow a heavy-tailed distribution (e.g., Pareto). The
    program must implement this aggregation model:
       o Model individual ON/OFF sources with states (ON/OFF) and timers for state
          duration.
       o Generate ON and OFF durations for each source based on a specified heavy-
          tailed distribution (e.g., Pareto distribution). This may require implementing the
          logic to sample from such a distribution.
       o Sum the output of all ON sources over time to produce the aggregate traffic rate
          or events (e.g., packet arrivals).
- **Event-Driven Simulation:** The simulator should ideally be event-driven, managing
    events like sources turning ON or OFF. This will require an event queue (a suitable
    **data structure** from the Java Collections Framework).
- **Simulation Execution:** Run the simulation for the specified duration, processing
    events from the event queue and updating the state of the traffic sources.
- **Output Data:** Generate output to the console or a file. This could include:
    o A time-series of the aggregate traffic rate (e.g., samples at regular intervals).
    o A log of events (e.g., timestamp, source ID, event type ON/OFF).
    o Basic summary statistics of the generated traffic (e.g., average rate, peak rate).
- **Console Interaction:** Use the console for prompting for input parameters and
    displaying basic status messages during the simulation.


- **Quit Command:** Implement a command (e.g., 'quit' or 'q') to exit the program
    gracefully.
- **Error Handling:** Display appropriate error messages for invalid input parameters or
    runtime issues and prompt the user for valid input.

**Potential Advanced/Bonus Features (for higher marks or differentiation):**

- Implementing alternative self-similar traffic generation methods (e.g., using Fractional
    Gaussian Noise or similar mathematical models).
- Implementing functionality to estimate the Hurst parameter (a measure of self-
    similarity) from the generated output data and display it.
- Allowing different types of ON/OFF sources with varying parameters.
- Implementing basic network elements (e.g., a queue) to observe the impact of the
    generated traffic.
- Generating output in a format suitable for plotting or external analysis (e.g., CSV).
- Implementing a command to load simulation parameters from a file.

**Assessment Rubric:** The project will be assessed based on criteria similar to previous
COMP41670 projects, adapting the focus for a simulator domain:

1. **Functionality (e.g., 60-70% of total grade):**
    o **Correctness and Completeness:** Percentage of required features working
       correctly. Minimum implementation of all core required features (input, traffic
       model implementation, simulation execution, output, quit, basic error handling)
       is necessary for a passing grade.
    o **Traffic Model Accuracy:** Assessment of whether the implemented model
       correctly generates traffic according to the specified process (e.g., aggregation
       of ON/OFF sources with heavy-tailed durations).
    o **Simulation Accuracy:** Assessment of whether the simulator correctly manages
       time and events.
    o **Feature Checklist:** Marks awarded based on a feature checklist, weighted by
       complexity.
    o **Advanced Features:** Implementation of advanced/bonus features will
       contribute to higher marks.
2. **Code Quality (e.g., 15-20% of total grade):**
    o **Readability:** Code is easy to read and understand.
    o **Structure:** Project is well-structured into logical classes, applying **OOP**
       **principles** effectively to model simulator components. Consider design patterns
       where appropriate.
    o **Method Design:** Methods are short and perform a single, clearly defined
       function.
    o **Data Structures:** Appropriate Java Collections (like ArrayList,
       PriorityQueue for the event queue, etc.) are used.
    o **Naming Conventions:** Consistent and meaningful names are used for classes,
       variables, and methods. Avoid noise words and make meaningful distinctions.
    o **Scope:** Variable scope is minimised.
    o **Comments & Javadoc:** Useful comments explain complex logic or design
       choices. Javadoc is used correctly for at least one class.
3. **Testing (e.g., 10-15% of total grade):**


```
o Unit Tests (JUnit): JUnit tests are provided for key classes (e.g., the ON/OFF
source class, event queue management logic). Aim for good test coverage.
o System Tests: Test scripts or a demonstration covering the overall simulation
flow, including parameter input, simulation execution, output generation, and
error handling.
o Explanation: The testing approach is clearly explained in the submission video.
```
4. **Version Control (Deduction):**
    o Proper use of Git/GitHub for source code control throughout the project. The
       repository must be private. Deductions will apply for incorrect usage.

**Submission Requirements:** Students should submit a zip file containing the following items
via Brightspace:

- **Report:** A report including student name(s), a self-assessment checklist detailing the
    working status of each required feature, and explanations for any partially working
    features.
- **Video:** A video (max. 5-10 minutes, similar to the UDL-suggested length for video
    reports) demonstrating the working features, explaining the code structure (potentially
    showing UML class/sequence diagrams as discussed in lectures), and explaining the
    testing approach. This addresses potential platform dependency issues.
- **Source Code:** A directory containing all the Java source code files.
- **Executable JAR:** A directory containing an executable JAR file of the program.

**Guidelines and Constraints:**

- The project must be implemented in Java.
- The interface must be entirely console-based (no GUI).
- Source code control must be managed using Git/GitHub. The repository must be
    private. Team members (if group project) and designated staff must be given access.
- Use of external libraries is restricted to standard Java libraries (including java.util
    for Collections, java.util.Random for random number generation, potentially
    java.io or java.nio.file for file output) and JUnit for testing. Use of open-source
    code without proper attribution and adherence to licensing will be treated as
    **plagiarism**.
/**
 * @author: Clarence
 */

package view;

import java.util.Scanner;
import model.TrafficModel;

public class InputHandler {
    private final Scanner scanner = new Scanner(System.in);

    public int readInt(String prompt, int defaultVal){
        while (true) {
            String s = readLine(prompt, String.valueOf(defaultVal));
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e){
                System.out.println("Invalid integer input. Try again (or press ENTER for default: " + defaultVal + ")");
            }
        }
    }

    public double readDouble(String prompt, double defaultVal){
        while (true) {
            String s = readLine(prompt, String.valueOf(defaultVal));
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e){
                System.out.println("Invalid double input. Try again (or press ENTER for default: " + defaultVal + ")");
            }
        }
    }

    public Long readLongOrNull(String prompt, Long defaultVal){
        String displayPrompt = prompt + " [" + (defaultVal == null ? "random" : defaultVal) + "]";

        while (true) {
            System.out.println(displayPrompt + ": ");
            String line = scanner.nextLine().trim();
            if (isQuit(line)) {
                throw new QuitHandler("User requested to quit");
            }
            if (line == null || line.isEmpty()) {
                return defaultVal;
            }
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException e){
                System.out.println("Invalid long. Try again (or press ENTER for random seed).");
            }
        }
    }

    public TrafficModel readTrafficModel(String prompt, TrafficModel defaultVal){
        String defaultstr = defaultVal.toString();
        while (true) {
            String s = readLine(prompt, defaultstr);
            try {
                return TrafficModel.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e){
                System.out.println("Invalid traffic model. Try again (or press ENTER for default: " + defaultstr + ")");
            }
        }
    }

    public String readYesNo(String prompt, String defaultVal) {
        while (true) {
            System.out.print(prompt + "[" + defaultVal + "]: ");
            String line = scanner.nextLine().trim();

            if (isQuit(line)) {
                throw new QuitHandler("User requested to quit");
            }
            if (line.isEmpty()) {
                return defaultVal;
            }
            if (line.equalsIgnoreCase("y") || line.equalsIgnoreCase("yes")) {
                return "y";
            } else if (line.equalsIgnoreCase("n") || line.equalsIgnoreCase("no")) {
                return "n";
            }
            
            System.out.println("Invalid input. Please enter 'y' or 'n'.");
        }
    }

    public String readLine(String prompt, String defaultVal){
        System.out.print(prompt + " [" + defaultVal + "]: ");
        String line = scanner.nextLine().trim();
        if (isQuit(line)) {
            throw new QuitHandler("User requested to quit");
        }
        return line.isEmpty() ? defaultVal : line;
    }

    public boolean isQuit(String s) {
        return s != null && (s.equalsIgnoreCase("q") || s.equalsIgnoreCase("quit") || s.equalsIgnoreCase("exit"));
    }
 }
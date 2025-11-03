/**
 * @author: Clarence
 */

 package view;

 import java.util.Scanner;

 public class InputHandler {
    private final Scanner scanner = new Scanner(System.in);

    public int readInt(String prompt, int defaultVal){
        String s = readLine(prompt, String.valueOf(defaultVal));
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e){
            System.out.println("Invalid integer input. Using default: " + defaultVal);
            return defaultVal;
        }
    }

    public double readDouble(String prompt, double defaultVal){
        String s = readLine(prompt, String.valueOf(defaultVal));
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e){
            System.out.println("Invalid double input. Using default: " + defaultVal);
            return defaultVal;
        }
    }

    public Long readLongOrNull(String prompt, Long defaultVal){
        String shownDefaultSeed = defaultVal == null ? "random" : String.valueOf(defaultVal);
        String s = readLine(prompt, shownDefaultSeed);
        if (s == null || s.isEmpty()) return defaultVal;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e){
            System.out.println("Using random seed.");
            return defaultVal;
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
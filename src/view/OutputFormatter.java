/**
 * @author: Clarence
 */

 package view;

 import model.TrafficStatistics;

 public class OutputFormatter {
    public static void printSummary(TrafficStatistics stats) {
        System.out.println();
        System.out.println("Traffic Statistics Summary:");
        System.out.println("---------------------------");
        System.out.println("Number of Samples: " + stats.getTimeSeries().size());
        System.out.println("Average Rate: " + String.format("%.4f", stats.getAvgRate()));
        System.out.println("Peak Rate: " + String.format("%.4f", stats.getPeakRate()));
        System.out.println("Total Traffic: " + String.format("%.4f", stats.getTotTraffic()));
    }
 }
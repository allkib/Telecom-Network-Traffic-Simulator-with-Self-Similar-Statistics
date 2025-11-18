/**
 * @author: Clarence & All
 */

package view;

import model.TrafficStatistics;
import model.QueueStatistics;

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

    public static void printHurstParameter(double h, String method, boolean isSelfSimilar, String confidence) {
        System.out.println();
        System.out.println("Hurst Parameter Analysis:");
        System.out.println("-------------------------");
        System.out.println("Method: " + method);
        System.out.println("Hurst Parameter (H): " + String.format("%.4f", h));
        System.out.println("Confidence: " + confidence);
        System.out.println();
        
        if (isSelfSimilar) {
            System.out.println("  Traffic exhibits SELF-SIMILARITY (H > 0.5)");
            System.out.println("  This indicates long-range dependence in the traffic pattern.");
            System.out.println("  The traffic shows persistent behavior over multiple time scales.");
        } else if (h < 0.5) {
            System.out.println("  Traffic is ANTI-PERSISTENT (H < 0.5)");
            System.out.println("  This indicates mean-reverting behavior.");
        } else {
            System.out.println("  Traffic is RANDOM (H ≈ 0.5)");
            System.out.println("  No significant self-similarity detected.");
        }
        System.out.println();
    }

    public static void printQueueStatistics(QueueStatistics stats) {
        if (stats == null) {
            System.out.println("Queue statistics unavailable.");
            return;
        }

        System.out.println();
        System.out.println("Queue Statistics:");
        System.out.println("-----------------");
        System.out.println("Average Queue Length: " + String.format("%.4f", stats.getAvgQueueLen()));
        System.out.println("Peak Queue Length: " + String.format("%.4f", stats.getMaxQueueLen()));
        System.out.println("Overflow Count: " + stats.getOverflowCount());
        System.out.println("Overflow Percentage: " + String.format("%.2f%%", stats.getOverflowPercent()));
        System.out.println("Total Arrivals: " + String.format("%.2f", stats.getTotalArrivals()));
        System.out.println();
    }
}
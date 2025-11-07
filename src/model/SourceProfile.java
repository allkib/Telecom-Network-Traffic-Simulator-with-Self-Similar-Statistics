/**
 * @author: Clarence
 * 
 * Stores profile information for each ON/OFF traffic source.
 */

package model;

public class SourceProfile {
    private String name;
    private int numberOfSources;
    private double onRate;
    private double onAlpha;
    private double onXm;
    private double offAlpha;
    private double offXm;

    public SourceProfile(String name, int numberOfSources, double onRate, double onAlpha, double onXm, double offAlpha, double offXm) {
        this.name = name;
        this.numberOfSources = numberOfSources;
        this.onRate = onRate;
        this.onAlpha = onAlpha;
        this.onXm = onXm;
        this.offAlpha = offAlpha;
        this.offXm = offXm;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfSources() {
        return numberOfSources;
    }

    public double getOnRate() {
        return onRate;
    }

    public double getOnAlpha() {
        return onAlpha;
    }

    public double getOnXm() {
        return onXm;
    }

    public double getOffAlpha() {
        return offAlpha;
    }

    public double getOffXm() {
        return offXm;
    }
}

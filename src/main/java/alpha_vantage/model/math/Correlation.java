package alpha_vantage.model.math;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Correlation {

    // Two symbols being compared
    String symbolOne;
    String symbolTwo;

    // Basing correlation on closing prices of each crypto in USD
    // In double arrays for math comparison
    double[] closeOne;
    double[] closeTwo;

    // How many days offset are the two time series?
    // Default is 0, but can shift to see if there is a lagging correlation
    int daysOffset = 0;

    // Correlations
    double pearson;
    double spearman;
    double kendall;
    double averageCorrelation;

    public String getSymbolOne() {
        return symbolOne;
    }

    public void setSymbolOne(String symbolOne) {
        this.symbolOne = symbolOne;
    }

    public String getSymbolTwo() {
        return symbolTwo;
    }

    public void setSymbolTwo(String symbolTwo) {
        this.symbolTwo = symbolTwo;
    }

    public double[] getCloseOne() {
        return closeOne;
    }

    public void setCloseOne(double[] closeOne) {
        this.closeOne = closeOne;
    }

    public double[] getCloseTwo() {
        return closeTwo;
    }

    public void setCloseTwo(double[] closeTwo) {
        this.closeTwo = closeTwo;
    }

    public int getDaysOffset() {
        return daysOffset;
    }

    public void setDaysOffset(int daysOffset) {
        this.daysOffset = daysOffset;
    }

    public double getPearson() {
        return pearson;
    }

    public void setPearson(double pearson) {
        this.pearson = pearson;
    }

    public double getSpearman() {
        return spearman;
    }

    public void setSpearman(double spearman) {
        this.spearman = spearman;
    }

    public double getKendall() {
        return kendall;
    }

    public void setKendall(double kendall) {
        this.kendall = kendall;
    }

    public double getAverageCorrelation() {
        return averageCorrelation;
    }

    public void setAverageCorrelation(double averageCorrelation) {
        this.averageCorrelation = averageCorrelation;
    }
}

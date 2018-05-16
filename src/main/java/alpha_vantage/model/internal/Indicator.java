package alpha_vantage.model.internal;

public class Indicator {

    private String date;
    private String symbol;
    private double close;
    private double delta;
    private double simpleMovingAverage;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public double getSimpleMovingAverage() {
        return simpleMovingAverage;
    }

    public void setSimpleMovingAverage(double simpleMovingAverage) {
        this.simpleMovingAverage = simpleMovingAverage;
    }
}

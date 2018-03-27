package alpha_vantage.model.internal;

import alpha_vantage.enums.DigitalCurrency;

public class FindMax {

    private DigitalCurrency symbol;
    private String date;
    private double highUSD;

    public DigitalCurrency getSymbol() {
        return symbol;
    }

    public void setSymbol(DigitalCurrency symbol) {
        this.symbol = symbol;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getHighUSD() {
        return highUSD;
    }

    public void setHighUSD(double highUSD) {
        this.highUSD = highUSD;
    }
}

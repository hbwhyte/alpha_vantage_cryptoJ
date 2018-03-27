package alpha_vantage.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DigitalCurrencyData {

//    @JsonProperty("1a. open (CAD)")
//    double openLocal;
    @JsonProperty("1b. open (USD)")
    double openUSD;
//    @JsonProperty("2a. high (CAD)")
//    double highLocal;
    @JsonProperty("2b. high (USD)")
    double highUSD;
//    @JsonProperty("3a. low (CAD)")
//    double lowLocal;
    @JsonProperty("3b. low (USD)")
    double lowUSD;
//    @JsonProperty("4a. close (CAD)")
//    double closeLocal;
    @JsonProperty("4b. close (USD)")
    double closeUSD;
    @JsonProperty("5. volume")
    double volume;
    @JsonProperty("6. market cap (USD)")
    double marketCap;

    public DigitalCurrencyData() {
    }

//    public DigitalCurrencyData(double openLocal, double openUSD, double highLocal, double highUSD, double lowLocal,
//                               double lowUSD, double closeLocal, double closeUSD, double volume, double marketCap) {
//        this.openLocal = openLocal;
//        this.openUSD = openUSD;
//        this.highLocal = highLocal;
//        this.highUSD = highUSD;
//        this.lowLocal = lowLocal;
//        this.lowUSD = lowUSD;
//        this.closeLocal = closeLocal;
//        this.closeUSD = closeUSD;
//        this.volume = volume;
//        this.marketCap = marketCap;
//    }

//    public double getOpenLocal() {
//        return openLocal;
//    }
//
//    public void setOpenLocal(double openLocal) {
//        this.openLocal = openLocal;
//    }

    public double getOpenUSD() {
        return openUSD;
    }

    public void setOpenUSD(double openUSD) {
        this.openUSD = openUSD;
    }

//    public double getHighLocal() {
//        return highLocal;
//    }
//
//    public void setHighLocal(double highLocal) {
//        this.highLocal = highLocal;
//    }

    public double getHighUSD() {
        return highUSD;
    }

    public void setHighUSD(double highUSD) {
        this.highUSD = highUSD;
    }

//    public double getLowLocal() {
//        return lowLocal;
//    }
//
//    public void setLowLocal(double lowLocal) {
//        this.lowLocal = lowLocal;
//    }

    public double getLowUSD() {
        return lowUSD;
    }

    public void setLowUSD(double lowUSD) {
        this.lowUSD = lowUSD;
    }

//    public double getCloseLocal() {
//        return closeLocal;
//    }
//
//    public void setCloseLocal(double closeLocal) {
//        this.closeLocal = closeLocal;
//    }
//
//    public double getCloseUSD() {
//        return closeUSD;
//    }

    public void setCloseUSD(double closeUSD) {
        this.closeUSD = closeUSD;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(double marketCap) {
        this.marketCap = marketCap;
    }
}

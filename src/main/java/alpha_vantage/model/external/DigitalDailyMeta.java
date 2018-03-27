package alpha_vantage.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DigitalDailyMeta {

    @JsonProperty("1. Information")
    String information;
    @JsonProperty("2. Digital Currency Code")
    String digitalCurrencyCode;
    @JsonProperty("3. Digital Currency Name")
    String digitalCurrencyName;
    @JsonProperty("4. Market Code")
    String marketCode;
    @JsonProperty("5. Market Name")
    String marketName;
    @JsonProperty("6. Last Refreshed")
    String lastRefreshed;
    @JsonProperty("7. Time Zone")
    String timeZone;

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getDigitalCurrencyCode() {
        return digitalCurrencyCode;
    }

    public void setDigitalCurrencyCode(String digitalCurrencyCode) {
        this.digitalCurrencyCode = digitalCurrencyCode;
    }

    public String getDigitalCurrencyName() {
        return digitalCurrencyName;
    }

    public void setDigitalCurrencyName(String digitalCurrencyName) {
        this.digitalCurrencyName = digitalCurrencyName;
    }

    public String getMarketCode() {
        return marketCode;
    }

    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getLastRefreshed() {
        return lastRefreshed;
    }

    public void setLastRefreshed(String lastRefreshed) {
        this.lastRefreshed = lastRefreshed;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}

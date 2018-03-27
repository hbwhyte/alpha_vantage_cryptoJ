package alpha_vantage.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DigitalDailyResponse {
    @JsonProperty("Meta Data")
    DigitalDailyMeta metaData;
    @JsonProperty("Time Series (Digital Currency Daily)")
    private TimeSeries timeSeries;

    public DigitalDailyResponse() {
    }

    public DigitalDailyResponse(DigitalDailyMeta metaData, TimeSeries timeSeries) {
        this.metaData = metaData;
        this.timeSeries = timeSeries;
    }

    public DigitalDailyMeta getMetaData() {
        return metaData;
    }

    public void setMetaData(DigitalDailyMeta metaData) {
        this.metaData = metaData;
    }

    public TimeSeries getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(TimeSeries timeSeries) {
        this.timeSeries = timeSeries;
    }
}

package alpha_vantage.model.external;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.model.external.DigitalCurrencyData;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class TimeSeries {

    // TreeMap preserves sorting order
    @JsonIgnore
    private TreeMap<String, DigitalCurrencyData> days = new TreeMap<>();

    @JsonAnySetter
    public void setDays(String time, DigitalCurrencyData value){
        days.put(time,value);
    }

    @JsonAnyGetter
    public TreeMap<String, DigitalCurrencyData> getData() {
        return days;
    }

}

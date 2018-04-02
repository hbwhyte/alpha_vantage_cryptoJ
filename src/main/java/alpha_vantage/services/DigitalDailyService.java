package alpha_vantage.services;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.mappers.DigitalDailyMapper;
import alpha_vantage.model.internal.FindMax;
import alpha_vantage.model.external.DigitalDailyResponse;
import alpha_vantage.model.internal.DigitalCurrencyDaily;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class DigitalDailyService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DigitalDailyMapper digitalDailyMapper;

    @Value("${alphavantage.api-key}")
    private String apiKey;


    public DigitalDailyResponse searchDigitalDaily(DigitalCurrency symbol) {
        String fQuery = "https://www.alphavantage.co/query?function=DIGITAL_CURRENCY_DAILY&symbol=" + symbol + "&market=USD&apikey=" + apiKey;
        DigitalDailyResponse response = restTemplate.getForObject(fQuery, DigitalDailyResponse.class);
        return response;
    }

    public ArrayList<DigitalCurrencyDaily> searchDigital30(DigitalCurrency symbol) {
       DigitalDailyResponse response = searchDigitalDaily(symbol);
        ArrayList<DigitalCurrencyDaily> last30 = new ArrayList<>();

        for (LocalDate date = LocalDate.now().minusDays(30); date.isBefore(LocalDate.now().minusDays(1));
             date = date.plusDays(1)) {
            String mydate = date.toString();
            DigitalCurrencyDaily obj = new DigitalCurrencyDaily();
            obj.setDate(mydate);
            obj.setSymbol((response.getMetaData().getDigitalCurrencyCode().name()));
            obj.setOpen(response.getTimeSeries().getDays().get(obj.getDate()).getOpenUSD());
            obj.setHigh(response.getTimeSeries().getDays().get(obj.getDate()).getHighUSD());
            obj.setLow(response.getTimeSeries().getDays().get(obj.getDate()).getLowUSD());
            obj.setClose(response.getTimeSeries().getDays().get(obj.getDate()).getCloseUSD());
            obj.setVolume(response.getTimeSeries().getDays().get(obj.getDate()).getVolume());
            obj.setMarketCap(response.getTimeSeries().getDays().get(obj.getDate()).getMarketCap());
            last30.add(obj);
            }
        return last30;
    }

    public String persist(DigitalDailyResponse response) {
        DigitalCurrencyDaily obj = new DigitalCurrencyDaily();
        String symbol = response.getMetaData().getDigitalCurrencyCode().name();
        int rowsUpdated = 0;

        // Saves the past 30 days to the DB
        for (LocalDate date = LocalDate.now().minusDays(30); date.isBefore(LocalDate.now().minusDays(1));
             date = date.plusDays(1)) {
            String mydate = date.toString();
            if (digitalDailyMapper.doubleCheck(mydate, symbol) == null) {
                obj.setDate(mydate);
                obj.setSymbol((response.getMetaData().getDigitalCurrencyCode().name()));
                obj.setOpen(response.getTimeSeries().getDays().get(obj.getDate()).getOpenUSD());
                obj.setHigh(response.getTimeSeries().getDays().get(obj.getDate()).getHighUSD());
                obj.setLow(response.getTimeSeries().getDays().get(obj.getDate()).getLowUSD());
                obj.setClose(response.getTimeSeries().getDays().get(obj.getDate()).getCloseUSD());
                obj.setVolume(response.getTimeSeries().getDays().get(obj.getDate()).getVolume());
                obj.setMarketCap(response.getTimeSeries().getDays().get(obj.getDate()).getMarketCap());

                digitalDailyMapper.insertDay(obj);
                rowsUpdated++;
            }
        }
        return rowsUpdated + " rows updated.";
    }

    // Finds max value of a digital currency over the past specified number of days
    public FindMax findMax(DigitalCurrency symbol, int numDays) {
        DigitalDailyResponse response = searchDigitalDaily(symbol);

        FindMax obj = new FindMax();
        double maxVal = -1;
        String maxDate = null;
        obj.setSymbol(symbol);

        for (LocalDate date = LocalDate.now().minusDays(numDays + 1); date.isBefore(LocalDate.now().minusDays(1));
             date = date.plusDays(1)) {
            String mydate = date.toString();
            obj.setDate(mydate);
            obj.setHighUSD(response.getTimeSeries().getDays().get(obj.getDate()).getHighUSD());
            if (obj.getHighUSD() > maxVal) {
                maxVal = obj.getHighUSD();
                maxDate = obj.getDate();
            }
        }
        obj.setHighUSD(maxVal);
        obj.setDate(maxDate);
        return obj;

    }

    public DigitalCurrencyDaily addNew(DigitalCurrencyDaily entry) {
        digitalDailyMapper.insertDay(entry);
        return digitalDailyMapper.getByID(entry.getId());
    }

    public DigitalCurrencyDaily updateById(DigitalCurrencyDaily entry) {
        digitalDailyMapper.updateEntry(entry);
        return digitalDailyMapper.getByID(entry.getId());
    }

    /**
     * Marks entry at that id as inactive (isActive = 0). Does not permanently remove item from database.
     *
     * @param id of entry to be set to inactive
     * @return record of inactive entry
     */
    public DigitalCurrencyDaily deleteByID(int id) {
        digitalDailyMapper.deleteEntry(id);
        return digitalDailyMapper.getByID(id);
    }

    public DigitalCurrencyDaily getByID(int id) {
        return digitalDailyMapper.getByID(id);
    }

}


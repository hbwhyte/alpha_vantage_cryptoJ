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

    /**
     * Calls the Alpha Vantage API's Digital Currency Daily function, and searches
     * their database for all records for that symbol in USD.
     *
     * @param symbol enum of DigitalCurrency, of digital currencies supported by
     *               Alpha Vantage. Case sensitive (all caps).
     * @return DigitalDailyResponse full JSON
     */
    public DigitalDailyResponse searchDigitalDaily(DigitalCurrency symbol) {
        String fQuery = "https://www.alphavantage.co/query?function=DIGITAL_CURRENCY_DAILY&symbol=" + symbol + "&market=USD&apikey=" + apiKey;
        DigitalDailyResponse response = restTemplate.getForObject(fQuery, DigitalDailyResponse.class);
        return response;
    }

    /**
     * Calls the searchDigitalDaily() method, and parses the response to return
     * an ArrayList of DigitalCurrencyDaily info for the previous 30 days data
     * for the entered symbol.
     * <p>
     * Calls the persist() method to add the search objects to the database if
     * they are not currently there
     *
     * @param symbol enum of DigitalCurrency, of digital currencies supported by
     *               Alpha Vantage. Case sensitive (all caps).
     * @return ArrayList of DigitalCurrencyDaily objects
     */
    public ArrayList<DigitalCurrencyDaily> searchDigital30(DigitalCurrency symbol) {
        DigitalDailyResponse response = searchDigitalDaily(symbol);
        ArrayList<DigitalCurrencyDaily> last30 = new ArrayList<>();

        // Loops through the 30 previous days
        // Creates a new DigitalCurrencyDaily object for each day
        for (LocalDate date = LocalDate.now().minusDays(31); date.isBefore(LocalDate.now().minusDays(1));
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
        //TODO Make persist() a separate thread
        // If any of the objects in the last30 ArrayList are not in the database, persist() adds them
        persist(last30);
        return last30;
    }

    /**
     * For each object of the ArrayList, checks to see if that item currently
     * exists in the database, based on checking the date and symbol. If it
     * is not currently there, it inserts it.
     * <p>
     * Prints out the number of rows updated.
     *
     * @param last30 ArrayList of DigitalCurrencyDaily objects
     */
    public void persist(ArrayList<DigitalCurrencyDaily> last30) {
        int rowsUpdated = 0;

        for (DigitalCurrencyDaily daily : last30) {
            if (digitalDailyMapper.doubleCheck(daily.getDate(), daily.getSymbol()) == null) {
                digitalDailyMapper.insertDay(daily);
                rowsUpdated++;
            }
        }
        System.out.println(rowsUpdated + " rows updated.");
    }

    /**
     * Finds the max value of a digital currency over the past user defined number of days.
     *
     * @param symbol  enum of DigitalCurrency, of digital currencies supported by
     *                Alpha Vantage. Case sensitive (all caps).
     * @param numDays integer number of days to compare against. (e.g. highest price in the
     *                past 60 days etc.)
     * @return FindMax object
     */
    public FindMax findMax(DigitalCurrency symbol, int numDays) {
        // Calls Alpha Vantage API to return all historical information for that symbol
        DigitalDailyResponse response = searchDigitalDaily(symbol);

        FindMax obj = new FindMax();
        double maxVal = -1;
        String maxDate = null;
        obj.setSymbol(symbol);

        // Loops through dates, then compares their highest daily prices
        for (LocalDate date = LocalDate.now().minusDays(numDays + 1); date.isBefore(LocalDate.now().minusDays(1));
             date = date.plusDays(1)) {
            String mydate = date.toString();
            obj.setDate(mydate);
            obj.setHighUSD(response.getTimeSeries().getDays().get(obj.getDate()).getHighUSD());
            // if it finds a new highest price, it changes the value of maxValue and maxDate
            if (obj.getHighUSD() > maxVal) {
                maxVal = obj.getHighUSD();
                maxDate = obj.getDate();
            }
        }
        obj.setHighUSD(maxVal);
        obj.setDate(maxDate);
        return obj;
    }

    /**
     * Adds new individual entry to the database
     *
     * @param entry DigitalCurrencyDaily object to be added to the database
     * @return DigitalCurrencyDaily object of the new entry with id
     */
    public DigitalCurrencyDaily addNew(DigitalCurrencyDaily entry) {
        digitalDailyMapper.insertDay(entry);
        return digitalDailyMapper.getByID(entry.getId());
    }

    /**
     * Updates individual entry in the database
     *
     * @param entry DigitalCurrencyDaily object with the information to update
     *              the database.
     * @return DigitalCurrencyDaily object of the updated entry with id
     */
    public DigitalCurrencyDaily updateById(DigitalCurrencyDaily entry) {
        digitalDailyMapper.updateEntry(entry);
        return digitalDailyMapper.getByID(entry.getId());
    }

    /**
     * Marks entry at that id as inactive (isActive = 0). Does not permanently
     * remove item from database.
     *
     * @param id of entry to be set to inactive
     * @return record of inactive entry
     */
    public DigitalCurrencyDaily deleteByID(int id) {
        digitalDailyMapper.deleteEntry(id);
        return digitalDailyMapper.getByID(id);
    }

    /**
     * Returns one record from the database by their id number
     *
     * @param id integer of object to be returned
     * @return DigitalCurrencyDaily object of the entry by id
     */
    public DigitalCurrencyDaily getByID(int id) {
        return digitalDailyMapper.getByID(id);
    }
}


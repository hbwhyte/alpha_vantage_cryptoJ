package alpha_vantage.services;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.mappers.DigitalDailyMapper;
import alpha_vantage.model.external.DigitalDailyMeta;
import alpha_vantage.model.internal.FindMax;
import alpha_vantage.model.external.DigitalDailyResponse;
import alpha_vantage.model.internal.DigitalCurrencyDaily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Service
public class DigitalDailyService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DigitalDailyMapper digitalDailyMapper;

    @Autowired
    DataGeneration dataGeneration;

    @Value("${alphavantage.api-key}")
    private String apiKey;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * Calls the Alpha Vantage API's Digital Currency Daily function, and searches
     * their database for all records for that symbol in USD.
     *
     * @param symbol enum of DigitalCurrency, of digital currencies supported by
     *               Alpha Vantage. Case sensitive (all caps).
     * @return DigitalDailyResponse full JSON
     */

    @Cacheable(value = "digitaldaily")
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
    @Cacheable(value = "digitaldaily")
    public ArrayList<DigitalCurrencyDaily> searchDigital30(DigitalCurrency symbol) {
        long start = System.currentTimeMillis();
        System.out.println("Start searching " + symbol);
        DigitalDailyResponse response;  //searchDigitalDaily(symbol);
        response = dataGeneration.searchAsync(symbol);
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
        // If any of the objects in the last30 ArrayList are not in the database, persist() adds them
        //persist(last30);
        System.out.println("Done searching for " + symbol + " in " + (System.currentTimeMillis() - start));
        dataGeneration.persist(last30);
        return last30;
    }

    /**
     * Returns a List of all results for a given symbol from the database.
     *
     * @param symbol enum of DigitalCurrency, of digital currencies supported by
     *               Alpha Vantage. Case sensitive (all caps).
     * @return List of DigitalCurrencyDaily objects
     */
    @Cacheable("digitaldaily")
    public List<DigitalCurrencyDaily> searchDigital(DigitalCurrency symbol) {
        List<DigitalCurrencyDaily> results = digitalDailyMapper.getAllBySymbol(symbol);
        return results;
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
        long start = System.currentTimeMillis();
        System.out.println("Start persisting");
        for (DigitalCurrencyDaily daily : last30) {
            if (digitalDailyMapper.doubleCheck(daily.getDate(), daily.getSymbol()) == null) {
                digitalDailyMapper.insertDay(daily);
                rowsUpdated++;
            }
        }
        System.out.println("Done persisting in " + (System.currentTimeMillis() - start));
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
    @CachePut(value = "digitaldaily", key = "#entry.id")
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
    @CachePut(value = "digitaldaily", key = "#entry.id")
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
    @CacheEvict(value = "digitaldaily", key = "#id")
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
    @Cacheable(value = "digitaldaily", key = "#id")
    public DigitalCurrencyDaily getByID(int id) {
        return digitalDailyMapper.getByID(id);
    }

    @CacheEvict(value = {"digitaldaily"}, allEntries = true)
    public void clearCache() {
        logger.info("Clearing all caches");
    }

    /**
     * Searches every enum asynchronously and persists any non-duplicate
     * data to the database.
     */
    public void persistAll() {
        EnumSet.allOf(DigitalCurrency.class).forEach(coin -> dataGeneration.parseData(coin));
    }


    /**
     * Small test method to see if any of the Enums are null coming from the
     * Alpha Vantage API. It writes the list of active Enums if needed.
     */
    public void nullChecker() {
        EnumSet.allOf(DigitalCurrency.class).forEach(coin -> {
            DigitalDailyMeta response = searchDigitalDaily(coin).getMetaData();
            if (response != null) {
                System.out.println(coin + "(\""+coin.getFullName()+"\"),");
            }
        });
    }

}


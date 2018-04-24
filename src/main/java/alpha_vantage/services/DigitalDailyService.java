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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
     * Returns a List of all results for a given symbol from the database.
     *
     * @param symbol enum of DigitalCurrency, of digital currencies supported by
     *               Alpha Vantage. Case sensitive (all caps).
     * @return List of DigitalCurrencyDaily objects
     */
    @Cacheable("digitaldaily")
    public List<DigitalCurrencyDaily> searchDigital(DigitalCurrency symbol) {
        return digitalDailyMapper.getAllBySymbol(symbol);
    }

    /**
     * Returns a sorted ArrayList of DigitalCurrencyDaily info from the database
     * for the previous 30 days data of the entered symbol.
     *
     * @param symbol enum of DigitalCurrency, of digital currencies supported by
     *               Alpha Vantage. Case sensitive (all caps).
     * @return ArrayList of DigitalCurrencyDaily objects
     */
    @Cacheable(value = "digitaldaily")
    public List<DigitalCurrencyDaily> searchDigital30(DigitalCurrency symbol) {
        // For tracking speed of search
        long start = System.currentTimeMillis();
        logger.info("Start searching " + symbol);
        // Gets all instances of symbol from the database
        List<DigitalCurrencyDaily> responses = digitalDailyMapper.getAllBySymbol(symbol);
        List<DigitalCurrencyDaily> last30 = new ArrayList<>();
        // Loops through List to get the last 30 days of data
        for (LocalDate date = LocalDate.now().minusDays(1); date.isAfter(LocalDate.now().minusDays(32));
             date = date.minusDays(1)) {
            // Double check to make sure only 30 results are returned
            if (last30.size() == 30) {
                break;
            }
            // Loop sorts entries by date since the data in the database is not ordered
            for (int i = 0; i < responses.size(); i++) {
                if (responses.get(i).getDate().equals(date.format(DateTimeFormatter.ISO_LOCAL_DATE))) {
                    last30.add(responses.get(i));
                    break;
                }
            }
        }
        logger.info("Done searching for " + symbol + " in " + (System.currentTimeMillis() - start));
        return last30;
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
        // Returns all historical data for that symbol from our database
        List<DigitalCurrencyDaily> responses = digitalDailyMapper.getAllBySymbol(symbol);

        FindMax max = new FindMax();
        double maxVal = -1;
        String maxDate = null;
        max.setSymbol(symbol);

        // Loops through days, then compares their highest daily prices
        for (DigitalCurrencyDaily day : responses) {
            // if it finds a new highest price within the given date range, it changes the value of maxValue and maxDate
            if (LocalDate.parse(day.getDate()).isAfter(LocalDate.now().minusDays(numDays + 1))
                    && day.getHigh() > maxVal) {
                maxVal = day.getHigh();
                maxDate = day.getDate();
            }
        }
        // Sets object based on final maxVal and maxDate
        max.setHighUSD(maxVal);
        max.setDate(maxDate);
        return max;
    }

    @CacheEvict(value = {"digitaldaily"}, allEntries = true)
    public void clearCache() {
        logger.info("Clearing all caches");
    }

    /**
     * Small test method to see if any of the Enums are null coming from the
     * Alpha Vantage API. It writes the list of active Enums if needed.
     */
    public void nullChecker() {
        EnumSet.allOf(DigitalCurrency.class).forEach(coin -> {
            DigitalDailyMeta response = dataGeneration.searchAsync(coin).getMetaData();
            if (response != null) {
                System.out.println(coin + "(\"" + coin.getFullName() + "\"),");
            }
        });
    }
}


package alpha_vantage.services;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.mappers.DigitalDailyMapper;
import alpha_vantage.model.external.DigitalDailyResponse;
import alpha_vantage.model.internal.DigitalCurrencyDaily;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * DataGeneration class is responsible for making API calls and keeping
 * the database information up to date.
 */
@Service
public class DataGeneration {

    Logger logger = LoggerFactory.getLogger(DataGeneration.class);

    @Autowired
    DigitalDailyMapper digitalDailyMapper;

    @Autowired
    RestTemplate restTemplate;

    @Value("${alphavantage.api-key}")
    String apiKey;


    @Async
    public DigitalDailyResponse searchAsync(DigitalCurrency symbol) {
        String fQuery = "https://www.alphavantage.co/query?function=DIGITAL_CURRENCY_DAILY&symbol=" + symbol + "&market=USD&apikey=" + apiKey;
        return restTemplate.getForObject(fQuery, DigitalDailyResponse.class);
    }

    /**
     * For a given symbol, makes the API Call then maps the response for all provided
     * dates as DigitalCurrencyDaily POJOs. Skips dates with no data.
     *
     * @param symbol enum of DigitalCurrency, of digital currencies supported by
     *                Alpha Vantage. Case sensitive (all caps).
     * @throws NullPointerException is thrown if the symbol returns no information
     */
    @Async
    public ArrayList<DigitalCurrencyDaily> parseData(DigitalCurrency symbol) throws NullPointerException {
        // Starts counter to track time taken to persist one result set
        long start = System.currentTimeMillis();
        logger.info("Start searching " + symbol);

        DigitalDailyResponse response;
        ArrayList<DigitalCurrencyDaily> allRecords = new ArrayList<>();
        try {
            // Makes API call to Alpha Vantage
            response = searchAsync(symbol);
            // Gets date of most recent entry of the response
            LocalDate date = getDate(response);
            // Loops through all dates with data, and maps them to a DigitalCurrencyDaily POJO
            for (int i = 0; i < response.getTimeSeries().getDays().size(); i++) {
                DigitalCurrencyDaily obj = new DigitalCurrencyDaily();
                // Formats date as yyyy-MM-dd
                obj.setDate(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
                obj.setSymbol((response.getMetaData().getDigitalCurrencyCode().name()));
                // Nested try because API call often has gaps of dates within their data
                try {
                    obj.setOpen(response.getTimeSeries().getDays().get(obj.getDate()).getOpenUSD());
                    obj.setHigh(response.getTimeSeries().getDays().get(obj.getDate()).getHighUSD());
                    obj.setLow(response.getTimeSeries().getDays().get(obj.getDate()).getLowUSD());
                    obj.setClose(response.getTimeSeries().getDays().get(obj.getDate()).getCloseUSD());
                    obj.setVolume(response.getTimeSeries().getDays().get(obj.getDate()).getVolume());
                    obj.setMarketCap(response.getTimeSeries().getDays().get(obj.getDate()).getMarketCap());
                    // If all of the info is there, add the mapped object to the ArrayList
                    allRecords.add(obj);
                } catch (NullPointerException e) {
                    // If there is a gap of dates in the API call date, logs a warning but keeps going
                    // Warning: on many of the smaller alt coins, there can be fairly significant gaps. This is expected.
                    logger.warn(date.format(DateTimeFormatter.ISO_LOCAL_DATE) + " for " + symbol + " is missing from the API call.");
                    // If date was missing, it was not part of the .size(), so don't increment i
                    i--;
                }
                // Move on to next date
                date = date.minusDays(1);
            }
        } catch (NullPointerException e) {
            logger.error(symbol + " was null");
            throw new NullPointerException(symbol + " was null");
        }
        // Log how long the API call and mapping took per symbol
        logger.info("Done searching for " + symbol + " in " + (System.currentTimeMillis() - start));
        // Persist mapped records to the database
        persist(allRecords);
        return allRecords;
    }

    /**
     * Asyncronous method that persists an ArrayList of Alpha Vantage
     * API calls to the database, if that results is not already in the
     * database.
     *
     * @param results ArrayList of DigitalCurrencyDaily mapped objects
     */
    @Async
    public void persist(ArrayList<DigitalCurrencyDaily> results) {
        // Starts counter to track time taken to persist one result set
        long start = System.currentTimeMillis();
        logger.info("Starting persisting " + results.get(0).getSymbol());

        // Checks if the data for that specific date + symbol already exists in the database
        // If not, persists to the database
        for (DigitalCurrencyDaily daily : results) {
            if (digitalDailyMapper.doubleCheck(daily.getDate(), daily.getSymbol()) == null) {
                digitalDailyMapper.insertDay(daily);
            }
        }
        // Logs time it took to persist
        logger.info("Done persisting " + results.get(0).getSymbol() + " in " +
                (System.currentTimeMillis() - start) + " milliseconds.");
    }

    /**
     * Pulls from the Alpha Vantage JSON metadata what the most recent data point
     * is for that cryptocurrency (when it was last refreshed)
     *
     * @param response mapped Alpha Vanatage API call for one symbol
     * @return date of most recent data point, as a LocalDate object
     */
    private LocalDate getDate(DigitalDailyResponse response) {
        StringBuilder sb = new StringBuilder(response.getMetaData().getLastRefreshed());
        // Crops Alpha Vantages default format "2018-04-17 (end of day)"
        sb.delete(10, 23);
        // Parses String to LocalDate
        return LocalDate.parse(sb.toString(), DateTimeFormatter.ISO_LOCAL_DATE);
    }
}


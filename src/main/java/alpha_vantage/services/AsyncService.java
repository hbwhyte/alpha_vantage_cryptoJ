package alpha_vantage.services;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.mappers.DigitalDailyMapper;
import alpha_vantage.model.external.DigitalDailyResponse;
import alpha_vantage.model.internal.DigitalCurrencyDaily;

import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static alpha_vantage.enums.DigitalCurrency.AGI;
import static alpha_vantage.enums.DigitalCurrency.BTC;

@Service
public class AsyncService {

    Logger logger = LoggerFactory.getLogger(AsyncService.class);

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

    @Async
    public void searchAsyncAll(DigitalCurrency symbol)
            throws NullPointerException {

        long start = System.currentTimeMillis();
        System.out.println("Start searching " + symbol);
        DigitalDailyResponse response;
        ArrayList<DigitalCurrencyDaily> allRecords = new ArrayList<>();
        try {
            response = searchAsync(symbol);
            LocalDate date = getDate(response);
            for (int i = 0; i < response.getTimeSeries().getDays().size(); i++) {
                DigitalCurrencyDaily obj = new DigitalCurrencyDaily();
                obj.setDate(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
                obj.setSymbol((response.getMetaData().getDigitalCurrencyCode().name()));
                try {
                    obj.setOpen(response.getTimeSeries().getDays().get(obj.getDate()).getOpenUSD());
                    obj.setHigh(response.getTimeSeries().getDays().get(obj.getDate()).getHighUSD());
                    obj.setLow(response.getTimeSeries().getDays().get(obj.getDate()).getLowUSD());
                    obj.setClose(response.getTimeSeries().getDays().get(obj.getDate()).getCloseUSD());
                    obj.setVolume(response.getTimeSeries().getDays().get(obj.getDate()).getVolume());
                    obj.setMarketCap(response.getTimeSeries().getDays().get(obj.getDate()).getMarketCap());
                    allRecords.add(obj);
                } catch (NullPointerException e) {
                    logger.warn(date.format(DateTimeFormatter.ISO_LOCAL_DATE) + " for " + symbol + " is missing from the API call.");
                }
                date = date.minusDays(1);
            }
        } catch (NullPointerException e) {
            logger.warn("null");
            throw new NullPointerException(symbol.getFullName() + "was null");
        }
        System.out.println("Done searching for " + symbol + " in " + (System.currentTimeMillis() - start));
        persist(allRecords);
    }

    @Async
    public void persist(ArrayList<DigitalCurrencyDaily> results) {
        long start = System.currentTimeMillis();
        System.out.println("Starting persisting " + results.get(0).getSymbol());
        for (DigitalCurrencyDaily daily : results) {
            if (digitalDailyMapper.doubleCheck(daily.getDate(), daily.getSymbol()) == null) {
                digitalDailyMapper.insertDay(daily);
            }
        }
        System.out.println("Done persisting " + results.get(0).getSymbol() + " in " + (System.currentTimeMillis() - start));
    }

    private LocalDate getDate(DigitalDailyResponse response) {
        StringBuilder sb = new StringBuilder(response.getMetaData().getLastRefreshed());
        sb.delete(10, 23);
        System.out.println(LocalDate.parse(sb.toString(),DateTimeFormatter.ISO_LOCAL_DATE));
        return LocalDate.parse(sb.toString(),DateTimeFormatter.ISO_LOCAL_DATE);
    }
}


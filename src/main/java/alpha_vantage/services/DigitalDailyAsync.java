package alpha_vantage.services;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.mappers.DigitalDailyMapper;
import alpha_vantage.model.external.DigitalDailyResponse;
import alpha_vantage.model.internal.DigitalCurrencyDaily;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class DigitalDailyAsync {

    @Autowired
    DigitalDailyMapper digitalDailyMapper;

    @Autowired
    RestTemplate restTemplate;

    @Value("${alphavantage.api-key}")
    private String apiKey;

    @Async
    @Transactional
    public void persist(ArrayList<DigitalCurrencyDaily> last30) {
        long start = System.currentTimeMillis();
        System.out.println("Starting persisting " + last30.get(0).getSymbol());
        for (DigitalCurrencyDaily daily : last30) {
            if (digitalDailyMapper.doubleCheck(daily.getDate(), daily.getSymbol()) == null) {
                digitalDailyMapper.insertDay(daily);
            }
        }
        System.out.println("Done persisting "+ last30.get(0).getSymbol() +" in " + (System.currentTimeMillis() - start));
    }

    @Async
    @Transactional
    public CompletableFuture<DigitalDailyResponse> searchAsync(DigitalCurrency symbol) {
        String fQuery = "https://www.alphavantage.co/query?function=DIGITAL_CURRENCY_DAILY&symbol=" + symbol + "&market=USD&apikey=" + apiKey;
        DigitalDailyResponse response = restTemplate.getForObject(fQuery, DigitalDailyResponse.class);
        return CompletableFuture.completedFuture(response);
    }

    @Async
    @Transactional
    public CompletableFuture<ArrayList<DigitalCurrencyDaily>> searchAsync30(DigitalCurrency symbol) {
        long start = System.currentTimeMillis();
        System.out.println("Start searching " + symbol);
        DigitalDailyResponse response = null;
        try {
            response = searchAsync(symbol).get();
        } catch (NullPointerException e) {
            System.out.println(symbol + " was null");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println(symbol + "interrupted exception");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println(symbol + " execution exception");
            e.printStackTrace();
        }
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
        persist(last30);
        return CompletableFuture.completedFuture(last30);
    }

    @Async
    @Transactional
    public CompletableFuture<ArrayList<DigitalCurrencyDaily>> searchAsyncAll(DigitalCurrency symbol) {
        long start = System.currentTimeMillis();
        System.out.println("Start searching " + symbol);
        DigitalDailyResponse response = null;
        try {
            response = searchAsync(symbol).get();
        } catch (NullPointerException e) {
            System.out.println(symbol + " was null");
        } catch (InterruptedException e) {
            System.out.println(symbol + "interrupted exception");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println(symbol + " execution exception");
            e.printStackTrace();
        }
        ArrayList<DigitalCurrencyDaily> allRecords = new ArrayList<>();
        // Loops through the 30 previous days
        // Creates a new DigitalCurrencyDaily object for each day

        LocalDate date = LocalDate.now().minusDays(2);
        for (int i = 0; i < response.getTimeSeries().getDays().size(); i++) {
            DigitalCurrencyDaily obj = new DigitalCurrencyDaily();
            obj.setDate(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
            obj.setSymbol((response.getMetaData().getDigitalCurrencyCode().name()));
            obj.setOpen(response.getTimeSeries().getDays().get(obj.getDate()).getOpenUSD());
            obj.setHigh(response.getTimeSeries().getDays().get(obj.getDate()).getHighUSD());
            obj.setLow(response.getTimeSeries().getDays().get(obj.getDate()).getLowUSD());
            obj.setClose(response.getTimeSeries().getDays().get(obj.getDate()).getCloseUSD());
            obj.setVolume(response.getTimeSeries().getDays().get(obj.getDate()).getVolume());
            obj.setMarketCap(response.getTimeSeries().getDays().get(obj.getDate()).getMarketCap());
            allRecords.add(obj);
            date = date.minusDays(1);
        }
        // If any of the objects in the last30 ArrayList are not in the database, persist() adds them
        //persist(last30);
        System.out.println("Done searching for " + symbol + " in " + (System.currentTimeMillis() - start));
        persist(allRecords);
        return CompletableFuture.completedFuture(allRecords);
    }

}


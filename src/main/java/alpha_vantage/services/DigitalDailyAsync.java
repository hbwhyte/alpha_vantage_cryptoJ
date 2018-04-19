//package alpha_vantage.services;
//
//import alpha_vantage.enums.DigitalCurrency;
//import alpha_vantage.mappers.DigitalDailyMapper;
//import alpha_vantage.model.external.DigitalDailyResponse;
//import alpha_vantage.model.internal.DigitalCurrencyDaily;
//import org.apache.ibatis.jdbc.Null;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Retryable;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestTemplate;
//
//
//import java.lang.reflect.InvocationTargetException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.EnumSet;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
//@Service
//public class DigitalDailyAsync {
//
//    Logger logger = LoggerFactory.getLogger(DigitalDailyAsync.class);
//
//    @Autowired
//    DigitalDailyMapper digitalDailyMapper;
//
//    @Autowired
//    RestTemplate restTemplate;
//
//    @Value("${alphavantage.api-key}")
//    private String apiKey;
//
////    @Async
////    @Transactional
//    public void persist(ArrayList<DigitalCurrencyDaily> last30) {
//        long start = System.currentTimeMillis();
//        System.out.println("Starting persisting " + last30.get(0).getSymbol());
//        for (DigitalCurrencyDaily daily : last30) {
//            if (digitalDailyMapper.doubleCheck(daily.getDate(), daily.getSymbol()) == null) {
//                digitalDailyMapper.insertDay(daily);
//            }
//        }
//        System.out.println("Done persisting "+ last30.get(0).getSymbol() +" in " + (System.currentTimeMillis() - start));
//    }
//
//
//    @Async
//    @Transactional
//    public CompletableFuture<DigitalDailyResponse> searchAsync(DigitalCurrency symbol) {
//        String fQuery = "https://www.alphavantage.co/query?function=DIGITAL_CURRENCY_DAILY&symbol=" + symbol + "&market=USD&apikey=" + apiKey;
//        DigitalDailyResponse response = restTemplate.getForObject(fQuery, DigitalDailyResponse.class);
//        if (response == null) {
//            throw new NullPointerException();
//        }
//        return CompletableFuture.completedFuture(response);
//    }
//
//
//}
//

package alpha_vantage.services;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.mappers.DigitalDailyMapper;
import alpha_vantage.model.external.DigitalDailyResponse;
import alpha_vantage.model.internal.DigitalCurrencyDaily;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Service
public class DigitalDailyTask {

    @Autowired
    DigitalDailyMapper digitalDailyMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    RestTemplate restTemplate;

    @Value("${alphavantage.api-key}")
    private String apiKey;

    @Async
    @Transactional
    public void persist(ArrayList<DigitalCurrencyDaily> last30) {
        long start = System.currentTimeMillis();
        System.out.println("Starting persisting");
        for (DigitalCurrencyDaily daily : last30) {
            if (digitalDailyMapper.doubleCheck(daily.getDate(), daily.getSymbol()) == null) {
                digitalDailyMapper.insertDay(daily);
            }
        }
        System.out.println("Done persisting in " + (System.currentTimeMillis() - start));
    }

    @Async
    @Transactional
    public CompletableFuture<DigitalDailyResponse> searchAsync(DigitalCurrency symbol) {
        long start = System.currentTimeMillis();
        System.out.println("Starting search");
        String fQuery = "https://www.alphavantage.co/query?function=DIGITAL_CURRENCY_DAILY&symbol=" + symbol + "&market=USD&apikey=" + apiKey;
        DigitalDailyResponse response = restTemplate.getForObject(fQuery, DigitalDailyResponse.class);
        System.out.println("Completed search in " + (System.currentTimeMillis() - start));
        return CompletableFuture.completedFuture(response);
    }

    public void executeAsynchronously() {

        MyThread myThread = applicationContext.getBean(MyThread.class);
        System.out.println("thread?");
        taskExecutor.execute(myThread);
    }
}


package alpha_vantage.services;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.mappers.AnalysisMapper;
import alpha_vantage.mappers.DigitalDailyMapper;
import alpha_vantage.model.internal.DigitalCurrencyDaily;
import alpha_vantage.model.internal.Indicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalysisService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DigitalDailyMapper digitalDailyMapper;

    @Autowired
    AnalysisMapper analysisMapper;

    public ArrayList<Indicator> indicators(DigitalCurrency symbol) {
        int timeframe = 90;
        // creates a timeseries to calculate indicators on
        TimeSeries series = buildTimeSeries(symbol);
        ClosePriceIndicator close = new ClosePriceIndicator(series);

        // calculates 30 tick simple moving average
        SMAIndicator sma = new SMAIndicator(close, timeframe);

        // calculates 30 tick exponential moving average
        EMAIndicator ema = new EMAIndicator(close, timeframe);

        // build ArrayList of Indicator
        ArrayList<Indicator> indicators = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // skip first 30 (not enough trailing data)
        for (int i = timeframe; i < series.getBarCount(); i++) {

            Indicator indicator = new Indicator();
            indicator.setDate(series.getBar(i).getEndTime().format(dtf));
            indicator.setSymbol(series.getName());
            indicator.setClose(series.getBar(i).getClosePrice().doubleValue());
            indicator.setDelta(series.getBar(i).getClosePrice().doubleValue()
                    - series.getBar(i - 1).getClosePrice().doubleValue());
            indicator.setSimpleMovingAverage(sma.getValue(i).doubleValue());

            indicators.add(indicator);

        }

        logger.info("indicators calculated");

//        // store in DB
//        persistIndicators(indicators);

        return indicators;
    }

    /**
     * Builds a TimeSeries for TA4J
     */
    private TimeSeries buildTimeSeries(DigitalCurrency symbol) {

        String symbolString = symbol.toString();

        TimeSeries series = new BaseTimeSeries(symbolString);

        // pull all rate records for symbol from DB
        List<DigitalCurrencyDaily> rates = digitalDailyMapper.getAllBySymbol(symbol);

        // iterate through rate records
        for (int i = 0; i < rates.size(); i++) {

            // get close date
            String date = rates.get(i).getDate();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-hh");
            ZonedDateTime closeDate = ZonedDateTime.parse(date, format);

            // create a bar for rate data (CloseDate, Open, High, Low, Close, Volume)
            Bar bar = new BaseBar(closeDate,(double)0, (double)0, (double)0, rates.get(i).getClose(),0);

            // add bar to series
            series.addBar(bar);
        }

        return series;
    }

    public static void main(String[] args) {

    }


//    public void persistIndicators(ArrayList<Indicator> indicators) {
//        // iterate through rates
//        for (Indicator indicator : indicators) {
//            try {
//                // check if already in DB
//                if (indicatorMapper.findIndicatorBySymbolAndDate(indicator.getSymbol(), indicator.getDate()) == null) {
//                    // persist to DB
//                    indicatorMapper.insertIndicator(indicator);
//                    logger.info(indicator.getSymbol() + " data persisted for " + indicator.getDate());
//                }
//            } catch (Exception ex) {
//                logger.error("could not persist to database: " + indicator.getSymbol() + " data for " + indicator.getDate());
//            }
//        }
//    }

}

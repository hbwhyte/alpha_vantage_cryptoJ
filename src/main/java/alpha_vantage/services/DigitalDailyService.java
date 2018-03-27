package alpha_vantage.services;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.enums.PhysicalCurrency;
import alpha_vantage.model.internal.FindMax;
import alpha_vantage.model.external.DigitalDailyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static alpha_vantage.enums.PhysicalCurrency.USD;

@Service
public class DigitalDailyService {
    @Autowired
    RestTemplate restTemplate;

    public DigitalDailyResponse searchDigitalDaily(DigitalCurrency symbol, PhysicalCurrency market) {
        String fQuery = "https://www.alphavantage.co/query?function=DIGITAL_CURRENCY_DAILY&symbol=" + symbol + "&market=" + market + "&apikey=[APIKEY]";
        DigitalDailyResponse response = restTemplate.getForObject(fQuery, DigitalDailyResponse.class);
        return response;
    }

    // Finds max value of a digital currency over the past specified number of days
    public FindMax findMax(DigitalCurrency symbol, int numDays) {
        DigitalDailyResponse response = searchDigitalDaily(symbol, USD);

        FindMax obj = new FindMax();
        double maxVal = -1;
        String maxDate = null;
        obj.setSymbol(symbol);

        for (LocalDate date = LocalDate.now().minusDays(numDays + 1); date.isBefore(LocalDate.now().minusDays(1));
             date = date.plusDays(1)) {
            String mydate = date.toString();
            obj.setDate(mydate);
            obj.setHighUSD(response.getTimeSeries().getData().get(obj.getDate()).getHighUSD());
            if (obj.getHighUSD() > maxVal) {
                maxVal = obj.getHighUSD();
                maxDate = obj.getDate();
            }
        }
        obj.setHighUSD(maxVal);
        obj.setDate(maxDate);
        return obj;

    }
}

//    public DigitalDailyResponse (final Map<String, String> metaData,
//                      final List<DigitalCurrencyData> digitalData) {
//            super(metaData, digitalData);
//        }
//
//        /**
//         * Creates {@code Daily} instance from json.
//         *
//         * @param market
//         * @param json   string to parse
//         * @return Daily instance
//         */
//        public static Daily from(Market market, String json) {
//            Parser parser = new Parser(market);
//            return parser.parseJson(json);
//        }
//
//        /**
//         * Helper class for parsing json to {@code Daily}.
//         *
//         * @see DigitalCurrencyParser
//         * @see JsonParser
//         */
//        private static class Parser extends DigitalCurrencyParser<Daily> {
//
//
//            /**
//             * Used to find correct key values in json
//             */
//            private final Market market;
//
//            public Parser(Market market) {
//                this.market = market;
//            }
//
//            @Override
//            String getDigitalCurrencyDataKey() {
//                return "Time Series (Digital Currency Daily)";
//            }
//
//            @Override
//            Daily resolve(Map<String, String> metaData,
//                          Map<String, Map<String, String>> digitalCurrencyData) {
//                List<DigitalCurrencyData> currencyDataList = new ArrayList<>();
//                digitalCurrencyData.forEach((key, values) -> {
//                    currencyDataList.add(
//                            new DigitalCurrencyData(
//                                    LocalDate.parse(key, SIMPLE_DATE_FORMAT).atStartOfDay(),
//                                    Double.parseDouble(values.get("1a. open (" + market.getValue() + ")")),
//                                    Double.parseDouble(values.get("1b. open (USD)")),
//                                    Double.parseDouble(values.get("2a. high (" + market.getValue() + ")")),
//                                    Double.parseDouble(values.get("2b. high (USD)")),
//                                    Double.parseDouble(values.get("3a. low (" + market.getValue() + ")")),
//                                    Double.parseDouble(values.get("3b. low (USD)")),
//                                    Double.parseDouble(values.get("4a. close (" + market.getValue() + ")")),
//                                    Double.parseDouble(values.get("4b. close (USD)")),
//                                    Double.parseDouble(values.get("5. volume")),
//                                    Double.parseDouble(values.get("6. market cap (USD)"))
//                            )
//                    );
//                });
//                return new Daily(metaData, currencyDataList);
//            }
//        }
//    }
//}

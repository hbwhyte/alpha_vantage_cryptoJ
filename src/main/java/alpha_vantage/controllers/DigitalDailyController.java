package alpha_vantage.controllers;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.enums.PhysicalCurrency;
import alpha_vantage.model.internal.FindMax;
import alpha_vantage.model.external.DigitalDailyResponse;
import alpha_vantage.services.DigitalDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/digitaldaily")
public class DigitalDailyController {
    @Autowired
    DigitalDailyService digitalDailyService;

    @RequestMapping("/search")
    public DigitalDailyResponse searchDigitalDaily(@RequestParam(value = "symbol", defaultValue = "BTC") DigitalCurrency symbol) {
        return digitalDailyService.searchDigitalDaily(symbol);
    }

    @RequestMapping("/max")
    public FindMax findMax(@RequestParam(value = "symbol", defaultValue = "BTC") DigitalCurrency symbol,
                           @RequestParam(value = "days", defaultValue = "30") int numDays) {
        return digitalDailyService.findMax(symbol, numDays);
    }


}
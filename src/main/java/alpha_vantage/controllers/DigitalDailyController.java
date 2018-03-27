package alpha_vantage.controllers;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.enums.PhysicalCurrency;
import alpha_vantage.model.external.DigitalDailyMeta;
import alpha_vantage.model.internal.DigitalCurrencyDaily;
import alpha_vantage.model.internal.FindMax;
import alpha_vantage.model.external.DigitalDailyResponse;
import alpha_vantage.services.DigitalDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    //Create
    @RequestMapping(method= RequestMethod.POST, value="/")
    public DigitalCurrencyDaily addNew(@RequestBody DigitalCurrencyDaily entry) {
        return digitalDailyService.addNew(entry);
    }

    //Read
    @RequestMapping(method= RequestMethod.GET, value="/")
    public DigitalCurrencyDaily getById(@RequestParam(value="id") int id) {
        return digitalDailyService.getByID(id);
    }

    //Update
    @RequestMapping(method= RequestMethod.PATCH, value="/")
    public DigitalCurrencyDaily updateById(@RequestBody DigitalCurrencyDaily entry){
        return digitalDailyService.updateById(entry);
    }

    //Delete
    @RequestMapping(method= RequestMethod.DELETE, value="/")
    public DigitalCurrencyDaily deleteById(@RequestParam(value="id")int id){
        return digitalDailyService.deleteByID(id);
    }

}
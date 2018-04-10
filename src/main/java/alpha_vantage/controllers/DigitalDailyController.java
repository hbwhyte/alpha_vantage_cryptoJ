package alpha_vantage.controllers;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.model.internal.DigitalCurrencyDaily;
import alpha_vantage.model.internal.FindMax;
import alpha_vantage.services.DigitalDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/digitaldaily")
public class DigitalDailyController {

    @Autowired
    DigitalDailyService digitalDailyService;

    /**
     * GET request that calls searchDigital30(). Default search is for ETH (Ethereum).
     *
     * @param symbol enum of DigitalCurrency, of digital currencies supported by
     *               Alpha Vantage. Case sensitive (all caps).
     * @return DigitalDailyResponse object (JSON)
     */
    @RequestMapping("/search")
    public ArrayList<DigitalCurrencyDaily> searchDigital30(@RequestParam(value = "symbol", defaultValue = "ETH") DigitalCurrency symbol) {
        long start = System.currentTimeMillis();
        ArrayList<DigitalCurrencyDaily> timed = digitalDailyService.searchDigital30(symbol);
        System.out.println("Completed search in " + (System.currentTimeMillis() - start));
        return timed;
    }

    /**
     * GET request that calls findMax(). Default search is for the highest price of
     * BTC (Bitcoin) over the past 30 days.
     *
     * @param symbol  enum of DigitalCurrency, of digital currencies supported by
     *                Alpha Vantage. Case sensitive (all caps).
     * @param numDays number of days to compare against
     * @return FindMax object (JSON)
     */
    @RequestMapping("/max")
    public FindMax findMax(@RequestParam(value = "symbol", defaultValue = "BTC") DigitalCurrency symbol,
                           @RequestParam(value = "days", defaultValue = "30") int numDays) {
        return digitalDailyService.findMax(symbol, numDays);
    }

    /**
     * POST request that runs an INSERT query (Create)
     *
     * @param entry as DigitalCurrencyDaily object in the body of the POST request.
     * @return the new added DigitalCurrencyDaily object
     */
    @RequestMapping(method = RequestMethod.POST, value = "/")
    public DigitalCurrencyDaily addNew(@RequestBody DigitalCurrencyDaily entry) {
        return digitalDailyService.addNew(entry);
    }

    /**
     * GET request that runs a SELECT query (Read)
     *
     * @param id integer to specify object id
     * @return the DigitalCurrencyDaily object with that id
     */
    @RequestMapping(method = RequestMethod.GET, value = "/")
    public DigitalCurrencyDaily getById(@RequestParam(value = "id") int id) {
        return digitalDailyService.getByID(id);
    }

    /**
     * PATCH request that runs a UPDATE query (Update)
     *
     * @param entry as DigitalCurrencyDaily object in the body of the PATCH request.
     * @return the DigitalCurrencyDaily object with that id
     */
    @RequestMapping(method = RequestMethod.PATCH, value = "/")
    public DigitalCurrencyDaily updateById(@RequestBody DigitalCurrencyDaily entry) {
        return digitalDailyService.updateById(entry);
    }

    /**
     * DELETE request that runs a UPDATE query to set as inactive (Delete)
     *
     * @param id integer to specify object id
     * @return the deactivated DigitalCurrencyDaily object with that id
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/")
    public DigitalCurrencyDaily deleteById(@RequestParam(value = "id") int id) {
        return digitalDailyService.deleteByID(id);
    }

    @RequestMapping(value = "/cache", method = RequestMethod.DELETE)
    public void clearCache() {
        digitalDailyService.clearCache();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/persist30")
    public void persist30() {
        digitalDailyService.persistAll();
    }

}
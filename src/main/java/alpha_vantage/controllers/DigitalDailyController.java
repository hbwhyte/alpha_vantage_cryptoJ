package alpha_vantage.controllers;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.model.internal.DigitalCurrencyDaily;
import alpha_vantage.model.internal.FindMax;
import alpha_vantage.services.DataGeneration;
import alpha_vantage.services.DigitalDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/digitaldaily")
public class DigitalDailyController {

    @Autowired
    DigitalDailyService digitalDailyService;

    @Autowired
    DataGeneration dataGeneration;

    /**
     * GET request that searches for a given symbol either for all results or for
     * the past 30 days. Default search is for the past 30 days of ETH (Ethereum).
     *
     * @param symbol enum of DigitalCurrency, of digital currencies supported by
     *               Alpha Vantage. Case sensitive (all caps).
     * @return List of the DigitalDailyCurrency objects (JSON)
     */
    @RequestMapping(value = "/search")
    public List<DigitalCurrencyDaily> searchDigital30(@RequestParam(value = "symbol", defaultValue = "ETH") DigitalCurrency symbol,
                                                           @RequestParam(value = "all", defaultValue = "0") int all) {
        // if all = 1 then return all results for that symbol
        if (Objects.equals(all,1)) {
            return digitalDailyService.searchDigital(symbol);
        } else {
            // else return only the last 30 days
            return digitalDailyService.searchDigital30(symbol);
        }
    }

    /**
     * GET request that calls findMax(). Default search is for the highest price of
     * BTC (Bitcoin) over the past 90 days.
     *
     * @param symbol  enum of DigitalCurrency, of digital currencies supported by
     *                Alpha Vantage. Case sensitive (all caps).
     * @param numDays number of days to compare against
     * @return FindMax object (JSON)
     */
    @RequestMapping("/max")
    public FindMax findMax(@RequestParam(value = "symbol", defaultValue = "BTC") DigitalCurrency symbol,
                           @RequestParam(value = "days", defaultValue = "90") int numDays) {
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
        return dataGeneration.addNew(entry);
    }

    /**
     * GET request that runs a SELECT query (Read)
     *
     * @param id integer to specify object id
     * @return the DigitalCurrencyDaily object with that id
     */
    @RequestMapping(method = RequestMethod.GET, value = "/")
    public DigitalCurrencyDaily getById(@RequestParam(value = "id") int id) {
        return dataGeneration.getByID(id);
    }

    /**
     * PATCH request that runs a UPDATE query (Update)
     *
     * @param entry as DigitalCurrencyDaily object in the body of the PATCH request.
     * @return the DigitalCurrencyDaily object with that id
     */
    @RequestMapping(method = RequestMethod.PATCH, value = "/")
    public DigitalCurrencyDaily updateById(@RequestBody DigitalCurrencyDaily entry) {
        return dataGeneration.updateById(entry);
    }

    /**
     * DELETE request that runs a UPDATE query to set as inactive (Delete)
     *
     * @param id integer to specify object id
     * @return the deactivated DigitalCurrencyDaily object with that id
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/")
    public DigitalCurrencyDaily deleteById(@RequestParam(value = "id") int id) {
        return dataGeneration.deleteByID(id);
    }

    /**
     * DELETE request the breaks the cache.
     */
    @RequestMapping(value = "/cache", method = RequestMethod.DELETE)
    public void clearCache() {
        digitalDailyService.clearCache();
    }

    /**
     * POST request that cycles through all active enums for all dates
     * and persists to the database if not already there.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/persistall")
    public void persistAll() {dataGeneration.persistAll(); }


    /**
     * GET request that verifies that the enums provided by Alpha Vantage
     * are ones that actively have data.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/null")
    public void enumChecker() {
        digitalDailyService.nullChecker();
    }

}
package alpha_vantage.controllers;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.model.internal.Indicator;
import alpha_vantage.services.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired
    AnalysisService analysisService;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public ArrayList<Indicator> getById(@RequestParam(value = "symbol") DigitalCurrency symbol) {
        return analysisService.indicators(symbol);
    }

}
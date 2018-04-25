package alpha_vantage.services;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.mappers.DigitalDailyMapper;
import alpha_vantage.model.internal.DigitalCurrencyDaily;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


// http://commons.apache.org/proper/commons-math/userguide/stat.html#a1.7_Covariance_and_correlation
// https://stats.stackexchange.com/questions/29096/correlation-between-two-time-series
@Service
public class MathService {

    @Autowired
    DigitalDailyMapper digitalDailyMapper;


    static double[] x = {1.0, 2.0, 3.0, 4.0};
    static double[] y = {2.0, 3.0, 6.0, 9.0};

    //TODO get from DB, turn coin daily values into named arrays
    public double[] createArrays(DigitalCurrency symbol) {
        List<DigitalCurrencyDaily> responses = digitalDailyMapper.getAllBySymbol(symbol);
        double[] openArray = new double[responses.size()];
        int i = 0;
        for (DigitalCurrencyDaily day : responses) {
            openArray[i] = day.getOpen();
            i++;
        }
        return openArray;
    }




    //TODO create loop that compares each enum to another. Nested for loop

    //TODO need to match dates, and test out with lag (+-7 days?) to find variance along the time series and ignore lopsided pairings

    //TODO Output into matrix? which sees which coins are most closely correlated with each other.
    public static void main(String[] args) {

        double pearson = new PearsonsCorrelation().correlation(x, y);
        System.out.println("Pearson's correlation = " + pearson);

        double spearman = new SpearmansCorrelation().correlation(x, y);
        System.out.println("Spearman's correlation = " + spearman);

        double kendall = new KendallsCorrelation().correlation(x, y);
        System.out.println("Kendall's correlation = " + kendall);


        MathService obj = new MathService();

        System.out.println(obj.digitalDailyMapper.getByDate("2018-03-01"));

    }
}

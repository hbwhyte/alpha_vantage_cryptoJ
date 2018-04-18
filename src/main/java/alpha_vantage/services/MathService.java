package alpha_vantage.services;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.springframework.stereotype.Service;

@Service
public class MathService {

    static double[] x = {1.0, 2.0, 3.0, 4.0};
    static double[] y = {2.0, 3.0, 6.0, 9.0};

    //TODO get from DB, turn coin daily values into named arrays

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


    }
}

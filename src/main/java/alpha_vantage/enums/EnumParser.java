package alpha_vantage.enums;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class EnumParser {

    public static void main(String[] args) {
        String path = "/Users/hbwhyte/dev_stuff/coding_nomads/bali/projects/alphavantage/src/main/java/alpha_vantage/enums/";
        String physicalCurrency = "physical_currency_list.csv";
        String digitalCurrency = "digital_currency_list.csv";
        ArrayList<String> symbols = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        // Can swap out digitalCurrency and physicalCurrency in path to print either
        try(BufferedReader in = new BufferedReader(new FileReader(path+digitalCurrency))) {
            String headerLine = in.readLine();
            String currentLine;

            while((currentLine = in.readLine()) != null) {
            String[] data = currentLine.split(",");
            symbols.add(data[0]);
            names.add(data[1]);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }

        for (int i = 0; i < symbols.size(); i++) {
            System.out.println(symbols.get(i)+"(\""+names.get(i)+"\"),");
        } {

        }
    }



}

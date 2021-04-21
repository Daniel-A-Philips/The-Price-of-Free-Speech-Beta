package Java;
import java.time.Instant;
import java.util.Date;
import java.io.*;
import java.net.*;

public class FinnHubTest {

    public static void main(String[] args) throws IOError, MalformedURLException, IOException  {
        long ut1 = Instant.now().getEpochSecond();
        System.out.println(ut1);
        URL url = new URL("https://finnhub.io/api/v1/stock/candle?symbol=AAPL&resolution=1&from=1615298999&to=1615302599&token=c1vv82l37jkoemkedus0");
        InputStream in = url.openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        String total = "";
        while((line = reader.readLine()) != null) {
            //System.out.println(line);
            total += line;
        }
        String[] data = total.split("\":");
        for(String a : data){
            System.out.println(a);
        }


    }
}

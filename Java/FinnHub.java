package Java;
import java.time.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class FinnHub {

    public String Interval;
    public String Ticker;
    public String Format = "csv";
    public ArrayList<String> Lines = new ArrayList<String>();
    public LocalDate[] Dates;
    String[] start_end = new String[2];

    FinnHub(String start, String end, String Ticker, String Interval){
        start_end[0] = start;
        start_end[1] = end;
        this.Ticker = Ticker;
        this.Interval = Interval;
        run();
    }

    private void run() {
        try{
            long ut1 = Instant.now().getEpochSecond();
            System.out.println(ut1);
            long[] times = getDates();
            System.out.println(times[0] + ", "+ times[1]);
            URL url = new URL("https://finnhub.io/api/v1/stock/candle?symbol=" + Ticker + "&resolution=" + Interval + "&from=" + times[0] + "&to=" + times[1] + "&format=" + Format + "&token=c1vv82l37jkoemkedus0");
            InputStream in = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = reader.readLine()) != null) {
                //System.out.println(line);
                Lines.add(line);
            }
            writeToCSV();
        } catch(Exception e){ System.out.println(e + "occured on line" + e.getStackTrace() + "in FinnHub.java");}
    }

    private long[] getDates(){
        String a = start_end[0];
        String b = start_end[1];
        String[] tempMonthArray = new String[] {"" ,"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayList<String> months = new ArrayList<String>(Arrays.asList(tempMonthArray));
        if(a.equals(b)){ //If only one month is selected
            int year = Integer.parseInt(a.substring(a.length()-4,a.length()));
            int month = Integer.parseInt(a.substring(0,a.length()-3));
            YearMonth temp = YearMonth.of(year,month);
            LocalDate strtDate = LocalDate.of(year,month,1);
            LocalDate endDate = LocalDate.of(year,month,temp.lengthOfMonth());
            Dates = new LocalDate[]{strtDate,endDate};
            return new long[]{toUnix(strtDate),toUnix(endDate)};
        }
        int year1 = Integer.parseInt(a.substring(a.length()-4,a.length()));
        int year2 = Integer.parseInt(b.substring(b.length()-4,b.length()));
        System.out.println("year1 " + year1);
        System.out.println("Month1 " + a.substring(0,a.length()-5));
        int month1 = months.indexOf(a.substring(0,a.length()-5));
        int month2 = months.indexOf(b.substring(0,b.length()-5));
        YearMonth temp = YearMonth.of(year2,month2);
        LocalDate date1 = LocalDate.of(year1,month1,1);
        System.out.println(date1);
        LocalDate date2 = LocalDate.of(year2,month2,temp.lengthOfMonth());
        System.out.println("Year 2 " + year2);
        Dates = new LocalDate[]{date1,date2};
        return new long[]{toUnix(date1),toUnix(date2)};
    }

    private long toUnix(LocalDate date){ 
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
        long epoch = date.atStartOfDay(zoneId).toEpochSecond();
        return epoch;
    }

    private void writeToCSV() throws IOException{
        String line;
        File tempFile = new File("FinnHubTest.csv");
        FileWriter writer = new FileWriter(tempFile);
        String headers = "time,open,high,low,close,volume";
        Lines.set(0,headers);
        Object[] lines = Lines.toArray();
        for(Object a : lines){
            line = (String)a;
            //System.out.println(line);
            if(!line.equals(headers)){
                long unix = Long.parseLong(line.substring(0,line.indexOf(",")));
                Date date = new Date ();
                date.setTime((long)unix*1000);
                line = date + line.substring(line.indexOf(","),line.length()); //Line used to change UNIX to date and time format on csv file
            }
            writer.write(line + "\n");
        }
        writer.close();

    }
}

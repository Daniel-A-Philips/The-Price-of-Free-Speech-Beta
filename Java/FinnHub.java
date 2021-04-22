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
    public ArrayList<LocalDate[]> Dates = new ArrayList<LocalDate[]>();
    private String[] tempMonthArray = new String[] {"" ,"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    String[] start_end = new String[2];

    FinnHub(String start, String end, String Ticker, String Interval){
        start_end[0] = start;
        start_end[1] = end;
        this.Ticker = Ticker;
        this.Interval = Interval;
        betaRun();
    }

    private void run() {
        /*try{
            long[] times = getDates();
            System.out.println(times[0] + ", "+ times[1]);
            urlConnect(times[0],times[1]);
            writeToCSV();
        } catch(Exception e){ System.out.println(e + " occured on line " + e.getStackTrace()[0].getLineNumber() + " in FinnHub.java");}*/
    }

    private void betaRun(){
        try{
            ArrayList<long[]> times = getDates();
            for(long[] time : times){
                System.out.println(time[0] + ", "+ time[1]);
                urlConnect(time[0],time[1]);
            }
            writeToCSV();
        } catch(Exception e){ System.out.println(e + " occured on line " + e.getStackTrace()[0].getLineNumber() + " in FinnHub.java");}
    }

    private void urlConnect(long start, long end){
        try{
            URL url = new URL("https://finnhub.io/api/v1/stock/candle?symbol=" + Ticker + "&resolution=" + Interval + "&from=" + start + "&to=" + end + "&format=" + Format + "&token=c1vv82l37jkoemkedus0");
            InputStream in = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            ArrayList<String> temp = new ArrayList<String>();
            while((line = reader.readLine()) != null) {
                //System.out.println(line);
                if(Lines.contains(line)) continue;
                temp.add(line);
                Lines.add(line);
            }
            temp.addAll(Lines);
            Lines = temp;
        } catch(Exception e){ System.out.println(e + " occured on line " + e.getStackTrace()[0].getLineNumber() + " in FinnHub.java");}
    }

    private ArrayList<long[]> getDates(){
        String a = start_end[0];
        String b = start_end[1];
        ArrayList<String> months = new ArrayList<String>(Arrays.asList(tempMonthArray));
        ArrayList<long[]> holder = new ArrayList<long[]>();
        //String[] start_end_months = new String[]{a.replaceAll("[0-9]", "").replaceAll(" ", ""), b.replaceAll("[0-9]", "").replaceAll(" ", "")};
        if(a.equals(b)){ //If only one month is selected
            int year = Integer.parseInt(a.substring(a.length()-4,a.length()));
            System.out.println(year);
            int month = Integer.parseInt(a.substring(0,a.length()-3));
            System.out.println("year : " + year + ", month : " + month);
            YearMonth temp = YearMonth.of(year,month);
            LocalDate date1 = LocalDate.of(year,month,1);
            LocalDate date2 = LocalDate.of(year,month,temp.lengthOfMonth());
            Dates.add(new LocalDate[]{date1,date2});
            holder.add(new long[]{toUnix(date1),toUnix(date2)});
            return holder;
        }
        int year1 = Integer.parseInt(a.substring(a.length()-4,a.length()));
        int year2 = Integer.parseInt(b.substring(b.length()-4,b.length()));
        int month1 = months.indexOf(a.substring(0,a.length()-5));
        int month2 = months.indexOf(b.substring(0,b.length()-5));
        int timesToRun = month2-month1;
        for(int i = 0; i < timesToRun; i++){
            System.out.println(month2-1-i);
            LocalDate date1 = LocalDate.of(year1,month2-1-i,1);
            System.out.println(date1);
            YearMonth temp = YearMonth.of(year2,month2-i);
            LocalDate date2 = LocalDate.of(year2,month2-i,temp.lengthOfMonth());
            System.out.println(date2);
            Dates.add(new LocalDate[]{date1,date2});
            holder.add(new long[]{toUnix(date1),toUnix(date2)});
        }
        return holder;
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
                if(line.contains("1609459260")) System.out.println(line);
                line = date + line.substring(line.indexOf(","),line.length()); //Line used to change UNIX to date and time format on csv file
            }
            writer.write(line + "\n");
        }
        writer.close();

    }
}

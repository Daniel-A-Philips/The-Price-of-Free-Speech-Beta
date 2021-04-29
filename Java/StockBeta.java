package Java;
import java.io.*;
import java.util.*;

public class StockBeta {

    protected ArrayList<String[]> RawData = new ArrayList<>();
    protected ArrayList<String[]> DayData = new ArrayList<>();
    protected ArrayList<ArrayList<String[]>> WeekData = new ArrayList<>(); //Day,Data Select
    protected String Interval;
    protected String Ticker;
    protected String Slice;
    protected String StartSlice;
    protected String EndSlice;
    protected boolean forSMVI;
    protected double LatestOpeningPrice;
    protected double SevenDayOpeningPrice;
    protected boolean isRange = false;

    public StockBeta(String Ticker, String Interval, String Slice, boolean forSMVI) throws IOException {
        this.Ticker = Ticker;
        this.Interval = Interval;
        this.StartSlice = Interaction.allSlices.get(0);
        this.EndSlice = Interaction.allSlices.get(6);
        this.forSMVI = forSMVI;
        isRange = true;
        run();
    }

    public StockBeta(String Ticker, String Interval, String StartSlice, String EndSlice, boolean forSMVI) throws IOException {
        this.Ticker = Ticker;
        this.Interval = Interval;
        this.StartSlice = StartSlice;
        this.EndSlice = EndSlice;
        this.forSMVI = forSMVI;
        isRange = true;
        run();
    }

    /**
     * Used to test the Stock class
     */
    public StockBeta() throws IOException{
        Interval = "5";
        Ticker = "IBM";
        Slice = "year1month1";
        run();
    }

    public void run() throws IOException {
        getHistory();
    }

    private ArrayList<String[]> parseToArray(ArrayList<String> a){
        ArrayList<String[]> temp = new ArrayList<String[]>();
        for(String s : a){
            temp.add(s.split(","));
        }
        return temp;
    }

    private void getHistory(){
        FinnHub finnhub = new FinnHub(StartSlice,EndSlice,Ticker,Interval,forSMVI);
        System.out.println("getHistory(): Created FinnHub Instance for " + Ticker);
        RawData = parseToArray(finnhub.RawData);
        System.out.println("getHistory(): Created RawData");
        DayData = getDayData();
        System.out.println("getHistory(): Created DayData");
        WeekData = GetWeekData();
        System.out.println("getHistory(): Created WeekData");
        LatestOpeningPrice = Double.parseDouble(RawData.get(1)[1]);
        System.out.println("getHistory(): Created LatestOpeningPrice");
        SevenDayOpeningPrice = getSevenDayOpeningPrice();
        System.out.println("getHistory(): Created SevenDayOpeningPrice");
    }

    private ArrayList<String[]> getDayData() {
       /*
        for(String[] s : RawData){
            for(String a : s){
                System.out.print(a + ",");
            }
            System.out.print("\n");
        }*/
        ArrayList<String[]> DailyData = new ArrayList<>();
        DailyData.add(RawData.get(0));
        String CurrentDay = RawData.get(1)[0].substring(0, RawData.get(1)[0].indexOf(" "));
        for (int i = 1; i < RawData.size(); i++) {
            if (RawData.get(i)[0].contains(CurrentDay)) DailyData.add(RawData.get(i));
            else break;
        }
        return DailyData;
    }

    private ArrayList<String[]> getDayData(String Date){
        ArrayList<String[]> DayData = new ArrayList<String[]>();
        int start = -1;
        int end = -1;
        boolean searchForEnd = false;
        for(int i = 1; i < RawData.size(); i++){
            if(RawData.get(i)[0].contains(Date)){
                if(!searchForEnd){
                    start = i;
                    searchForEnd = true;
                }
                else end = i;
            }
            else break;
        }
        System.out.println("start: " + start);
        System.out.println("end: " + end);
        for(int f = start; f <= end; f++){
            DayData.add(RawData.get(f));
        }
        return DayData;
    }

    private ArrayList<ArrayList<String[]>> GetWeekData(){
        ArrayList<ArrayList<String[]>> WeekData = new ArrayList<>();
        String[] Week = new String[7];
        Week[0] = RawData.get(1)[0];
        int Index = 1;
        for(int i = 2; i < RawData.size(); i++){
            if(Index > 6) break;
            if(RawData.get(i)[0].length() != 20) continue;
            if(!RawData.get(i)[0].substring(0,RawData.get(i)[0].indexOf(":")-3).equals(Week[Index-1].substring(0,Week[Index-1].indexOf(":")-3))){
                Week[Index] = RawData.get(i)[0];
                Index++;
                System.out.println("\tFound Needed Info");
            }
        }
        System.out.println("\tFinished Loop, Starting to add dates");
        for(String date : Week){
            WeekData.add(getDayData(date));
        }
        System.out.println("\tFinished Adding Dates");
        return WeekData;
    }


    private double getSevenDayOpeningPrice(){ return Double.parseDouble(WeekData.get(6).get(WeekData.get(6).size()-1)[1]);}

    /**
     * A method to find the average price of a stock on a set interval on the most recent day
     *
     * @return the average price of a stock as a double
     */
    public double avgDayPrice() {
        double tp = 0.0;
        for (int i = 1; i < DayData.size(); i++) {
            tp += (Double.parseDouble(RawData.get(i)[1]) + Double.parseDouble(RawData.get(i)[2]) + Double.parseDouble(RawData.get(i)[3]) + Double.parseDouble(RawData.get(i)[4])) / 4;
        }
        double ap = tp / (DayData.size() - 1);
        return ap;
    }

    /**
     * A method to find the average price of a stock on a given day
     *
     * @param data refers to the 2d array containing this format [Time, Open, High, Low, Close, Volume]
     * @return the average price of a given stock as a double
     */
    public double avgDayPrice(ArrayList<String[]> data) {
        double tp = 0.0;
        for (int i = 0; i < data.size(); i++) {
            tp += (Double.parseDouble(data.get(i)[1]) + Double.parseDouble(data.get(i)[2]) + Double.parseDouble(data.get(i)[3]) + Double.parseDouble(data.get(i)[4])) / 4;
        }
        double ap = tp / (data.size() - 1);
        return ap;
    }

    public double avgSevenDayPrice() {
        double total = 0;
        for (ArrayList<String[]> as : WeekData) {
            total += avgDayPrice(as);
        }
        return total / 7;
    }

    public double avgSevenDayPrice(ArrayList<ArrayList<String[]>> weekdata) {
        double total = 0;
        for (ArrayList<String[]> as : weekdata) {
            total += avgDayPrice(as);
        }
        return total / 7;
    }

    public double avgPrice(ArrayList<ArrayList<String[]>> c){
        double total = 0;
        for (ArrayList<String[]> as : c) {
            total += avgDayPrice(as);
        }
        return total / c.size();
    }

    /**
     * A method to find the standard deviation of a given stock for the most recent day
     *
     * @return the standard deviation of the stock as a double
     */
    public double DayDeviation() {
        double top = 0.0;
        for (int i = 1; i < DayData.size(); i++) {
            top += Math.pow(((Float.parseFloat(DayData.get(i)[1]) + Float.parseFloat(DayData.get(i)[2]) + Float.parseFloat(DayData.get(i)[3]) + Float.parseFloat(DayData.get(i)[4])) / 4.0) - avgDayPrice(), 2.0);
        }
        double bottom = DayData.size() - 2;
        double total = top / bottom;
        return Math.sqrt(total);
    }

    public double sevenDayDeviation(){
        double top = 0.0;
        for(int i = 0; i < 7; i++){
            for(ArrayList<String[]> as : WeekData){
                top += Math.pow( avgDayPrice(as)-avgSevenDayPrice(),2);
            }
        }
        double Bottom = 0.0;
        for(int i = 0; i < 7; i++){
            Bottom += WeekData.get(i).size()-1;
        }
        Bottom--;
        return Math.sqrt(top/Bottom);
    }

    public double deviation(ArrayList<String[]> a){
        //Check if the arraylist has not been formatted correctly, meaning it still has headers
        try{
            double d = Double.parseDouble(a.get(0)[0]);
        }catch(Exception e){ a.remove(0);}
        ////////////////////////////////////////////////////////////////////////////////////////
        double top = 0.0;
        double avg = avgDayPrice(a);
        double tmavg = 0.0;
        for(String[] sa : a){
            tmavg += Double.parseDouble(sa[2])+Double.parseDouble(sa[3]);
        }
        tmavg = tmavg / (2*a.size());
        for(String[] sa: a){
            top += (Double.parseDouble(sa[2])+Double.parseDouble(sa[3]))-tmavg;
        }
        double bottom = a.size()-1;
        return Math.sqrt(top/bottom);
    }

    public double getVariation(ArrayList<String[]> data){
        return Math.pow(deviation(data),2);
    }

    public double currentDayDeviationPercentage() {
        return DayDeviation()/avgDayPrice();
    }

    public double sevenDayDeviationPercentage(){
        return sevenDayDeviation()/avgSevenDayPrice();
    }

}

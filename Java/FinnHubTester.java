package Java;

public class FinnHubTester {
    public static void main(String[] args) {
        FinnHub finn = new FinnHub("March 2020", "March 2021", "AAPL", "15",false);
        for(double a : finn.High){
            System.out.println(a);
        }
    }
}

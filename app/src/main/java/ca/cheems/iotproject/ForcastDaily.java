package ca.cheems.iotproject;

public class ForcastDaily {
   int avg;
   String day;

    public ForcastDaily(int avg, String day) {
        this.avg = avg;
        this.day = day;
    }

    public int getAvg() {
        return avg;
    }

    public String getDay() {
        return day;
    }
}

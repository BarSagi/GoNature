package Common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UsageReportData implements Serializable {

    private int dayOfWeek;
    private double averageCapacity;

    public UsageReportData(int dayOfWeek, double averageCapacity) {
        this.dayOfWeek = dayOfWeek;
        this.averageCapacity = averageCapacity;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public double getAverageCapacity() {
        return averageCapacity;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setAverageCapacity(double averageCapacity) {
        this.averageCapacity = averageCapacity;
    }
}
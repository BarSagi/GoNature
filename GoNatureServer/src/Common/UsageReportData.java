package Common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UsageReportData implements Serializable {

    private int day;
    private int peakOccupancy;
    private boolean full;

    public UsageReportData(int day, int peakOccupancy, boolean full) {
        this.day = day;
        this.peakOccupancy = peakOccupancy;
        this.full = full;
    }

    public int getDay() {
        return day;
    }

    public int getPeakOccupancy() {
        return peakOccupancy;
    }

    public boolean isFull() {
        return full;
    }
    public boolean isNotFull() {
        return !full;
    }
}
package Common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UsageReportData implements Serializable {

    private int month;
    private double percentUnderCapacity;

    public UsageReportData(int month, double percentUnderCapacity) {
        this.month = month;
        this.percentUnderCapacity = percentUnderCapacity;
    }

    public int getMonth() {
        return month;
    }

    public double getPercentUnderCapacity() {
        return percentUnderCapacity;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setPercentUnderCapacity(double percentUnderCapacity) {
        this.percentUnderCapacity = percentUnderCapacity;
    }
}
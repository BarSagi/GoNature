package Common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CancellationReportData implements Serializable {

    private int dayOfMonth;
    private double value;

    public CancellationReportData(int dayOfMonth, double value) {
        this.dayOfMonth = dayOfMonth;
        this.value = value;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public double getValue() {
        return value;
    }
}
package Common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CancellationReportData implements Serializable {

    private int dayOfMonth;
    private int cancellations;
    private int noShows;

    public CancellationReportData(int dayOfMonth, int cancellations, int noShows) {
        this.dayOfMonth = dayOfMonth;
        this.cancellations = cancellations;
        this.noShows = noShows;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getCancellations() {
        return cancellations;
    }

    public void setCancellations(int cancellations) {
        this.cancellations = cancellations;
    }

    public int getNoShows() {
        return noShows;
    }

    public void setNoShows(int noShows) {
        this.noShows = noShows;
    }

    public double getValue() {
        return cancellations + noShows;
    }
}
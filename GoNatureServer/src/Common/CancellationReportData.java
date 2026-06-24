package Common;

import java.io.Serializable;

/**
 * Represents a single data point in a cancellation report.
 * Maps a specific day of the month to a statistical value (e.g., number of cancellations).
 * This class implements {@link Serializable} so it can be transmitted over the network
 * between the client and the server.
 */
@SuppressWarnings("serial")
public class CancellationReportData implements Serializable {
	
    /**
     * The day of the month (e.g., 1-31) for this data point.
     */
    private int dayOfMonth;
    
    /**
     * The calculated cancellation value (e.g., amount or average) for this specific day.
     */
    private double value;

    /**
     * Constructs a new CancellationReportData instance.
     *
     * @param dayOfMonth The day of the month for this data point.
     * @param value      The cancellation value associated with this day.
     */
    public CancellationReportData(int dayOfMonth, double value) {
        this.dayOfMonth = dayOfMonth;
        this.value = value;
    }

    /**
     * Retrieves the day of the month for this report entry.
     *
     * @return The day of the month.
     */
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * Retrieves the cancellation value for this report entry.
     *
     * @return The cancellation value.
     */
    public double getValue() {
        return value;
    }
}
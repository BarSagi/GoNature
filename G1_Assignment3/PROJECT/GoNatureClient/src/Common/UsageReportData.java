package Common;

import java.io.Serializable;

/**
 * Represents a single data point in a park usage report.
 * This class encapsulates information about the park's occupancy for a specific day,
 * including its peak occupancy level and whether it reached full capacity.
 * Implements {@link Serializable} to allow transmission over the network.
 */
public class UsageReportData implements Serializable {

    /**
     * A unique identifier for serializing this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The specific day of the month this data represents (e.g., 1-31).
     */
    private int day;
    
    /**
     * The highest number of visitors present in the park simultaneously on this day.
     */
    private int peakOccupancy;
    
    /**
     * A flag indicating whether the park reached its maximum capacity at any point during this day.
     * True if full, false otherwise.
     */
    private boolean full;

    /**
     * Constructs a new UsageReportData instance.
     *
     * @param day           The day of the month.
     * @param peakOccupancy The peak number of visitors on this day.
     * @param full          A boolean indicating if the park reached maximum capacity.
     */
    public UsageReportData(int day, int peakOccupancy, boolean full) {
        this.day = day;
        this.peakOccupancy = peakOccupancy;
        this.full = full;
    }

    /**
     * Retrieves the day of the month for this report entry.
     *
     * @return The day of the month.
     */
    public int getDay() {
        return day;
    }

    /**
     * Retrieves the peak occupancy recorded for this day.
     *
     * @return The maximum number of simultaneous visitors.
     */
    public int getPeakOccupancy() {
        return peakOccupancy;
    }

    /**
     * Checks if the park reached its full capacity on this day.
     *
     * @return true if the park was full, false otherwise.
     */
    public boolean isFull() {
        return full;
    }
    
    /**
     * Checks if the park did not reach its full capacity on this day.
     *
     * @return true if the park was not full, false otherwise.
     */
    public boolean isNotFull() {
        return !full;
    }
}
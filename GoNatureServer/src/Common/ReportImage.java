package Common;

import java.io.Serializable;

/**
 * Represents a generated report saved as an image file. This entity class
 * encapsulates the report's metadata (such as its type, associated park, and
 * date) along with the actual image data stored as a byte array. Implements
 * {@link Serializable} to allow the transfer of report images over the network.
 */
public class ReportImage implements Serializable {

	/**
	 * A unique identifier for serializing this class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The unique identification number of the report in the database.
	 */
	private int reportId;

	/**
	 * The type of the report (e.g., "VisitsReport", "CancellationsReport").
	 */
	private String reportType;

	/**
	 * The name of the park this report belongs to.
	 */
	private String parkName;

	/**
	 * The month the report covers (1-12).
	 */
	private int month;

	/**
	 * The year the report covers.
	 */
	private int year;

	/**
	 * A timestamp string indicating exactly when the report was generated and
	 * saved.
	 */
	private String createdAt;

	/**
	 * The binary data of the report image.
	 */
	private byte[] image;

	/**
	 * Constructs a new ReportImage intended for saving to the database. This
	 * constructor omits the reportId and createdAt fields, as they are typically
	 * generated automatically by the database upon insertion.
	 *
	 * @param reportType The type of the report.
	 * @param parkName   The name of the park.
	 * @param month      The month the report covers.
	 * @param year       The year the report covers.
	 * @param image      The byte array containing the image data.
	 */
	public ReportImage(String reportType, String parkName, int month, int year, byte[] image) {
		this.reportType = reportType;
		this.parkName = parkName;
		this.month = month;
		this.year = year;
		this.image = image;
	}

	/**
	 * Constructs a ReportImage with all fields populated. This constructor is
	 * typically used when fetching an existing report from the database.
	 *
	 * @param reportId   The unique ID of the report.
	 * @param reportType The type of the report.
	 * @param parkName   The name of the park.
	 * @param month      The month the report covers.
	 * @param year       The year the report covers.
	 * @param createdAt  The timestamp of when the report was created.
	 * @param image      The byte array containing the image data.
	 */
	public ReportImage(int reportId, String reportType, String parkName, int month, int year, String createdAt,
			byte[] image) {
		this.reportId = reportId;
		this.reportType = reportType;
		this.parkName = parkName;
		this.month = month;
		this.year = year;
		this.createdAt = createdAt;
		this.image = image;
	}

	/**
	 * Retrieves the unique ID of the report.
	 *
	 * @return The report ID.
	 */
	public int getReportId() {
		return reportId;
	}

	/**
	 * Retrieves the type of the report.
	 *
	 * @return The report type.
	 */
	public String getReportType() {
		return reportType;
	}

	/**
	 * Retrieves the name of the park associated with this report.
	 *
	 * @return The park name.
	 */
	public String getParkName() {
		return parkName;
	}

	/**
	 * Retrieves the month the report covers.
	 *
	 * @return The month.
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * Retrieves the year the report covers.
	 *
	 * @return The year.
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Retrieves the creation timestamp of the report.
	 *
	 * @return The creation timestamp string.
	 */
	public String getCreatedAt() {
		return createdAt;
	}

	/**
	 * Retrieves the raw image data of the report.
	 *
	 * @return A byte array representing the image.
	 */
	public byte[] getImage() {
		return image;
	}
}
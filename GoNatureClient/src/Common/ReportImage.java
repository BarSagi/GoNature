package Common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ReportImage implements Serializable {

	private int reportId;
	private String reportType;
	private String parkName;
	private int month;
	private int year;
	private String createdAt;
	private byte[] image;

	// constructor for saving a report
	public ReportImage(String reportType, String parkName, int month, int year, byte[] image) {
		this.reportType = reportType;
		this.parkName = parkName;
		this.month = month;
		this.year = year;
		this.image = image;
	}

	// constructor for fetching a report
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

	// getters
	public int getReportId() {
		return reportId;
	}

	public String getReportType() {
		return reportType;
	}

	public String getParkName() {
		return parkName;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public byte[] getImage() {
		return image;
	}
}
package Reports;

import Database.DBController;
import Common.Visit;
import Common.VisitReportData;

import java.util.ArrayList;

public class ReportService {

	private DBController db;

	public ReportService(DBController db) {
		this.db = db;
	}

	public ArrayList<Visit> getVisitReport(int parkId, int month, int year) {
		return db.getVisitReport(parkId, month, year);
	}

	/*
	 * public ArrayList<Order> getCancellationReport(int parkId, int month, int
	 * year) { return db.getCancellationReport(parkId, month, year); }
	 */

	public VisitReportData generateVisitReport(int parkId, int month, int year) {

		ArrayList<Visit> visits;

		try {
			visits = db.getVisitReport(parkId, month, year);
		} catch (Exception e) {
			e.printStackTrace();
			return new VisitReportData(0, 0);
		}

		int individualVisitors = 0;
		int groupVisitors = 0;

		for (Visit v : visits) {

			String type = v.getOrderType();

			if (type == null)
				continue;

			if (type.equals("RegularGroup")) {
				individualVisitors += v.getActualVisitorCount();
			}

			else if (type.equals("OrganizedGroup")) {
				groupVisitors += v.getActualVisitorCount();
			}
		}

		return new VisitReportData(individualVisitors, groupVisitors);
	}
}
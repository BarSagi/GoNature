package Reports;

import Database.DBController;
import Common.Order;
import Common.VisitReportData;

import java.util.ArrayList;

public class ReportService {

    private DBController db;

    public ReportService(DBController db) {
        this.db = db;
    }

    public ArrayList<Order> getVisitReport(int parkId, int month, int year) {
        return db.getVisitReport(parkId, month, year);
    }

    public ArrayList<Order> getCancellationReport(int parkId, int month, int year) {
        return db.getCancellationReport(parkId, month, year);
    }
    
    public VisitReportData generateVisitReport(int parkId, int month, int year) {

        ArrayList<Order> orders;

        try {
            orders = db.getVisitReport(parkId, month, year);
        } catch (Exception e) {
            e.printStackTrace();
            return new VisitReportData(0, 0);
        }

        int individualVisitors = 0;
        int groupVisitors = 0;

        for (Order o : orders) {

            if (o.getOrderStatus().equals("Canceled")) {
                continue; // do not count cancelled visits
            }

            String type = o.getOrderType();

            if (type.equals("Individual")) {
                individualVisitors += o.getVisitorCount();
            }

            else if (type.equals("SmallGroup") || type.equals("OrganizedGroup")) {
                groupVisitors += o.getVisitorCount();
            }
        }

        return new VisitReportData(individualVisitors, groupVisitors);
    }
}
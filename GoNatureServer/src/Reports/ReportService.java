package Reports;

import Database.DBController;
import Common.Order;

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
}
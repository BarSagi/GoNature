package Common;

import java.io.Serializable;
import java.sql.Timestamp;

@SuppressWarnings("serial")
public class Visit implements Serializable {

	private int visitId;
	private int parkId;
	private int orderId;
	private String visitorId;
	private int actualVisitorCount;
	private Timestamp entryTime;
	private Timestamp exitTime;
	private String orderType;

	public Visit(int visitId, int parkId, int orderId, String visitorId, int actualVisitorCount, Timestamp entryTime,
			Timestamp exitTime, String orderType) {

		this.visitId = visitId;
		this.parkId = parkId;
		this.orderId = orderId;
		this.visitorId = visitorId;
		this.actualVisitorCount = actualVisitorCount;
		this.entryTime = entryTime;
		this.exitTime = exitTime;
		this.orderType = orderType;
	}

	// Getters
	public int getVisitId() {
		return visitId;
	}

	public int getParkId() {
		return parkId;
	}

	public int getOrderId() {
		return orderId;
	}

	public String getVisitorId() {
		return visitorId;
	}

	public int getActualVisitorCount() {
		return actualVisitorCount;
	}

	public Timestamp getEntryTime() {
		return entryTime;
	}

	public Timestamp getExitTime() {
		return exitTime;
	}

	public String getOrderType() {
		return orderType;
	}

	@Override
	public String toString() {
		return "Visit{" + "visitId=" + visitId + ", parkId=" + parkId + ", orderId=" + orderId + ", visitorId='"
				+ visitorId + '\'' + ", actualVisitorCount=" + actualVisitorCount + ", entryTime=" + entryTime
				+ ", exitTime=" + exitTime + ", orderType='" + orderType + '\'' + '}';
	}
}
package Common;

import java.io.Serializable;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;

// this class will handle order logic
public class Order implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int orderId;
	private int parkId;
	private String visitorId;
	private Date visitDate;
	private Time visitTime;
	private int visitorCount;
	private String email;
	private String orderType;
	private String orderStatus;
	private Timestamp holdUntil;

	public Order(int orderId, int parkId, String visitorId, Date visitDate, Time visitTime, int visitorCount,
			String email, String orderType, String orderStatus) {

		this.orderId = orderId;
		this.parkId = parkId;
		this.visitorId = visitorId;
		this.visitDate = visitDate;
		this.visitTime = visitTime;
		this.visitorCount = visitorCount;
		this.email = email;
		this.orderType = orderType;
		this.orderStatus = orderStatus;
	}
	
	public Order(int orderId, int parkId, String visitorId, Date visitDate, Time visitTime, int visitorCount,
			String email, String orderType, String orderStatus, Timestamp holdUntil) {
		this.orderId = orderId;
		this.parkId = parkId;
		this.visitorId = visitorId;
		this.visitDate = visitDate;
		this.visitTime = visitTime;
		this.visitorCount = visitorCount;
		this.email = email;
		this.orderType = orderType;
		this.orderStatus = orderStatus;
		this.holdUntil = holdUntil;
	}

	public Order() { // empty constructor
	}

	@Override
	public String toString() {

		return "Order [ID=" + orderId + ", ParkID=" + parkId + ", VisitorID='" + visitorId + '\'' + ", Date="
				+ visitDate + ", Time=" + visitTime + ", Guests=" + visitorCount + ", Email='" + email + '\''
				+ ", Type='" + orderType + '\'' + ", Status='" + orderStatus + '\'' + ", HoldUntil=" + holdUntil + "]";
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getParkId() {
		return parkId;
	}

	public void setParkId(int parkId) {
		this.parkId = parkId;
	}

	public String getVisitorId() {
		return visitorId;
	}

	public void setVisitorId(String visitorId) {
		this.visitorId = visitorId;
	}

	public Date getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	public Time getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Time visitTime) {
		this.visitTime = visitTime;
	}

	public int getVisitorCount() {
		return visitorCount;
	}

	public void setVisitorCount(int visitorCount) {
		this.visitorCount = visitorCount;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	public Timestamp getHoldUntil() {
		return holdUntil;
	}

	public void setHoldUntil(Timestamp holdUntil) {
		this.holdUntil = holdUntil;
	}
}
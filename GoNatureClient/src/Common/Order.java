package Common;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Represents a visitor's order for a park visit in the GoNature system.
 * This entity class holds all the relevant details of an order, including
 * visitor information, timing, status, and automated reminder timestamps.
 * It implements {@link Serializable} so it can be passed between the client and server.
 */
// this class will handle order logic
public class Order implements Serializable {

	/**
	 * A unique identifier for serializing this class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The unique identification number for this order.
	 */
	private int orderId;
	
	/**
	 * The unique identification number of the park being visited.
	 */
	private int parkId;
	
	/**
	 * The ID string of the visitor who placed the order.
	 */
	private String visitorId;
	
	/**
	 * The scheduled date for the park visit.
	 */
	private Date visitDate;
	
	/**
	 * The scheduled time for the park visit.
	 */
	private Time visitTime;
	
	/**
	 * The total number of visitors included in this order.
	 */
	private int visitorCount;
	
	/**
	 * The contact email address of the visitor.
	 */
	private String email;
	
	/**
	 * The type of the order (e.g., Solo, Family, Group).
	 */
	private String orderType;
	
	/**
	 * The current status of the order (e.g., Pending, Approved, Cancelled).
	 */
	private String orderStatus;
	
	/**
	 * The timestamp until which a spot is held for the visitor (used in waiting lists).
	 */
	private Timestamp holdUntil;
	
	/**
	 * The timestamp representing the deadline for the visitor to confirm their reminder.
	 */
	private Timestamp reminderUntil;
	
	/**
	 * The unique alphanumeric string acting as the QR code for park entry.
	 */
	private String QRCode;

	/**
	 * Constructs an Order without timestamps.
	 *
	 * @param orderId      The unique order ID.
	 * @param parkId       The ID of the park.
	 * @param visitorId    The ID of the visitor.
	 * @param visitDate    The date of the visit.
	 * @param visitTime    The time of the visit.
	 * @param visitorCount The number of visitors.
	 * @param email        The email of the visitor.
	 * @param orderType    The type of the order.
	 * @param orderStatus  The current status of the order.
	 */
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

	/**
	 * Constructs an Order including a hold limit timestamp.
	 *
	 * @param orderId      The unique order ID.
	 * @param parkId       The ID of the park.
	 * @param visitorId    The ID of the visitor.
	 * @param visitDate    The date of the visit.
	 * @param visitTime    The time of the visit.
	 * @param visitorCount The number of visitors.
	 * @param email        The email of the visitor.
	 * @param orderType    The type of the order.
	 * @param orderStatus  The current status of the order.
	 * @param holdUntil    The timestamp indicating how long the spot is held.
	 */
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

	/**
	 * Constructs a fully detailed Order including both hold and reminder timestamps.
	 *
	 * @param orderId       The unique order ID.
	 * @param parkId        The ID of the park.
	 * @param visitorId     The ID of the visitor.
	 * @param visitDate     The date of the visit.
	 * @param visitTime     The time of the visit.
	 * @param visitorCount  The number of visitors.
	 * @param email         The email of the visitor.
	 * @param orderType     The type of the order.
	 * @param orderStatus   The current status of the order.
	 * @param holdUntil     The timestamp indicating how long the spot is held.
	 * @param reminderUntil The timestamp indicating the confirmation deadline.
	 */
	public Order(int orderId, int parkId, String visitorId, Date visitDate, Time visitTime, int visitorCount,
			String email, String orderType, String orderStatus, Timestamp holdUntil, Timestamp reminderUntil) {
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
		this.reminderUntil = reminderUntil;
	}

	/**
	 * Default empty constructor.
	 */
	public Order() { // empty constructor
	}

	/**
	 * Returns a string representation of the order.
	 *
	 * @return A formatted string detailing the order's core properties.
	 */
	@Override
	public String toString() {

		return "Order [ID=" + orderId + ", ParkID=" + parkId + ", VisitorID='" + visitorId + '\'' + ", Date="
				+ visitDate + ", Time=" + visitTime + ", Guests=" + visitorCount + ", Email='" + email + '\''
				+ ", Type='" + orderType + '\'' + ", Status='" + orderStatus + '\'' + ", HoldUntil=" + holdUntil + "]";
	}

	/**
	 * @return The order ID.
	 */
	public int getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId The order ID to set.
	 */
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return The park ID.
	 */
	public int getParkId() {
		return parkId;
	}

	/**
	 * @param parkId The park ID to set.
	 */
	public void setParkId(int parkId) {
		this.parkId = parkId;
	}

	/**
	 * @return The visitor ID.
	 */
	public String getVisitorId() {
		return visitorId;
	}

	/**
	 * @param visitorId The visitor ID to set.
	 */
	public void setVisitorId(String visitorId) {
		this.visitorId = visitorId;
	}

	/**
	 * @return The visit date.
	 */
	public Date getVisitDate() {
		return visitDate;
	}

	/**
	 * @param visitDate The visit date to set.
	 */
	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	/**
	 * @return The visit time.
	 */
	public Time getVisitTime() {
		return visitTime;
	}

	/**
	 * @param visitTime The visit time to set.
	 */
	public void setVisitTime(Time visitTime) {
		this.visitTime = visitTime;
	}

	/**
	 * @return The number of visitors.
	 */
	public int getVisitorCount() {
		return visitorCount;
	}

	/**
	 * @param visitorCount The visitor count to set.
	 */
	public void setVisitorCount(int visitorCount) {
		this.visitorCount = visitorCount;
	}

	/**
	 * @return The visitor's email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return The order type.
	 */
	public String getOrderType() {
		return orderType;
	}

	/**
	 * @param orderType The order type to set.
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	/**
	 * @return The order status.
	 */
	public String getOrderStatus() {
		return orderStatus;
	}

	/**
	 * @param orderStatus The order status to set.
	 */
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	/**
	 * @return The timestamp indicating how long the spot is held.
	 */
	public Timestamp getHoldUntil() {
		return holdUntil;
	}

	/**
	 * @param holdUntil The hold limit timestamp to set.
	 */
	public void setHoldUntil(Timestamp holdUntil) {
		this.holdUntil = holdUntil;
	}

	/**
	 * @return The timestamp indicating the confirmation deadline.
	 */
	public Timestamp getReminderUntil() {
		return reminderUntil;
	}

	/**
	 * @param reminderUntil The confirmation deadline timestamp to set.
	 */
	public void setReminderUntil(Timestamp reminderUntil) {
		this.reminderUntil = reminderUntil;
	}

	/**
	 * @return The unique QR code string.
	 */
	public String getQrCode() {
		return QRCode;
	}

	/**
	 * @param qrCode The unique QR code string to set.
	 */
	public void setQrCode(String qrCode) {
		this.QRCode = qrCode;
	}
}
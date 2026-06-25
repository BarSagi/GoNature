package Common;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a record of a visitor's visit to a park in the GoNature system.
 * This entity tracks the timing of a visit and links it to an order and
 * visitor. Implements {@link Serializable} to allow transmission over the
 * network.
 */
public class Visit implements Serializable {

	/**
	 * A unique identifier for serializing this class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The unique identification number for this specific visit record.
	 */
	private int visitId;

	/**
	 * The unique identification number of the park visited.
	 */
	private int parkId;

	/**
	 * The unique identification number of the order associated with this visit.
	 */
	private int orderId;

	/**
	 * The unique ID of the visitor.
	 */
	private String visitorId;

	/**
	 * The actual number of visitors who arrived for this visit.
	 */
	private int actualVisitorCount;

	/**
	 * The timestamp recorded when the visitor entered the park.
	 */
	private Timestamp entryTime;

	/**
	 * The timestamp recorded when the visitor exited the park.
	 */
	private Timestamp exitTime;

	/**
	 * The type of the order related to this visit (e.g., "REGULAR", "GUIDE").
	 */
	private String orderType;

	/**
	 * Constructs a new Visit record.
	 *
	 * @param visitId            The unique ID of the visit.
	 * @param parkId             The ID of the park.
	 * @param orderId            The ID of the associated order.
	 * @param visitorId          The ID of the visitor.
	 * @param actualVisitorCount The number of people who actually arrived.
	 * @param entryTime          The time of entry.
	 * @param exitTime           The time of exit.
	 * @param orderType          The type of the order.
	 */
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

	/**
	 * @return The unique ID of the visit.
	 */
	public int getVisitId() {
		return visitId;
	}

	/**
	 * @return The park ID.
	 */
	public int getParkId() {
		return parkId;
	}

	/**
	 * @return The associated order ID.
	 */
	public int getOrderId() {
		return orderId;
	}

	/**
	 * @return The visitor ID.
	 */
	public String getVisitorId() {
		return visitorId;
	}

	/**
	 * @return The actual number of visitors who arrived.
	 */
	public int getActualVisitorCount() {
		return actualVisitorCount;
	}

	/**
	 * @return The time of park entry.
	 */
	public Timestamp getEntryTime() {
		return entryTime;
	}

	/**
	 * @return The time of park exit.
	 */
	public Timestamp getExitTime() {
		return exitTime;
	}

	/**
	 * @return The order type of this visit.
	 */
	public String getOrderType() {
		return orderType;
	}

	/**
	 * Returns a string representation of the visit.
	 *
	 * @return A formatted string detailing the visit properties.
	 */
	@Override
	public String toString() {
		return "Visit{" + "visitId=" + visitId + ", parkId=" + parkId + ", orderId=" + orderId + ", visitorId='"
				+ visitorId + '\'' + ", actualVisitorCount=" + actualVisitorCount + ", entryTime=" + entryTime
				+ ", exitTime=" + exitTime + ", orderType='" + orderType + '\'' + '}';
	}
}
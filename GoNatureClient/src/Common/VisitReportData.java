package Common;

import java.io.Serializable;

/**
 * Represents statistical data for a visit report. This class captures visitor
 * distribution data, specifically separating individual visitors from group
 * visitors for report generation. Implements {@link Serializable} to allow
 * transmission over the network.
 */
public class VisitReportData implements Serializable {

	/**
	 * A unique identifier for serializing this class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The number of visitors who arrived as individuals.
	 */
	private int individualVisitors;

	/**
	 * The number of visitors who arrived as part of a group.
	 */
	private int groupVisitors;

	/**
	 * Constructs a new VisitReportData instance.
	 *
	 * @param individualVisitors The count of individual visitors.
	 * @param groupVisitors      The count of group visitors.
	 */
	public VisitReportData(int individualVisitors, int groupVisitors) {
		this.individualVisitors = individualVisitors;
		this.groupVisitors = groupVisitors;
	}

	/**
	 * Retrieves the count of individual visitors.
	 *
	 * @return The number of individual visitors.
	 */
	public int getIndividualVisitors() {
		return individualVisitors;
	}

	/**
	 * Retrieves the count of group visitors.
	 *
	 * @return The number of group visitors.
	 */
	public int getGroupVisitors() {
		return groupVisitors;
	}
}
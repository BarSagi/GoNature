package Common;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a record of a visitor's stay within the park, including their
 * entry and exit times and the count of visitors. This class is serializable to
 * support transmission over the network.
 */
@SuppressWarnings("serial")
public class VisitRecord implements Serializable {

	/** The timestamp of the visitor's entry into the park. */
	public LocalDateTime entry;

	/** The timestamp of the visitor's exit from the park. */
	public LocalDateTime exit;

	/** The number of visitors associated with this record. */
	public int count;

	/**
	 * Constructs a new VisitRecord with specified timing and visitor count.
	 *
	 * @param entry The entry timestamp.
	 * @param exit  The exit timestamp.
	 * @param count The number of visitors.
	 */
	public VisitRecord(LocalDateTime entry, LocalDateTime exit, int count) {
		this.entry = entry;
		this.exit = exit;
		this.count = count;
	}
}
package Entity;

/**
 * Represents a visitor in the GoNature system. This entity holds personal
 * contact information and membership details for a visitor, used for managing
 * registrations and order associations.
 */
public class Visitor {

	/**
	 * The unique identification number of the visitor (e.g., ID card number).
	 */
	private String visitorId;

	/**
	 * The visitor's first name.
	 */
	private String firstName;

	/**
	 * The visitor's last name.
	 */
	private String lastName;

	/**
	 * The visitor's contact phone number.
	 */
	private String phone;

	/**
	 * The visitor's email address.
	 */
	private String email;

	/**
	 * The classification of the visitor (e.g., Casual, Subscriber, Guide).
	 */
	private String visitorType;

	/**
	 * The subscription number if the visitor is a subscriber, otherwise 0.
	 */
	private int subNumber;

	/**
	 * The total number of family members included under this visitor's
	 * registration.
	 */
	private int familyMembersNum;

	/**
	 * Constructs a new Visitor instance.
	 *
	 * @param visitorId     The unique ID of the visitor.
	 * @param firstName     The visitor's first name.
	 * @param lastName      The visitor's last name.
	 * @param phone         The visitor's phone number.
	 * @param email         The visitor's email address.
	 * @param visitorType   The type of the visitor.
	 * @param subNumber     The subscription number.
	 * @param familyMembers The number of family members.
	 */
	public Visitor(String visitorId, String firstName, String lastName, String phone, String email, String visitorType,
			int subNumber, int familyMembers) {
		this.setVisitorId(visitorId);
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setPhone(phone);
		this.setEmail(email);
		this.setVisitorType(visitorType);
		this.setSubNumber(subNumber);
		this.setFamilyMembersNum(familyMembers);
	}

	/**
	 * @return The visitor's ID.
	 */
	public String getVisitorId() {
		return visitorId;
	}

	/**
	 * @param visitorId The visitor's ID to set.
	 */
	public void setVisitorId(String visitorId) {
		this.visitorId = visitorId;
	}

	/**
	 * @return The visitor's first name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName The visitor's first name to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return The visitor's last name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName The visitor's last name to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return The visitor's phone number.
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone The visitor's phone number to set.
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return The visitor's email address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email The visitor's email address to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return The visitor's type.
	 */
	public String getVisitorType() {
		return visitorType;
	}

	/**
	 * @param visitorType The visitor's type to set.
	 */
	public void setVisitorType(String visitorType) {
		this.visitorType = visitorType;
	}

	/**
	 * @return The subscription number.
	 */
	public int getSubNumber() {
		return subNumber;
	}

	/**
	 * @param subNumber The subscription number to set.
	 */
	public void setSubNumber(int subNumber) {
		this.subNumber = subNumber;
	}

	/**
	 * @return The number of family members.
	 */
	public int getFamilyMembersNum() {
		return familyMembersNum;
	}

	/**
	 * @param familyMembersNum The number of family members to set.
	 */
	public void setFamilyMembersNum(int familyMembersNum) {
		this.familyMembersNum = familyMembersNum;
	}
}
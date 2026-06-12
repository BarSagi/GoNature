package Entity;

public class Visitor {
	private String visitorId;
	private String firstName;
	private String lastName;
	private String phone;
	private String email;
	private String visitorType;
	private int subNumber;
	private int familyMembersNum;

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

	public String getVisitorId() {
		return visitorId;
	}

	public void setVisitorId(String visitorId) {
		this.visitorId = visitorId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getVisitorType() {
		return visitorType;
	}

	public void setVisitorType(String visitorType) {
		this.visitorType = visitorType;
	}

	public int getSubNumber() {
		return subNumber;
	}

	public void setSubNumber(int subNumber) {
		this.subNumber = subNumber;
	}

	public int getFamilyMembersNum() {
		return familyMembersNum;
	}

	public void setFamilyMembersNum(int familyMembersNum) {
		this.familyMembersNum = familyMembersNum;
	}
}
package Entity;

public class Employee {
	private String employeeId;
	private String firstName;
	private String lastName;
	private String email;
	private String username;
	private String role;
	private String affiliation;

	public Employee(String employeeId, String firstName, String lastName, String email, String username, String role,
			String affiliation) {
		this.employeeId = employeeId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.username = username;
		this.role = role;
		this.affiliation = affiliation;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public String getRole() {
		return role;
	}

	public String getAffiliation() {
		return affiliation;
	}
}
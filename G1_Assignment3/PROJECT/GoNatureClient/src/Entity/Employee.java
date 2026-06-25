package Entity;

/**
 * Represents an employee in the GoNature system. This entity holds the personal
 * and professional details of an employee, including their role and affiliation
 * within the organization.
 */
public class Employee {

	/**
	 * The unique identification number for this employee.
	 */
	private String employeeId;

	/**
	 * The employee's first name.
	 */
	private String firstName;

	/**
	 * The employee's last name.
	 */
	private String lastName;

	/**
	 * The employee's email address.
	 */
	private String email;

	/**
	 * The username used by the employee to log into the system.
	 */
	private String username;

	/**
	 * The job role/position of the employee (e.g., Park Manager, Service
	 * Representative).
	 */
	private String role;

	/**
	 * The organization or park the employee is affiliated with.
	 */
	private String affiliation;

	/**
	 * Constructs a new Employee instance.
	 *
	 * @param employeeId  The unique employee ID.
	 * @param firstName   The employee's first name.
	 * @param lastName    The employee's last name.
	 * @param email       The employee's email address.
	 * @param username    The employee's system username.
	 * @param role        The job role.
	 * @param affiliation The organizational affiliation.
	 */
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

	/**
	 * @return The employee's ID.
	 */
	public String getEmployeeId() {
		return employeeId;
	}

	/**
	 * @return The employee's first name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @return The employee's last name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @return The employee's email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return The employee's username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return The employee's role.
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @return The employee's affiliation.
	 */
	public String getAffiliation() {
		return affiliation;
	}
}
package isaiah.maze_website.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

import isaiah.maze_website.converters.ConverterListIntArr2D;

/**
 * User model for user info in db.
 * 
 * @author Isaiah
 *
 */
@Entity
@Table(name = "users")
public class User {

	/**
	 * Max username length.
	 */
	private static final int MAX_LENGTH_USERNAME = 20;
	
	/**
	 * Max password length.
	 */
	private static final int MAX_LENGTH_PASSWORD = 30;

	/**
	 * Id for a user.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	private Long id;

	/**
	 * Username for a user.
	 */
	@NotBlank
	@Size(max = MAX_LENGTH_USERNAME)
	private String username;

	/**
	 * Password for a user.
	 */
	@NotBlank
	@Size(max = MAX_LENGTH_PASSWORD)
	private String password;

	/**
	 * Role for a user, uses enumeration to clarify available roles.
	 */
	@Enumerated(EnumType.STRING)
	private Role role;

	/**
	 * Saved mazes for user, uses converter class to help store in db.
	 */
	@Convert(converter = ConverterListIntArr2D.class)
	private List<int[][]> savedMazes;

	public User() {
	}

	public User(Long id, String username, String password, Role role, List<int[][]> savedMazes) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
		this.savedMazes = savedMazes;
	}

	/**
	 * Gets id.
	 * 
	 * @return id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets id.
	 * 
	 * @param id id for user
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets username.
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets username.
	 * @param username input
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets password.
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets password.
	 * @param password input
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets role.
	 * @return role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * Sets role.
	 * @param role input
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	/**
	 * Gets saved mazes.
	 * @return saved mazes
	 */
	public List<int[][]> getSavedMazes() {
		return savedMazes;
	}

	/**
	 * Sets saved mazes.
	 * @param savedMazes input
	 */
	public void setSavedMazes(List<int[][]> savedMazes) {
		this.savedMazes = savedMazes;
	}

}

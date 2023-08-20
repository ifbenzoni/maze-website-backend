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

@Entity
@Table(name = "users")
public class User {

	private static final int MAX_LENGTH_USERNAME = 20;
	private static final int MAX_LENGTH_PASSWORD = 255;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	private Long id;

	@NotBlank
	@Size(max = MAX_LENGTH_USERNAME)
	private String username;

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
	@Column(columnDefinition = "TEXT")
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<int[][]> getSavedMazes() {
		return savedMazes;
	}

	public void setSavedMazes(List<int[][]> savedMazes) {
		this.savedMazes = savedMazes;
	}

}

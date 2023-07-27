package isaiah.maze_website.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import isaiah.maze_website.converters.*;

@Entity
@Table(name = "users")
public class User {
	
	//TODO: finish javadoc and remove unnecessary imports

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	private Long id;

	@NotBlank
	@Size(max = 20)
	private String username;

	@NotBlank
	@Size(max = 30)
	private String password;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	//TODO: add role field with enum
	@Convert(converter = ConverterListIntArr2D.class)
	private List<int[][]> savedMazes;
	
	public User() {}

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

package isaiah.maze_website.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isaiah.maze_website.models.User;
import isaiah.maze_website.repositories.UserRepository;

/**
 * Uses user repository to modify user information in database.
 * 
 * @author Isaiah
 *
 */
@Service
public class UserService {
	
	/**
	 * Dependency injection for user repo.
	 */
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * Adds user to db using user repository class.
	 * @param user input
	 * @return added user
	 */
	public User addUser(User user) {
		return userRepository.save(user);
	}
	
	/**
	 * Gets user from db using user repository class.
	 * @param user input
	 * @return retrieved user
	 */
	public User getUser(User user) {
		return userRepository.findByUsername(user.getUsername());
	}

	/**
	 * Gets all users in db using user repository class.
	 * @return retrieved users
	 */
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
}

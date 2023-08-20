package isaiah.maze_website.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isaiah.maze_website.models.User;
import isaiah.maze_website.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public User addUser(User user) {
		return userRepository.saveAndFlush(user);
	}

	public void removeUser(User user) {
		userRepository.delete(user);
	}

	/**
	 * Gets user from db using user repository class and username.
	 * 
	 * @param user full user object input
	 * @return retrieved user
	 */
	public User getUser(User user) {
		return userRepository.findByUsername(user.getUsername());
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
}

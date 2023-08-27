package isaiah.maze_website.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import isaiah.maze_website.exceptions.MaxUsersReachedException;
import isaiah.maze_website.exceptions.UsernameConflictException;
import isaiah.maze_website.models.User;
import isaiah.maze_website.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

	private static final int MAX_SAVED_USERS = 10;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Checks and formats user. Calls UserRepository to add user to database.
	 * Process for setting the input user's id: create list of all user ids in db,
	 * Long starting at one is incremented until an id is found that is not in the
	 * list of ids currently in db.
	 * 
	 * 
	 * @param user input user
	 * @return saved user
	 * @throws MaxUsersReachedException  thrown if max users in database reached
	 * @throws UsernameConflictException thrown if user's username is a duplicate
	 */
	public User addUser(User user) throws MaxUsersReachedException, UsernameConflictException {
		List<User> allUsers = userRepository.findAll();
		// saved users limit
		if (allUsers.size() >= MAX_SAVED_USERS) {
			throw new MaxUsersReachedException("Maximum number of users in database has been reached.");
		}
		// conflict check
		for (User u : allUsers) {
			if (u.getUsername().equals(user.getUsername())) {
				throw new UsernameConflictException("Should not have duplicate usernames in database.");
			}
		}
		// set id
		List<Long> allIds = new ArrayList<Long>();
		for (User u : allUsers) {
			allIds.add(u.getId());
		}
		Long newId = 1L;
		while (allIds.contains(newId)) {
			newId++;
		}
		user.setId(newId);
		// encode password
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.saveAndFlush(user);
	}

	public User updateUser(User user) {
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

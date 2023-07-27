package isaiah.maze_website.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isaiah.maze_website.models.User;
import isaiah.maze_website.repositories.UserRepository;

@Service
public class UserService {
	
	//TODO: angular documentation?
	@Autowired
	private UserRepository userRepository;
	
	public User addUser(User user) {
		return userRepository.save(user);
	}
	
	public User getUser(User user) {
		return userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
}

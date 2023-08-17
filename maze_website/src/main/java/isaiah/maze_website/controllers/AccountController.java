package isaiah.maze_website.controllers;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import isaiah.maze_website.models.Role;
import isaiah.maze_website.models.User;
import isaiah.maze_website.security.jwt.JwtUtils;
import isaiah.maze_website.services.UserService;

/**
 * REST controller for account information.
 * 
 * @author Isaiah
 *
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {
	
	/**
	 * Max number of saved mazes.
	 */
	private static final int MAX_SAVED_MAZES = 10;

	/**
	 * Dependency injection for password encoder.
	 */
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Dependency injection for user service.
	 */
	@Autowired
	private UserService userService;

	/**
	 * Dependency injection for jwtUtils.
	 */
	@Autowired
	private JwtUtils jwtUtils;

	/**
	 * Checks received user for valid login info.
	 * 
	 * @param user user info for login
	 * @return jwt for user and http status
	 */
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody User user) {
		User retrievedUser = (userService.getUser(user));
		if (retrievedUser == null || !passwordEncoder.matches(user.getPassword(), retrievedUser.getPassword())) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else {
			String jwt = jwtUtils.generateJwt(retrievedUser);
			return new ResponseEntity<>(new Gson().toJson(jwt), HttpStatus.OK);
		}
	}

	/**
	 * Checks recieved jwt and sends details if valid.
	 * 
	 * @param token jwt stored on frontend
	 * @return user details and http status
	 */
	@PostMapping("/jwtUserDetails")
	public ResponseEntity<List<String>> detailsFromJwt(@RequestBody String token) {
		try {
			Claims claims = jwtUtils.getClaims(token);
			List<String> userInfo = new ArrayList<String>();
			userInfo.add(claims.get("username", String.class));
			userInfo.add(claims.get("role", String.class));
			return new ResponseEntity<>(userInfo, HttpStatus.OK);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}

	/**
	 * Creates account and uses provided jwt to authenticate and authorize.
	 * 
	 * @param token jwt
	 * @param user  user
	 * @return success of adding user and http status
	 */
	@PostMapping("/create")
	public ResponseEntity<Boolean> createAccount(String token, User user) {
		// conflict check
		List<User> allUsers = userService.getAllUsers();
		for (User u : allUsers) {
			if (u.getUsername().equals(user.getUsername())) {
				return new ResponseEntity<>(false, HttpStatus.CONFLICT);
			}
		}
		// authorization check
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class) == Role.ADMIN.toString()) {
			// TODO: check database to make sure pw is encoded
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userService.addUser(user);
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
		return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Uses jwt to ensure user has the admin role then sends user info to frontend.
	 * 
	 * @param token jwt
	 * @return list of all users and http status
	 */
	@PostMapping("/getAll")
	public ResponseEntity<List<User>> getUsers(String token) {
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class) == Role.ADMIN.toString()) {
			return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Uses jwt to check username and role. If less than 10 mazes for user, add maze to db.
	 * 
	 * @param token jwt
	 * @param maze maze to save
	 * @return success of saving maze and http status
	 */
	@PostMapping("/saveMaze")
	public ResponseEntity<Boolean> saveMazeInfo(String token, int[][] maze) {
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class) == Role.ADMIN.toString()
				|| claims.get("role", String.class) == Role.GUEST.toString()) {
			User dbUser = userService.getUser(new User(null, claims.get("username", String.class), null, null, null));
			if (dbUser.getSavedMazes().size() < MAX_SAVED_MAZES) {
				dbUser.getSavedMazes().add(maze);
				dbUser.setSavedMazes(dbUser.getSavedMazes());
				return new ResponseEntity<>(true, HttpStatus.OK);
			}
			return new ResponseEntity<>(false, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Uses jwt to check username and role. Gets mazes for user.
	 * 
	 * @param token jwt
	 * @return list of saved mazes and http status
	 */
	@PostMapping("/getMazes")
	public ResponseEntity<List<int[][]>> getMazeInfo(String token) {
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class) == Role.ADMIN.toString()
				|| claims.get("role", String.class) == Role.GUEST.toString()) {
			User dbUser = userService.getUser(new User(null, claims.get("username", String.class), null, null, null));
			return new ResponseEntity<>(dbUser.getSavedMazes(), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	}

}

package isaiah.maze_website.controllers;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
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

@RestController
@RequestMapping("/accounts")
public class AccountController {

	private static final int MAX_SAVED_USERS = 10;
	private static final int MAX_SAVED_MAZES = 10;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtils jwtUtils;

	/**
	 * Checks received user for valid login info. Uses userService to retrieve info
	 * from database and compares passwords with passwordEncoder.
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
	 * Uses jwt to check role. Sends details if valid.
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
	 * Creates account and uses provided jwt to authenticate and authorize. Limits
	 * number of users in database to 10.
	 * 
	 * @param token jwt
	 * @param user  user
	 * @return success of adding user and http status
	 */
	@PostMapping("/create/{token}")
	public ResponseEntity<Boolean> createAccount(@PathVariable String token, @RequestBody User user) {
		// conflict check
		List<User> allUsers = userService.getAllUsers();
		for (User u : allUsers) {
			if (u.getUsername().equals(user.getUsername())) {
				return new ResponseEntity<>(false, HttpStatus.CONFLICT);
			}
		}
		// saved users limit
		if (allUsers.size() >= MAX_SAVED_USERS) {
			return new ResponseEntity<>(false, HttpStatus.CONFLICT);
		}
		// authorization check
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class).equals(Role.ADMIN.toString())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userService.addUser(user);
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
		return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Uses jwt to check role. Removes user from db.
	 * 
	 * @param token jwt
	 * @param user  user
	 * @return success of removing user and http status
	 */
	@PostMapping("/delete/{token}")
	public ResponseEntity<Boolean> deleteAccount(@PathVariable String token, @RequestBody User user) {
		// authorization check
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class).equals(Role.ADMIN.toString())) {
			User userToRemove = userService.getUser(user);
			userService.removeUser(userToRemove);
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
		return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Uses jwt to check role. Sends all guest user info to frontend.
	 * 
	 * @param token jwt
	 * @return list of all users and http status
	 */
	@PostMapping("/getAll")
	public ResponseEntity<List<String>> getUsers(@RequestBody String token) {
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class).equals(Role.ADMIN.toString())) {
			List<User> allUsers = userService.getAllUsers();
			List<String> allUsernames = new ArrayList<>();
			for (User u : allUsers) {
				if (u.getRole() != Role.ADMIN) {
					allUsernames.add(u.getUsername());
				}
			}
			return new ResponseEntity<>(allUsernames, HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Uses jwt to check username and role. If less than 10 mazes for user, add maze
	 * to db.
	 * 
	 * @param token jwt
	 * @param maze  maze to save
	 * @return success of saving maze and http status
	 */
	@PostMapping("/saveMaze/{token}")
	public ResponseEntity<Boolean> saveMazeInfo(@PathVariable String token, @RequestBody int[][] maze) {
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class).equals(Role.ADMIN.toString())
				|| claims.get("role", String.class).equals(Role.GUEST.toString())) {
			User dbUser = userService.getUser(new User(null, claims.get("username", String.class), null, null, null));
			// saved mazes limit
			if (dbUser.getSavedMazes().size() < MAX_SAVED_MAZES) {
				List<int[][]> updatedSavedMazes = new ArrayList<int[][]>();
				updatedSavedMazes.addAll(dbUser.getSavedMazes());
				updatedSavedMazes.add(maze);
				dbUser.setSavedMazes(updatedSavedMazes);
				userService.addUser(dbUser);
				return new ResponseEntity<>(true, HttpStatus.OK);
			}
			return new ResponseEntity<>(false, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Uses jwt to check username and role. Removes provided maze from db.
	 * 
	 * @param token jwt
	 * @param index index of maze to remove
	 * @return success of removing maze and http status
	 */
	@PostMapping("/deleteMaze/{token}")
	public ResponseEntity<Boolean> removeMazeInfo(@PathVariable String token, @RequestBody int index) {
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class).equals(Role.ADMIN.toString())
				|| claims.get("role", String.class).equals(Role.GUEST.toString())) {
			User dbUser = userService.getUser(new User(null, claims.get("username", String.class), null, null, null));
			List<int[][]> updatedSavedMazes = new ArrayList<int[][]>();
			updatedSavedMazes.addAll(dbUser.getSavedMazes());
			updatedSavedMazes.remove(index);
			dbUser.setSavedMazes(updatedSavedMazes);
			userService.addUser(dbUser);
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
		return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Uses jwt to check username and role. Gets mazes for user.
	 * 
	 * @param token jwt
	 * @return list of saved mazes and http status, sends info as JSON
	 */
	@PostMapping("/getMazes")
	public ResponseEntity<String> getMazeInfo(@RequestBody String token) {
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class).equals(Role.ADMIN.toString())
				|| claims.get("role", String.class).equals(Role.GUEST.toString())) {
			User dbUser = userService.getUser(new User(null, claims.get("username", String.class), null, null, null));
			return new ResponseEntity<>(new Gson().toJson(dbUser.getSavedMazes()), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Uses Jwts package to check remaining time on jwt.
	 * 
	 * @param token jwt
	 * @return time left
	 */
	@PostMapping("/getJwtTime")
	public ResponseEntity<Integer> getTimeRemaining(@RequestBody String token) {
		try {
			int timeLeft = jwtUtils.getExpiration(token);
			return new ResponseEntity<>(timeLeft, HttpStatus.OK);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}

}

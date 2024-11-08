package isaiah.maze_website.controllers;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import isaiah.maze_website.exceptions.MaxUsersReachedException;
import isaiah.maze_website.exceptions.UsernameConflictException;
import isaiah.maze_website.models.Role;
import isaiah.maze_website.models.User;
import isaiah.maze_website.security.jwt.JwtUtils;
import isaiah.maze_website.services.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/accounts")
public class AccountController {

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
	 * @param response http servlet response for setting jwt
	 * @return cookie in response + login success message
	 */
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody User user, HttpServletResponse response) {
		User retrievedUser = (userService.getUser(user));
		if (retrievedUser == null || !passwordEncoder.matches(user.getPassword(), retrievedUser.getPassword())) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		String jwt = jwtUtils.generateJwt(retrievedUser);
		
		//can remove Secure; SameSite=None (if needed when testing on localhost)
		response.addHeader("Set-Cookie", "userInfoJwt=" + jwt + "; Path=/; HttpOnly; Secure; SameSite=None");
		
        return new ResponseEntity<>(new Gson().toJson("Login successful"), HttpStatus.OK);
	}
	
	/**
	 * Remove jwt from cookie.
	 * 
	 * @param response http servlet response for setting jwt
	 * @return cookie in response + logout success message
	 */
	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletResponse response) {
		
		response.addHeader("Set-Cookie", "userInfoJwt=; Path=/; HttpOnly; Max-Age=0; SameSite=None; Secure");

	    // Return a successful logout response
	    return new ResponseEntity<>(new Gson().toJson("Logout successful"), HttpStatus.OK);
	}

	/**
	 * Uses jwt to check role. Sends details if valid.
	 * 
	 * @param cookies cookie details from frontend (jwt)
	 * @return user details and http status
	 */
	@GetMapping("/jwtUserDetails")
	public ResponseEntity<List<String>> detailsFromJwt(@RequestHeader(value = "Cookie", required = false) String cookies) {
	    //getting token from cookie
		String token = null;
	    if (cookies != null) {
	        for (String cookie : cookies.split(";")) {
	            if (cookie.trim().startsWith("userInfoJwt=")) {
	                token = cookie.split("=")[1].trim();
	                break;
	            }
	        }
	    }
	    if (token == null) {
	        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	    }
		
	    //check jwt & retrieve details
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
	 * Creates account and uses provided jwt to authenticate and authorize. Checks
	 * for exceptions thrown by UserService.
	 * 
	 * @param cookies cookie details from frontend (jwt)
	 * @param user  user
	 * @return success of adding user and http status
	 */
	@PostMapping("/create")
	public ResponseEntity<Boolean> createAccount(@RequestHeader(value = "Cookie", required = false) String cookies, @RequestBody User user) {
	    //getting token from cookie
		String token = null;
	    if (cookies != null) {
	        for (String cookie : cookies.split(";")) {
	            if (cookie.trim().startsWith("userInfoJwt=")) {
	                token = cookie.split("=")[1].trim();
	                break;
	            }
	        }
	    }
	    if (token == null) {
	        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	    }
		
		// authorization check & create acc
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class).equals(Role.ADMIN.toString())) {
			try {
				userService.addUser(user);
			} catch (MaxUsersReachedException e) {
				System.out.println(e.getMessage());
				return new ResponseEntity<>(false, HttpStatus.CONFLICT);
			} catch (UsernameConflictException e) {
				System.out.println(e.getMessage());
				return new ResponseEntity<>(false, HttpStatus.CONFLICT);
			}
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
		return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Uses jwt to check role. Removes user from db.
	 * 
	 * @param cookies cookie details from frontend (jwt)
	 * @param user  user
	 * @return success of removing user and http status
	 */
	@PostMapping("/delete")
	public ResponseEntity<Boolean> deleteAccount(@RequestHeader(value = "Cookie", required = false) String cookies, @RequestBody User user) {
	    //getting token from cookie
		String token = null;
	    if (cookies != null) {
	        for (String cookie : cookies.split(";")) {
	            if (cookie.trim().startsWith("userInfoJwt=")) {
	                token = cookie.split("=")[1].trim();
	                break;
	            }
	        }
	    }
	    if (token == null) {
	        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	    }
		
	    // authorization check & delete acc
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
	 * @param cookies cookie details from frontend (jwt)
	 * @return list of all users and http status
	 */
	@GetMapping("/getAll")
	public ResponseEntity<List<String>> getUsers(@RequestHeader(value = "Cookie", required = false) String cookies) {
		//getting token from cookie
		String token = null;
	    if (cookies != null) {
	        for (String cookie : cookies.split(";")) {
	            if (cookie.trim().startsWith("userInfoJwt=")) {
	                token = cookie.split("=")[1].trim();
	                break;
	            }
	        }
	    }
	    if (token == null) {
	        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	    }
		
	    //check jwt & get details
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
	 * @param cookies cookie details from frontend (jwt)
	 * @param maze  maze to save
	 * @return success of saving maze and http status
	 */
	@PostMapping("/saveMaze")
	public ResponseEntity<Boolean> saveMazeInfo(@RequestHeader(value = "Cookie", required = false) String cookies, @RequestBody int[][] maze) {
	    //getting token from cookie
		String token = null;
	    if (cookies != null) {
	        for (String cookie : cookies.split(";")) {
	            if (cookie.trim().startsWith("userInfoJwt=")) {
	                token = cookie.split("=")[1].trim();
	                break;
	            }
	        }
	    }
	    if (token == null) {
	        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	    }
		
	    //check jwt & save maze
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
				userService.updateUser(dbUser);
				return new ResponseEntity<>(true, HttpStatus.OK);
			}
			return new ResponseEntity<>(false, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Uses jwt to check username and role. Removes provided maze from db.
	 * 
	 * @param cookies cookie details from frontend (jwt)
	 * @param index index of maze to remove
	 * @return success of removing maze and http status
	 */
	@PostMapping("/deleteMaze")
	public ResponseEntity<Boolean> removeMazeInfo(@RequestHeader(value = "Cookie", required = false) String cookies, @RequestBody int index) {
	    //getting token from cookie
		String token = null;
	    if (cookies != null) {
	        for (String cookie : cookies.split(";")) {
	            if (cookie.trim().startsWith("userInfoJwt=")) {
	                token = cookie.split("=")[1].trim();
	                break;
	            }
	        }
	    }
	    if (token == null) {
	        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	    }
	    
	    //check jwt & remove maze
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class).equals(Role.ADMIN.toString())
				|| claims.get("role", String.class).equals(Role.GUEST.toString())) {
			User dbUser = userService.getUser(new User(null, claims.get("username", String.class), null, null, null));
			List<int[][]> updatedSavedMazes = new ArrayList<int[][]>();
			updatedSavedMazes.addAll(dbUser.getSavedMazes());
			updatedSavedMazes.remove(index);
			dbUser.setSavedMazes(updatedSavedMazes);
			userService.updateUser(dbUser);
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
		return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Uses jwt to check username and role. Gets mazes for user.
	 * 
	 * @param cookies cookie details from frontend (jwt)
	 * @return list of saved mazes and http status, sends info as JSON
	 */
	@GetMapping("/getMazes")
	public ResponseEntity<String> getMazeInfo(@RequestHeader(value = "Cookie", required = false) String cookies) {
	    //getting token from cookie
		String token = null;
	    if (cookies != null) {
	        for (String cookie : cookies.split(";")) {
	            if (cookie.trim().startsWith("userInfoJwt=")) {
	                token = cookie.split("=")[1].trim();
	                break;
	            }
	        }
	    }
	    if (token == null) {
	        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	    }
	    
	    //check jwt & get details
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
	 * @param cookies cookie details from frontend (jwt)
	 * @return time left
	 */
	@GetMapping("/getJwtTime")
	public ResponseEntity<Integer> getTimeRemaining(@RequestHeader(value = "Cookie", required = false) String cookies) {
	    //getting token from cookie
		String token = null;
	    if (cookies != null) {
	        for (String cookie : cookies.split(";")) {
	            if (cookie.trim().startsWith("userInfoJwt=")) {
	                token = cookie.split("=")[1].trim();
	                break;
	            }
	        }
	    }
	    if (token == null) {
	        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	    }
	    
	    //check jwt & get details
		try {
			int timeLeft = jwtUtils.getExpiration(token);
			return new ResponseEntity<>(timeLeft, HttpStatus.OK);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
	}

}

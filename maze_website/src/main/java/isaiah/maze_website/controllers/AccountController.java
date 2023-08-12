package isaiah.maze_website.controllers;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	UserService userService;
	
	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody User user) {
		/*
		System.out.println(user.getUsername());
		System.out.println(user.getPassword());
		System.out.println(user.getSavedMazes());
		//TODO: add stuff to make look like when using database (use database)
		//TODO: remove in memory & add security
		if (!userDetailsService.userExists(user.getUsername())){
			return new ResponseEntity<>(false, HttpStatus.OK);
		}
		UserDetails dbUserInfo = userDetailsService.loadUserByUsername(user.getUsername());
		return new ResponseEntity<>(dbUserInfo.getUsername().equals(user.getUsername())
				&& passwordEncoder.matches(user.getPassword(), dbUserInfo.getPassword()), HttpStatus.OK);
		*/
		//TODO: can clean usb after set up github
		User retrievedUser = (userService.getUser(user));
		if (retrievedUser == null || !passwordEncoder.matches(user.getPassword(), retrievedUser.getPassword())) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else {
			String jwt = jwtUtils.generateJwt(retrievedUser);
			return new ResponseEntity<>(new Gson().toJson(jwt), HttpStatus.OK);//TODO: compare using password encoder
		}
	}
	
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
	 * creates account and uses provided jwt to authenticate
	 * @param token jwt
	 * @param user user
	 */
	@PostMapping("/create")
	public void createAccount(String token, User user) {
		//TODO: require unique username
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class) == Role.ADMIN.toString()) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userService.addUser(user);
		}
	}
	
	@PostMapping("/getAll")
	public ResponseEntity<List<User>> getUsers(String token, User user) {
		Claims claims = jwtUtils.getClaims(token);
		if (claims.get("role", String.class) == Role.ADMIN.toString()) {
			return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	}
	
	@PostMapping("/saveMaze")
	public void SaveMazeInfo(User user) {
		//TODO: set up here & on angular, will need get method(s)
	}

}

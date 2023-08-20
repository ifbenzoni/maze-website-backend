package isaiah.maze_website.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Class for adding initial user to database.
 * 
 * @author Isaiah
 *
 */
public final class EncodeAndDisplayPassword {

	/**
	 * Encodes and displays provided password. Clear after use.
	 * 
	 * @param args default
	 */
	public static void main(String[] args) {
		String password = "test_password";
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		System.out.println(passwordEncoder.encode(password));
	}

}

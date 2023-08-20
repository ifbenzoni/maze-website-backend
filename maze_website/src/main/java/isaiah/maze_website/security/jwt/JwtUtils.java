package isaiah.maze_website.security.jwt;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import isaiah.maze_website.models.User;

/**
 * Used for generating JWTs, getting claims, and getting expiration time.
 * 
 * @author Isaiah
 *
 */
@Component
public class JwtUtils {

	/**
	 * Secret key generated using Keys class to properly format key.
	 */
	private SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	// String jwtSecret = Encoders.BASE64.encode(key.getEncoded());

	/**
	 * JWT expiration in minutes.
	 */
	private static final Long JWT_EXPIRATION = 60L;

	/**
	 * Milliseconds in a minute.
	 */
	private static final int MS_IN_MIN = 60000;

	/**
	 * Generates JWT based on provided user. Stores username and role.
	 * 
	 * @param user input user
	 * @return JWT
	 */
	public String generateJwt(User user) {
		return Jwts.builder().claim("username", user.getUsername()).claim("role", user.getRole())
				.setSubject(user.getUsername()).setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(Instant.now()))
				.setExpiration(Date.from(Instant.now().plus(JWT_EXPIRATION, ChronoUnit.MINUTES)))
				.signWith(key, SignatureAlgorithm.HS512).compact();
	}

	public Claims getClaims(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		return claims;
	}

	public int getExpiration(String token) {
		int remainingTime = (int) (Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
				.getExpiration().getTime() - System.currentTimeMillis()) / MS_IN_MIN;
		return remainingTime;
	}

}

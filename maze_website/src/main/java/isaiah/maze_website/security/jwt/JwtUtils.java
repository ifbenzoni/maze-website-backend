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
 * Used for generating and getting claims from JWT.
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
	 * Generates JWT based on provided user.
	 * 
	 * @param user input user
	 * @return JWT
	 */
	public String generateJwt(User user) {
		return Jwts.builder().claim("username", user.getUsername()).claim("role", user.getRole())
				.setSubject(user.getUsername()).setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(Instant.now()))
				// TODO: reasonable reset value and refresh
				.setExpiration(Date.from(Instant.now().plus(JWT_EXPIRATION, ChronoUnit.MINUTES)))
				.signWith(key, SignatureAlgorithm.HS512).compact();
	}

	/**
	 * Gets claims from JWT.
	 * 
	 * @param token JWT
	 * @return claims
	 */
	public Claims getClaims(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		return claims;
	}

}

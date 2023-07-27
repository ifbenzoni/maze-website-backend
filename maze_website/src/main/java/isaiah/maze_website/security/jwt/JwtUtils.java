package isaiah.maze_website.security.jwt;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import isaiah.maze_website.models.User;

@Component
public class JwtUtils {
	
	//used to generate and properly format key
	SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	//String jwtSecret = Encoders.BASE64.encode(key.getEncoded());

	public String generateJwt(User user) {
		return Jwts.builder()
				.claim("username", user.getUsername())
				.claim("role", user.getRole())
				.setSubject(user.getUsername())
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(Instant.now()))
				//TODO: reasonable reset value and refresh
				.setExpiration(Date.from(Instant.now().plus(1L, ChronoUnit.MINUTES)))
				.signWith(key, SignatureAlgorithm.HS512)
				.compact();
	}
	
	public Claims getClaims(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		return claims;
	}

}

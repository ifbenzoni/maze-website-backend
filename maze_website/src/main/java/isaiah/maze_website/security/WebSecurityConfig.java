package isaiah.maze_website.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Steps up spring security and CORS.
 * 
 * @author Isaiah
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	/**
	 * Max age of CORS pre-flight.
	 */
	private static final long MAX_AGE_CORS = 3600L;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// May want to learn spring security properly at some point.
		// Using JWT instead of CSRF, may want to learn more about this.
		http.cors().and().csrf().disable();
		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration
				.setAllowedOrigins(Arrays.asList("https://amazing-website.party"));//"https://localhost:4200"
		corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type",
				"Access-Control-Allow-Origin", "X-Requested-With"));
		corsConfiguration.setExposedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type",
				"Access-Control-Allow-Origin", "X-Requested-With"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		corsConfiguration.setMaxAge(MAX_AGE_CORS);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

}

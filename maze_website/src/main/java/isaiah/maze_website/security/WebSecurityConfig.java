package isaiah.maze_website.security;

import java.util.Collections;
import java.util.List;

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
	 * Max age of CORS pre-flight. - matching default val
	 */
	private static final long MAX_AGE_CORS = 1800L;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable();
		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		//corsConfiguration.applyPermitDefaultValues();
		List<String> allowedOrigins = List.of("https://amazing-website.party", "https://maze-website-frontend.web.app");//"https://localhost:4200", "http://localhost:4200"
		corsConfiguration.setAllowedOrigins(allowedOrigins);
		List<String> allowedHeaders = Collections.singletonList("*");
		corsConfiguration.setAllowedHeaders(allowedHeaders);
		List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
		corsConfiguration.setAllowedMethods(allowedMethods);
		corsConfiguration.setMaxAge(MAX_AGE_CORS);
	    corsConfiguration.setAllowCredentials(true);//important for cookies

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

}

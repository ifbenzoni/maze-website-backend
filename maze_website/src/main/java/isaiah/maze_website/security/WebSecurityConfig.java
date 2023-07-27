package isaiah.maze_website.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
        UserDetails guest = User.withUsername("guest")
            .password(passwordEncoder().encode("guestPass"))
            .roles("USER")
            .build();
        UserDetails testUser = User.withUsername("testUser")
            .password(passwordEncoder().encode("testUserPass"))
            .roles("USER")
            .build();
        UserDetails admin = User.withUsername("admin")
            .password(passwordEncoder().encode("adminPass"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(guest, testUser, admin);
	}
	
	@Bean 
	public PasswordEncoder passwordEncoder() { 
	    return new BCryptPasswordEncoder(); 
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		//TODO: remove any info about local ip address from test hosting locally(in at least two files on front and backend)
		//TODO: learn about and re-enable csrf if important
		http.cors().and().csrf().disable();
		//TODO: remove below if unnecessary
		//TODO: remove in memory user details above
		/*
		.and()
		.authorizeHttpRequests((requests) -> requests
			.requestMatchers("/", "/home").permitAll()
			.anyRequest().authenticated()
		)
		.formLogin((form) -> form
			.loginPage("/login")
			.permitAll()
		)
		.logout((logout) -> logout.permitAll());
		 */
		return http.build();
	}
	
}

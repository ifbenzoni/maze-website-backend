package isaiah.maze_website;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;

@SpringBootApplication
public class MazeWebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MazeWebsiteApplication.class, args);
	}
    
}

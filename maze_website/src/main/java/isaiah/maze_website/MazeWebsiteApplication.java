package isaiah.maze_website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public final class MazeWebsiteApplication {
	
	private MazeWebsiteApplication() {}

	public static void main(String[] args) {
		SpringApplication.run(MazeWebsiteApplication.class, args);
	}
    
}

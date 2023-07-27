package isaiah.maze_website.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import isaiah.maze_website.models.User;

public interface UserRepository extends JpaRepository<User, Long>{

	User findByUsernameAndPassword(String username, String password);

}

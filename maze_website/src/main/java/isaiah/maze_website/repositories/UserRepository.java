package isaiah.maze_website.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import isaiah.maze_website.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);

}

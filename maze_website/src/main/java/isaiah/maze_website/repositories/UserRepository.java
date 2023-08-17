package isaiah.maze_website.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import isaiah.maze_website.models.User;

/**
 * User repository interface to be used by user service.
 * 
 * @author Isaiah
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);

}

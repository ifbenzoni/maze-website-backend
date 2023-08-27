package isaiah.maze_website.exceptions;

public class MaxUsersReachedException extends Exception {

	private static final long serialVersionUID = 1L;

	public MaxUsersReachedException(String errorMessage) {
		super(errorMessage);
	}
}

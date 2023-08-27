package isaiah.maze_website.exceptions;

public class UsernameConflictException extends Exception {

	private static final long serialVersionUID = 1L;

	public UsernameConflictException(String errorMessage) {
		super(errorMessage);
	}
}

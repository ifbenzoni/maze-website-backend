package isaiah.maze_website.exceptions;

public class UsernameConflictException extends Exception {
    public UsernameConflictException (String errorMessage) {
        super(errorMessage);
    }
}

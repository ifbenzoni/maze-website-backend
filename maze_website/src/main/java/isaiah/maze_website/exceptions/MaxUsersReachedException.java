package isaiah.maze_website.exceptions;

public class MaxUsersReachedException extends Exception {
    public MaxUsersReachedException (String errorMessage) {
        super(errorMessage);
    }
}

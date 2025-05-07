package novi.backend.eindopdrachtmoesproducebackend.exceptions;

/**
 * Exception thrown when a user attempts to access or modify a resource they are not authorized to.
 */
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String username, Long resourceId) {
        super("User '" + username + "' is not authorized to access or modify resource with ID: " + resourceId);
    }
}
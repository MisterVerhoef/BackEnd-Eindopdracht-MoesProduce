package novi.backend.eindopdrachtmoesproducebackend.exceptions;

/**
 * Exception thrown when a user attempts to access or modify a resource they are not authorized to.
 */
public class UnauthorizedAccessException extends RuntimeException {

    /****
     * Constructs an UnauthorizedAccessException with a custom error message.
     *
     * @param message the detail message explaining the unauthorized access
     */
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    /**
     * Constructs an UnauthorizedAccessException with a standardized message indicating that the specified user is not authorized to access or modify the given resource.
     *
     * @param username the username of the user attempting unauthorized access
     * @param resourceId the ID of the resource the user attempted to access or modify
     */
    public UnauthorizedAccessException(String username, Long resourceId) {
        super("User '" + username + "' is not authorized to access or modify resource with ID: " + resourceId);
    }
}
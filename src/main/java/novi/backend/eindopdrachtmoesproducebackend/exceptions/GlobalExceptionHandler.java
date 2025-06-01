package novi.backend.eindopdrachtmoesproducebackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Constants for response body keys
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    /**
     * Handles UnauthorizedAccessException by returning a structured 403 Forbidden error response.
     *
     * The response body includes the timestamp, HTTP status code, reason phrase, and the exception message.
     *
     * @return a ResponseEntity with a 403 status and detailed error information
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Object> handleUnauthorizedAccessException(UnauthorizedAccessException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.FORBIDDEN.value());
        body.put(ERROR, HttpStatus.FORBIDDEN.getReasonPhrase());
        body.put(MESSAGE, ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Handles AdvertNotFoundException by returning a structured 404 Not Found error response.
     *
     * The response body includes the current timestamp, HTTP status code, reason phrase, and the exception message.
     *
     * @return a ResponseEntity containing error details and HTTP status 404
     */
    @ExceptionHandler(AdvertNotFoundException.class)
    public ResponseEntity<Object> handleAdvertNotFoundException(AdvertNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.NOT_FOUND.value());
        body.put(ERROR, HttpStatus.NOT_FOUND.getReasonPhrase());
        body.put(MESSAGE, ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles all uncaught exceptions and returns a structured error response with HTTP 400 Bad Request status.
     *
     * @return a ResponseEntity containing a map with timestamp, status code, error reason, and exception message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put(MESSAGE, ex.getMessage());

        return ResponseEntity.badRequest().body(body);
    }

}

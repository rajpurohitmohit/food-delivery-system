package foodDelivery.exception;

/**
 * Custom Exception for User-related errors
 * Demonstrates Exception Handling - User Defined Exceptions
 * 
 * Used for:
 * - Invalid login credentials
 * - Registration validation failures
 * - User not found errors
 * - Password validation failures
 */
public class InvalidUserException extends Exception {
    
    public InvalidUserException(String message) {
        super(message);
    }
    
    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }
}

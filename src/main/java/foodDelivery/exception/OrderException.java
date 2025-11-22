package foodDelivery.exception;

/**
 * Custom Exception for Order-related errors
 * 
 * Used for:
 * - Empty order validation
 * - Invalid order status updates
 * - Order not found errors
 * - Order cancellation issues
 */
public class OrderException extends Exception {
    
    public OrderException(String message) {
        super(message);
    }
    
    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }
}

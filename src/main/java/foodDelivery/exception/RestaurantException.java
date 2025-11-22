// File: com/fooddelivery/exception/RestaurantException.java
package foodDelivery.exception;

/**
 * Custom Exception for Restaurant-related errors
 * 
 * Used for:
 * - Restaurant not found
 * - Menu item validation
 * - Restaurant status issues
 */
public class RestaurantException extends Exception {
    
    public RestaurantException(String message) {
        super(message);
    }
    
    public RestaurantException(String message, Throwable cause) {
        super(message, cause);
    }
}
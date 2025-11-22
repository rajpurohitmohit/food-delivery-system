// File: com/fooddelivery/thread/OrderProcessingThread.java
package foodDelivery.thread;

import foodDelivery.model.Order;
import foodDelivery.service.OrderService;
import foodDelivery.exception.OrderException;

/**
 * Thread class for processing orders
 * Demonstrates: 
 * - Thread creation using Thread class (extends Thread)
 * - Thread lifecycle (start, run, sleep)
 * - Thread priorities
 * - Exception handling within threads
 */
public class OrderProcessingThread extends Thread {
    private Order order;
    private OrderService orderService;

    /**
     * Constructor with order
     * Demonstrates: Constructor initialization, thread naming
     */
    public OrderProcessingThread(Order order) {
        this.order = order;
        this.orderService = new OrderService();
        
        // Set thread name for better identification
        this.setName("OrderProcessor-" + order.getOrderId());
    }

    /**
     * Constructor with order and priority
     * Demonstrates: Constructor overloading, thread priority setting
     */
    public OrderProcessingThread(Order order, int priority) {
        this.order = order;
        this.orderService = new OrderService();
        this.setPriority(priority);
        this.setName("OrderProcessor-" + order.getOrderId());
    }

    /**
     * Main execution method of the thread
     * Demonstrates: Thread lifecycle, sleep, exception handling
     */
    @Override
    public void run() {
        try {
            System.out.println("\n========================================");
            System.out.println("[" + Thread.currentThread().getName() + "] Starting order processing");
            System.out.println("   Thread ID: " + Thread.currentThread().threadId());
            System.out.println("   Priority: " + Thread.currentThread().getPriority());
            System.out.println("   Order ID: " + order.getOrderId());
            System.out.println("========================================\n");
            
            // Stage 1: Order Confirmation
            System.out.println("[" + Thread.currentThread().getName() + "] Confirming order...");
            Thread.sleep(2000); // Simulate confirmation time
            orderService.updateOrderStatus(order.getOrderId(), "CONFIRMED");
            System.out.println("✓ [" + Thread.currentThread().getName() + "] Order CONFIRMED");
            
            // Stage 2: Food Preparation
            System.out.println("[" + Thread.currentThread().getName() + "] Preparing food...");
            Thread.sleep(3000); // Simulate preparation time
            orderService.updateOrderStatus(order.getOrderId(), "PREPARING");
            System.out.println("✓ [" + Thread.currentThread().getName() + "] Order PREPARING");
            
            // Stage 3: Quality Check
            System.out.println("[" + Thread.currentThread().getName() + "] Quality check...");
            Thread.sleep(1500); // Simulate quality check
            System.out.println("[" + Thread.currentThread().getName() + "] Quality check passed");
            
            // Stage 4: Dispatch
            System.out.println("[" + Thread.currentThread().getName() + "] Dispatching order...");
            Thread.sleep(2000); // Simulate dispatch time
            orderService.updateOrderStatus(order.getOrderId(), "OUT_FOR_DELIVERY");
            System.out.println("✓ [" + Thread.currentThread().getName() + "] Order OUT FOR DELIVERY");
            
            // Stage 5: Delivery
            System.out.println("[" + Thread.currentThread().getName() + "] Delivering order...");
            Thread.sleep(4000); // Simulate delivery time
            orderService.updateOrderStatus(order.getOrderId(), "DELIVERED");
            
            System.out.println("\n========================================");
            System.out.println(" [" + Thread.currentThread().getName() + "] Order DELIVERED successfully!");
            System.out.println("   Total processing time: ~12.5 seconds");
            System.out.println("========================================\n");
            
        } catch (InterruptedException e) {
            System.err.println("[" + Thread.currentThread().getName() + "] Order processing interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (OrderException e) {
            System.err.println("[" + Thread.currentThread().getName() + "] Error updating order status: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[" + Thread.currentThread().getName() + "] Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Get order being processed
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Get current processing stage
     * Demonstrates: Method to check thread state
     */
    public String getCurrentStage() {
        if (!this.isAlive()) {
            return "COMPLETED";
        }
        return order.getStatus();
    }
}
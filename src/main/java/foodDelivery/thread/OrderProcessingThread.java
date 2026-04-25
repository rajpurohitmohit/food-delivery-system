package foodDelivery.thread;

import foodDelivery.model.Order;
import foodDelivery.service.OrderService;
import foodDelivery.exception.OrderException;

public class OrderProcessingThread extends Thread {
    private Order order;
    private OrderService orderService;

    public OrderProcessingThread(Order order) {
        this.order = order;
        this.orderService = new OrderService();
    }

    public void run() {
        try {
            System.out.println("\nProcessing order #" + order.getOrderId() + "...");
            
            // Stage 1: Confirm
            Thread.sleep(2000);
            orderService.updateOrderStatus(order.getOrderId(), "CONFIRMED");
            System.out.println("Order #" + order.getOrderId() + " confirmed");
            
            // Stage 2: Prepare
            Thread.sleep(3000);
            orderService.updateOrderStatus(order.getOrderId(), "PREPARING");
            System.out.println("Order #" + order.getOrderId() + " being prepared");
            
            // Stage 3: Deliver
            Thread.sleep(3000);
            orderService.updateOrderStatus(order.getOrderId(), "DELIVERED");
            System.out.println("Order #" + order.getOrderId() + " delivered!\n");
            
        } catch (OrderException e) {
            System.err.println("Error updating order: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Order processing interrupted for order #" + order.getOrderId());
            Thread.currentThread().interrupt();
        }
    }

    public Order getOrder() {
        return order;
    }
}
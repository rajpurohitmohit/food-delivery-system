// File: com/fooddelivery/model/Order.java
package foodDelivery.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Order class managing customer orders
 */
public class Order {
    private int orderId;
    private int customerId;
    private int restaurantId;
    private ArrayList<OrderItem> items;
    private double totalAmount;
    private String status; // PENDING, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    private Date orderDate;

    public Order(int customerId, int restaurantId) {
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
        this.status = "PENDING";
        this.orderDate = new Date();
    }

    public void addItem(OrderItem item) {
        items.add(item);
        calculateTotal();
    }

    public void removeItem(int itemId) {
        items.removeIf(item -> item.getItemId() == itemId);
        calculateTotal();
    }

    private void calculateTotal() {
        totalAmount = 0.0;
        for (OrderItem item : items) {
            totalAmount += item.getSubtotal();
        }
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public ArrayList<OrderItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<OrderItem> items) {
        this.items = items;
        calculateTotal();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}
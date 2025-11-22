// File: com/fooddelivery/service/OrderService.java
package foodDelivery.service;

import foodDelivery.model.Order;
import foodDelivery.model.OrderItem;
import foodDelivery.exception.OrderException;
import foodDelivery.util.DatabaseConnection;
import foodDelivery.thread.OrderProcessingThread;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    /**
     * Start order processing in a separate thread
     * Demonstrates: Multithreading integration
     */
    public void startOrderProcessing(Order order) {
        OrderProcessingThread thread = new OrderProcessingThread(order);
        thread.start();
        System.out.println("✓ Order processing started in background (Thread ID: " + thread.threadId() + ")");
    }

    /**
     * Place a new order
     * Demonstrates: Transaction management with commit/rollback
     */
    public void placeOrder(Order order) throws OrderException {
        if (order.getItems().isEmpty()) {
            throw new OrderException("Cannot place order with no items");
        }
        
        if (order.getTotalAmount() <= 0) {
            throw new OrderException("Invalid order amount");
        }

        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert order
            String orderSql = "INSERT INTO orders (customer_id, restaurant_id, total_amount, status) VALUES (?, ?, ?, ?)";
            orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            
            orderStmt.setInt(1, order.getCustomerId());
            orderStmt.setInt(2, order.getRestaurantId());
            orderStmt.setDouble(3, order.getTotalAmount());
            orderStmt.setString(4, order.getStatus());
            
            orderStmt.executeUpdate();
            
            // Get generated order ID
            rs = orderStmt.getGeneratedKeys();
            if (rs.next()) {
                order.setOrderId(rs.getInt(1));
            } else {
                throw new OrderException("Failed to get order ID");
            }
            
            // Insert order items
            String itemSql = "INSERT INTO order_items (order_id, item_id, item_name, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
            itemStmt = conn.prepareStatement(itemSql);
            
            for (OrderItem item : order.getItems()) {
                itemStmt.setInt(1, order.getOrderId());
                itemStmt.setInt(2, item.getItemId());
                itemStmt.setString(3, item.getItemName());
                itemStmt.setDouble(4, item.getPrice());
                itemStmt.setInt(5, item.getQuantity());
                itemStmt.setDouble(6, item.getSubtotal());
                itemStmt.executeUpdate();
            }
            
            conn.commit(); // Commit transaction if all successful
            
        } catch (SQLException e) {
            // Rollback on any error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error");
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            throw new OrderException("Failed to place order: " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (orderStmt != null) orderStmt.close();
                if (itemStmt != null) itemStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Get all orders for a customer
     * Demonstrates: Complex query with multiple table access
     */
    public List<Order> getCustomerOrders(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_date DESC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, customerId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("customer_id"),
                    rs.getInt("restaurant_id")
                );
                order.setOrderId(rs.getInt("order_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                
                // Load order items for each order
                loadOrderItems(order);
                orders.add(order);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching customer orders: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return orders;
    }

    /**
     * Get all orders for a restaurant
     */
    public List<Order> getRestaurantOrders(int restaurantId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE restaurant_id = ? ORDER BY order_date DESC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, restaurantId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("customer_id"),
                    rs.getInt("restaurant_id")
                );
                order.setOrderId(rs.getInt("order_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                
                loadOrderItems(order);
                orders.add(order);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching restaurant orders: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return orders;
    }

    /**
     * Load order items for a specific order
     * Helper method demonstrating modular code design
     */
    private void loadOrderItems(Order order) {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, order.getOrderId());
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OrderItem item = new OrderItem(
                    rs.getInt("item_id"),
                    rs.getString("item_name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                );
                order.addItem(item);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading order items: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Update order status
     * Demonstrates: UPDATE query with validation
     */
    public void updateOrderStatus(int orderId, String status) throws OrderException {
        // Validate status
        String[] validStatuses = {"PENDING", "CONFIRMED", "PREPARING", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"};
        boolean isValid = false;
        for (String validStatus : validStatuses) {
            if (validStatus.equalsIgnoreCase(status)) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw new OrderException("Invalid order status: " + status);
        }
        
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, status.toUpperCase());
            pstmt.setInt(2, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new OrderException("Order not found with ID: " + orderId);
            }
            
        } catch (SQLException e) {
            throw new OrderException("Failed to update order status: " + e.getMessage(), e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Get order by ID
     */
    public Order getOrderById(int orderId) throws OrderException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Order order = new Order(
                    rs.getInt("customer_id"),
                    rs.getInt("restaurant_id")
                );
                order.setOrderId(rs.getInt("order_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                
                loadOrderItems(order);
                return order;
            } else {
                throw new OrderException("Order not found");
            }
            
        } catch (SQLException e) {
            throw new OrderException("Failed to fetch order: " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
package foodDelivery.service;

import foodDelivery.model.Customer;
import foodDelivery.model.Restaurant;
import foodDelivery.exception.InvalidUserException;
import foodDelivery.exception.OrderException;
import foodDelivery.util.DatabaseConnection;
import java.sql.*;

public class UserService {

    public void cancelOrder(int orderId) throws OrderException {
        String sql = "UPDATE orders SET status = 'CANCELLED' WHERE order_id = ? AND status = 'PENDING'";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new OrderException("Cannot cancel order. Order may not exist or is already processed.");
            }
            
        } catch (SQLException e) {
            throw new OrderException("Failed to cancel order: " + e.getMessage(), e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    
    public void registerCustomer(Customer customer) throws InvalidUserException {
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new InvalidUserException("Email cannot be empty");
        }
        
        if (!customer.getEmail().contains("@")) {
            throw new InvalidUserException("Invalid email format");
        }
        
        if (customer.getPassword() == null || customer.getPassword().length() < 6) {
            throw new InvalidUserException("Password must be at least 6 characters");
        }
        
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new InvalidUserException("Name cannot be empty");
        }

        String sql = "INSERT INTO customers (name, email, phone, address, password) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getPassword());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    customer.setUserId(rs.getInt(1));
                }
            }
            
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new InvalidUserException("Email already exists. Please use a different email.");
        } catch (SQLException e) {
            throw new InvalidUserException("Failed to register customer: " + e.getMessage(), e);
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


    public Customer loginCustomer(String email, String password) throws InvalidUserException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidUserException("Email cannot be empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidUserException("Password cannot be empty");
        }
        
        String sql = "SELECT * FROM customers WHERE email = ? AND password = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("password")
                );
                customer.setUserId(rs.getInt("customer_id"));
                return customer;
            } else {
                throw new InvalidUserException("Invalid email or password");
            }
            
        } catch (SQLException e) {
            throw new InvalidUserException("Login failed: " + e.getMessage(), e);
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

    
    public void registerRestaurant(Restaurant restaurant) throws InvalidUserException {
        if (restaurant.getEmail() == null || restaurant.getEmail().trim().isEmpty()) {
            throw new InvalidUserException("Email cannot be empty");
        }
        
        if (!restaurant.getEmail().contains("@")) {
            throw new InvalidUserException("Invalid email format");
        }
        
        if (restaurant.getPassword() == null || restaurant.getPassword().length() < 6) {
            throw new InvalidUserException("Password must be at least 6 characters");
        }
        
        if (restaurant.getName() == null || restaurant.getName().trim().isEmpty()) {
            throw new InvalidUserException("Restaurant name cannot be empty");
        }

        String sql = "INSERT INTO restaurants (name, email, phone, address, password) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, restaurant.getName());
            pstmt.setString(2, restaurant.getEmail());
            pstmt.setString(3, restaurant.getPhone());
            pstmt.setString(4, restaurant.getAddress());
            pstmt.setString(5, restaurant.getPassword());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    restaurant.setRestaurantId(rs.getInt(1));
                }
            }
            
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new InvalidUserException("Email already exists. Please use a different email.");
        } catch (SQLException e) {
            throw new InvalidUserException("Failed to register restaurant: " + e.getMessage(), e);
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


    public Restaurant loginRestaurant(String email, String password) throws InvalidUserException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidUserException("Email cannot be empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidUserException("Password cannot be empty");
        }
        
        String sql = "SELECT * FROM restaurants WHERE email = ? AND password = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Restaurant restaurant = new Restaurant(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("password")
                );
                restaurant.setRestaurantId(rs.getInt("restaurant_id"));
                restaurant.setActive(rs.getBoolean("is_active"));
                return restaurant;
            } else {
                throw new InvalidUserException("Invalid email or password");
            }
            
        } catch (SQLException e) {
            throw new InvalidUserException("Login failed: " + e.getMessage(), e);
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
    
    
    public Customer getCustomerById(int customerId) throws InvalidUserException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("password")
                );
                customer.setUserId(rs.getInt("customer_id"));
                return customer;
            } else {
                throw new InvalidUserException("Customer not found");
            }
            
        } catch (SQLException e) {
            throw new InvalidUserException("Failed to fetch customer: " + e.getMessage(), e);
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
// File: com/fooddelivery/service/RestaurantService.java
package foodDelivery.service;

import foodDelivery.model.Restaurant;
import foodDelivery.model.MenuItem;
import foodDelivery.util.DatabaseConnection;
import foodDelivery.util.FileReportGenerator;
import foodDelivery.exception.RestaurantException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Restaurant operations
 * Demonstrates: ArrayList usage, JDBC Statement and PreparedStatement
 */
public class RestaurantService {

    /**
     * Get all active restaurants
     * Demonstrates: Statement, ResultSet, ArrayList population
     */
    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT * FROM restaurants WHERE is_active = 1";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Restaurant restaurant = new Restaurant(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("cuisine_type"),
                    rs.getString("password")
                );
                restaurant.setRestaurantId(rs.getInt("restaurant_id"));
                restaurant.setRating(rs.getDouble("rating"));
                restaurant.setActive(rs.getBoolean("is_active"));
                restaurants.add(restaurant);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching restaurants: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return restaurants;
    }

    /**
     * Add a new menu item
     * Demonstrates: PreparedStatement with multiple parameters
     */
    public void addMenuItem(MenuItem item) {
        String sql = "INSERT INTO menu_items (restaurant_id, name, description, price, category) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, item.getRestaurantId());
            pstmt.setString(2, item.getName());
            pstmt.setString(3, item.getDescription());
            pstmt.setDouble(4, item.getPrice());
            pstmt.setString(5, item.getCategory());
            
            pstmt.executeUpdate();
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                item.setItemId(rs.getInt(1));
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding menu item: " + e.getMessage());
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
     * Get menu items for a specific restaurant
     * Demonstrates: PreparedStatement with WHERE clause
     */
    public List<MenuItem> getMenuItems(int restaurantId) {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE restaurant_id = ? AND is_available = 1";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, restaurantId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                MenuItem item = new MenuItem(
                    rs.getInt("restaurant_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("category")
                );
                item.setItemId(rs.getInt("item_id"));
                item.setAvailable(rs.getBoolean("is_available"));
                items.add(item);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching menu items: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return items;
    }

    /**
     * Get restaurant by ID
     */
    public Restaurant getRestaurantById(int restaurantId) throws RestaurantException {
        String sql = "SELECT * FROM restaurants WHERE restaurant_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, restaurantId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Restaurant restaurant = new Restaurant(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("cuisine_type"),
                    rs.getString("password")
                );
                restaurant.setRestaurantId(rs.getInt("restaurant_id"));
                restaurant.setRating(rs.getDouble("rating"));
                restaurant.setActive(rs.getBoolean("is_active"));
                return restaurant;
            } else {
                throw new RestaurantException("Restaurant not found");
            }
            
        } catch (SQLException e) {
            throw new RestaurantException("Failed to fetch restaurant: " + e.getMessage(), e);
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
     * Generate sales report for a restaurant
     * Demonstrates: File handling integration with JDBC
     */
    public void generateSalesReport(int restaurantId) {
        String sql = "SELECT o.order_id, o.total_amount, o.status, o.order_date " +
                    "FROM orders o WHERE o.restaurant_id = ? ORDER BY o.order_date DESC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, restaurantId);
            rs = pstmt.executeQuery();
            
            // Generate file report using FileReportGenerator
            FileReportGenerator.generateSalesReport(restaurantId, rs);
            System.out.println("\n✓ Sales report generated successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error generating sales report: " + e.getMessage());
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
     * Update restaurant rating
     */
    public void updateRating(int restaurantId, double rating) throws RestaurantException {
        if (rating < 0 || rating > 5) {
            throw new RestaurantException("Rating must be between 0 and 5");
        }
        
        String sql = "UPDATE restaurants SET rating = ? WHERE restaurant_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setDouble(1, rating);
            pstmt.setInt(2, restaurantId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new RestaurantException("Restaurant not found");
            }
            
        } catch (SQLException e) {
            throw new RestaurantException("Failed to update rating: " + e.getMessage(), e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
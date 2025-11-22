package foodDelivery.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Connection Utility Class
 * Handles MySQL database connection and initialization
 */
public class DatabaseConnection {
    // Database credentials - UPDATE THESE WITH YOUR MYSQL SETTINGS
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/food_delivery_db";
    private static final String DB_NAME = "food_delivery_db";
    private static final String DB_URL_WITH_DB = "jdbc:mysql://127.0.0.1:3306/food_delivery_db";
    private static final String USERNAME = "root";  // Change to your MySQL username
    private static final String PASSWORD = "Sql@2025";  // Change to your MySQL password
    
    private static Connection connection = null;

    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Establish connection
                connection = DriverManager.getConnection(DB_URL_WITH_DB, USERNAME, PASSWORD);
                System.out.println("Database connection established successfully!");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Make sure to add mysql-connector-java to your project.", e);
            }
        }
        return connection;
    }

    /**
     * Initialize database and create tables
     * @throws SQLException if initialization fails
     */
    public static void initializeDatabase() throws SQLException {
        Connection conn = null;
        Statement stmt = null;

        try {
            // First, connect without specifying database to create it
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            stmt = conn.createStatement();

            // Create database if not exists
            String createDB = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(createDB);
            System.out.println("Database created/verified successfully!");

            // Use the database
            stmt.executeUpdate("USE " + DB_NAME);

            // Create Customers table
            String createCustomersTable = "CREATE TABLE IF NOT EXISTS customers (" +
                    "customer_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100) UNIQUE NOT NULL," +
                    "phone VARCHAR(15) NOT NULL," +
                    "address VARCHAR(255) NOT NULL," +
                    "password VARCHAR(100) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createCustomersTable);

            // Create Restaurants table
            String createRestaurantsTable = "CREATE TABLE IF NOT EXISTS restaurants (" +
                    "restaurant_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100) UNIQUE NOT NULL," +
                    "phone VARCHAR(15) NOT NULL," +
                    "address VARCHAR(255) NOT NULL," +
                    "cuisine_type VARCHAR(50) NOT NULL," +
                    "password VARCHAR(100) NOT NULL," +
                    "rating DECIMAL(2,1) DEFAULT 0.0," +
                    "is_active BOOLEAN DEFAULT TRUE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createRestaurantsTable);

            // Create Menu Items table
            String createMenuItemsTable = "CREATE TABLE IF NOT EXISTS menu_items (" +
                    "item_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "restaurant_id INT NOT NULL," +
                    "name VARCHAR(100) NOT NULL," +
                    "description TEXT," +
                    "price DECIMAL(10,2) NOT NULL," +
                    "category VARCHAR(50) NOT NULL," +
                    "is_available BOOLEAN DEFAULT TRUE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createMenuItemsTable);

            // Create Orders table
            String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders (" +
                    "order_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "customer_id INT NOT NULL," +
                    "restaurant_id INT NOT NULL," +
                    "total_amount DECIMAL(10,2) NOT NULL," +
                    "status VARCHAR(50) DEFAULT 'PENDING'," +
                    "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "delivery_address VARCHAR(255)," +
                    "FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createOrdersTable);

            // Create Order Items table
            String createOrderItemsTable = "CREATE TABLE IF NOT EXISTS order_items (" +
                    "order_item_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "order_id INT NOT NULL," +
                    "item_id INT NOT NULL," +
                    "item_name VARCHAR(100) NOT NULL," +
                    "quantity INT NOT NULL," +
                    "price DECIMAL(10,2) NOT NULL," +
                    "subtotal DECIMAL(10,2) NOT NULL," +
                    "FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createOrderItemsTable);

            // Create Delivery table
            String createDeliveryTable = "CREATE TABLE IF NOT EXISTS delivery (" +
                    "delivery_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "order_id INT NOT NULL," +
                    "delivery_person_name VARCHAR(100)," +
                    "delivery_person_phone VARCHAR(15)," +
                    "delivery_status VARCHAR(50) DEFAULT 'ASSIGNED'," +
                    "estimated_time INT," +
                    "actual_delivery_time TIMESTAMP," +
                    "FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createDeliveryTable);

            System.out.println("All tables created successfully!");

        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found!", e);
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed!");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
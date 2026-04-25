package foodDelivery.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/food_delivery_db";
    private static final String DB_NAME = "food_delivery_db";
    private static final String DB_URL_WITH_DB = "jdbc:mysql://127.0.0.1:3306/food_delivery_db";
    
    private static String USERNAME;
    private static String PASSWORD;
    
    static {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
            USERNAME = props.getProperty("DB_USER", "root");
            PASSWORD = props.getProperty("DB_PASSWORD", "Sql@2025");
        } catch (IOException e) {
            System.err.println("Warning: Could not read .env file, falling back to default credentials");
            USERNAME = "root";
            PASSWORD = "Sql@2025";
        }
    }
    

    /**
     * @return A new database Connection
     * @throws SQLException 
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL_WITH_DB, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Make sure to add mysql-connector-java to your project.", e);
        }
    }

    /**
     * @throws SQLException 
     */
    public static void initializeDatabase() throws SQLException {
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            stmt = conn.createStatement();

            String createDB = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(createDB);
            System.out.println("Database created/verified successfully!");

            stmt.executeUpdate("USE " + DB_NAME);
            
            // Customers Table
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

            // Restaurants table
            String createRestaurantsTable = "CREATE TABLE IF NOT EXISTS restaurants (" +
                    "restaurant_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100) UNIQUE NOT NULL," +
                    "phone VARCHAR(15) NOT NULL," +
                    "address VARCHAR(255) NOT NULL," +
                    "password VARCHAR(100) NOT NULL," +
                    "is_active BOOLEAN DEFAULT TRUE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createRestaurantsTable);

            // Menu Items table
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

            // Order Items table
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

            System.out.println("All tables created successfully!");

        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found!", e);
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
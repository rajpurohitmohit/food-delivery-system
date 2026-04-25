package foodDelivery.util;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class FileReportGenerator {
    
    private static final String REPORT_DIR = "reports/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    

    public static void generateSalesReport(int restaurantId, ResultSet rs) {
        File dir = new File(REPORT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String filename = REPORT_DIR + "sales_report_restaurant_" + restaurantId + "_" + timestamp + ".txt";
        
        try (FileWriter writer = new FileWriter(filename)) {
            
            writer.write("=".repeat(80) + "\n");
            writer.write("SALES REPORT - Restaurant ID: " + restaurantId + "\n");
            writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("=".repeat(80) + "\n");
            writer.write("\n");
            

            writer.write(String.format("%-12s %-20s %-15s %-25s", "Order ID", "Total Amount", "Status", "Order Date") + "\n");
            writer.write("-".repeat(80) + "\n");
            

            double totalSales = 0.0;
            int orderCount = 0;
            
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                double totalAmount = rs.getDouble("total_amount");
                String status = rs.getString("status");
                String orderDate = rs.getString("order_date");
                
                writer.write(String.format("%-12d $%-19.2f %-15s %-25s", 
                    orderId, totalAmount, status, orderDate) + "\n");
                
                if ("DELIVERED".equals(status) || "COMPLETED".equals(status)) {
                    totalSales += totalAmount;
                }
                orderCount++;
            }
            

            writer.write("\n");
            writer.write("-".repeat(80) + "\n");
            writer.write(String.format("Total Orders: %d", orderCount) + "\n");
            writer.write(String.format("Total Sales (Completed): $%.2f", totalSales) + "\n");
            writer.write("=".repeat(80) + "\n");
            
            System.out.println("📄 Report saved to: " + filename);
            
        } catch (SQLException e) {
            System.err.println("Error reading data from ResultSet: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error writing report file: " + e.getMessage());
        }
    }
}

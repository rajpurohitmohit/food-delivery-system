package foodDelivery;

// File: FoodDeliverySystem.java
import foodDelivery.service.*;
import foodDelivery.model.*;
import foodDelivery.exception.*;
import foodDelivery.util.DatabaseConnection;

import java.util.Scanner;
import java.util.List;

/**
 * Main Application Class for Food Delivery System
 * Demonstrates: OOP concepts, Exception Handling, Package usage
 */
public class FoodDeliverySystem {
    private UserService userService;
    private RestaurantService restaurantService;
    private OrderService orderService;
    private Scanner scanner;

    public FoodDeliverySystem() {
        this.userService = new UserService();
        this.restaurantService = new RestaurantService();
        this.orderService = new OrderService();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        System.out.println("=== Food Delivery System ===\n");
        
        // Initialize database
        try {
            DatabaseConnection.initializeDatabase();
            System.out.println("Database initialized successfully!\n");
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            return;
        }

        FoodDeliverySystem app = new FoodDeliverySystem();
        app.run();
    }

    public void run() {
        boolean exit = false;

        while (!exit) {
            try {
                System.out.println("\n=== Main Menu ===");
                System.out.println("1. Customer Login/Register");
                System.out.println("2. Restaurant Login/Register");
                System.out.println("3. Browse Restaurants");
                System.out.println("4. Exit");
                System.out.print("Choose option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        handleCustomerMenu();
                        break;
                    case 2:
                        handleRestaurantMenu();
                        break;
                    case 3:
                        browseRestaurants();
                        break;
                    case 4:
                        exit = true;
                        System.out.println("Thank you for using Food Delivery System!");
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                scanner.nextLine(); // clear buffer
            }
        }

        scanner.close();
    }

    private void handleCustomerMenu() throws InvalidUserException {
        System.out.println("\n=== Customer Menu ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.print("Choose option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            registerCustomer();
        } else if (choice == 2) {
            Customer customer = loginCustomer();
            if (customer != null) {
                customerDashboard(customer);
            }
        }
    }

    private void registerCustomer() throws InvalidUserException {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Customer customer = new Customer(name, email, phone, address, password);
        userService.registerCustomer(customer);
        System.out.println("Registration successful! Customer ID: " + customer.getUserId());
    }

    private Customer loginCustomer() {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            return userService.loginCustomer(email, password);
        } catch (InvalidUserException e) {
            System.err.println("Login failed: " + e.getMessage());
            return null;
        }
    }

    private void customerDashboard(Customer customer) {
        boolean back = false;

        while (!back) {
            try {
                System.out.println("\n=== Customer Dashboard ===");
                System.out.println("Welcome, " + customer.getName() + "!");
                System.out.println("1. Browse Restaurants");
                System.out.println("2. View My Orders");
                System.out.println("3. View Profile");
                System.out.println("4. Back to Main Menu");
                System.out.print("Choose option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        placeOrder(customer);
                        break;
                    case 2:
                        viewCustomerOrders(customer);
                        break;
                    case 3:
                        customer.displayInfo();
                        break;
                    case 4:
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    private void placeOrder(Customer customer) throws OrderException {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        
        if (restaurants.isEmpty()) {
            System.out.println("No restaurants available!");
            return;
        }

        System.out.println("\n=== Available Restaurants ===");
        for (int i = 0; i < restaurants.size(); i++) {
            System.out.println((i + 1) + ". " + restaurants.get(i).getName() + 
                             " - " + restaurants.get(i).getCuisineType());
        }

        System.out.print("Select restaurant (number): ");
        int restChoice = scanner.nextInt() - 1;
        scanner.nextLine();

        if (restChoice < 0 || restChoice >= restaurants.size()) {
            throw new OrderException("Invalid restaurant selection");
        }

        Restaurant selectedRestaurant = restaurants.get(restChoice);
        List<MenuItem> menu = restaurantService.getMenuItems(selectedRestaurant.getRestaurantId());

        if (menu.isEmpty()) {
            System.out.println("No menu items available!");
            return;
        }

        System.out.println("\n=== Menu ===");
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.get(i);
            System.out.printf("%d. %s - $%.2f (%s)\n", 
                i + 1, item.getName(), item.getPrice(), item.getCategory());
        }

        Order order = new Order(customer.getUserId(), selectedRestaurant.getRestaurantId());

        boolean addingItems = true;
        while (addingItems) {
            System.out.print("\nSelect item (number) or 0 to finish: ");
            int itemChoice = scanner.nextInt() - 1;
            scanner.nextLine();

            if (itemChoice == -1) {
                addingItems = false;
            } else if (itemChoice >= 0 && itemChoice < menu.size()) {
                System.out.print("Quantity: ");
                int quantity = scanner.nextInt();
                scanner.nextLine();

                MenuItem item = menu.get(itemChoice);
                OrderItem orderItem = new OrderItem(item.getItemId(), item.getName(), 
                                                    item.getPrice(), quantity);
                order.addItem(orderItem);
                System.out.println("Added to order!");
            } else {
                System.out.println("Invalid selection!");
            }
        }

        if (order.getItems().isEmpty()) {
            System.out.println("No items added. Order cancelled.");
            return;
        }

        System.out.printf("\nTotal Amount: $%.2f\n", order.getTotalAmount());
        System.out.print("Confirm order? (yes/no): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("yes")) {
            orderService.placeOrder(order);
            System.out.println("\n✓ Order placed successfully! Order ID: " + order.getOrderId());
            
            // Start order processing in separate thread
            orderService.startOrderProcessing(order);
        } else {
            System.out.println("Order cancelled.");
        }
    }

    private void viewCustomerOrders(Customer customer) {
        List<Order> orders = orderService.getCustomerOrders(customer.getUserId());
        
        if (orders.isEmpty()) {
            System.out.println("No orders found!");
            return;
        }

        System.out.println("\n=== Your Orders ===");
        for (Order order : orders) {
            System.out.printf("Order ID: %d | Status: %s | Total: $%.2f\n",
                order.getOrderId(), order.getStatus(), order.getTotalAmount());
        }
    }

    private void handleRestaurantMenu() throws InvalidUserException {
        System.out.println("\n=== Restaurant Menu ===");
        System.out.println("1. Register Restaurant");
        System.out.println("2. Login");
        System.out.print("Choose option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            registerRestaurant();
        } else if (choice == 2) {
            Restaurant restaurant = loginRestaurant();
            if (restaurant != null) {
                restaurantDashboard(restaurant);
            }
        }
    }

    private void registerRestaurant() throws InvalidUserException {
        System.out.print("Enter restaurant name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter cuisine type: ");
        String cuisineType = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Restaurant restaurant = new Restaurant(name, email, phone, address, 
                                               cuisineType, password);
        userService.registerRestaurant(restaurant);
        System.out.println("Restaurant registered! ID: " + restaurant.getRestaurantId());
    }

    private Restaurant loginRestaurant() {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            return userService.loginRestaurant(email, password);
        } catch (InvalidUserException e) {
            System.err.println("Login failed: " + e.getMessage());
            return null;
        }
    }

    private void restaurantDashboard(Restaurant restaurant) {
        boolean back = false;

        while (!back) {
            try {
                System.out.println("\n=== Restaurant Dashboard ===");
                System.out.println("Welcome, " + restaurant.getName() + "!");
                System.out.println("1. Add Menu Item");
                System.out.println("2. View Menu");
                System.out.println("3. View Orders");
                System.out.println("4. Update Order Status");
                System.out.println("5. Generate Sales Report");
                System.out.println("6. Back to Main Menu");
                System.out.print("Choose option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        addMenuItem(restaurant);
                        break;
                    case 2:
                        viewMenu(restaurant);
                        break;
                    case 3:
                        viewRestaurantOrders(restaurant);
                        break;
                    case 4:
                        updateOrderStatus(restaurant);
                        break;
                    case 5:
                        restaurantService.generateSalesReport(restaurant.getRestaurantId());
                        break;
                    case 6:
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    private void addMenuItem(Restaurant restaurant) {
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        System.out.print("Enter price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter category (APPETIZER/MAIN_COURSE/DESSERT/BEVERAGE): ");
        String category = scanner.nextLine();

        MenuItem item = new MenuItem(restaurant.getRestaurantId(), name, 
                                     description, price, category);
        restaurantService.addMenuItem(item);
        System.out.println("Menu item added successfully!");
    }

    private void viewMenu(Restaurant restaurant) {
        List<MenuItem> menu = restaurantService.getMenuItems(restaurant.getRestaurantId());
        
        if (menu.isEmpty()) {
            System.out.println("No menu items found!");
            return;
        }

        System.out.println("\n=== Menu Items ===");
        for (MenuItem item : menu) {
            System.out.printf("%s - $%.2f (%s)\n", item.getName(), 
                            item.getPrice(), item.getCategory());
        }
    }

    private void viewRestaurantOrders(Restaurant restaurant) {
        List<Order> orders = orderService.getRestaurantOrders(restaurant.getRestaurantId());
        
        if (orders.isEmpty()) {
            System.out.println("No orders found!");
            return;
        }

        System.out.println("\n=== Restaurant Orders ===");
        for (Order order : orders) {
            System.out.printf("Order ID: %d | Status: %s | Total: $%.2f\n",
                order.getOrderId(), order.getStatus(), order.getTotalAmount());
        }
    }

    private void updateOrderStatus(Restaurant restaurant) throws OrderException {
        System.out.print("Enter order ID: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Status options: CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED");
        System.out.print("Enter new status: ");
        String status = scanner.nextLine();

        orderService.updateOrderStatus(orderId, status);
        System.out.println("Order status updated successfully!");
    }

    private void browseRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        
        if (restaurants.isEmpty()) {
            System.out.println("No restaurants available!");
            return;
        }

        System.out.println("\n=== Available Restaurants ===");
        for (Restaurant restaurant : restaurants) {
            System.out.printf("%s - %s\n  Address: %s\n  Phone: %s\n\n",
                restaurant.getName(), restaurant.getCuisineType(),
                restaurant.getAddress(), restaurant.getPhone());
        }
    }
}
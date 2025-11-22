// File: com/fooddelivery/model/Restaurant.java
package foodDelivery.model;

/**
 * Restaurant class with additional properties
 */
public class Restaurant {
    private int restaurantId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String cuisineType;
    private String password;
    private double rating;
    private boolean isActive;

    public Restaurant(String name, String email, String phone, String address, 
                     String cuisineType, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.cuisineType = cuisineType;
        this.password = password;
        this.rating = 0.0;
        this.isActive = true;
    }

    // Getters and Setters
    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void displayInfo() {
        System.out.println("\n=== Restaurant Information ===");
        System.out.println("ID: " + restaurantId);
        System.out.println("Name: " + name);
        System.out.println("Cuisine: " + cuisineType);
        System.out.println("Phone: " + phone);
        System.out.println("Address: " + address);
        System.out.println("Rating: " + rating);
    }
}
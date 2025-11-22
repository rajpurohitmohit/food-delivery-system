// File: com/fooddelivery/model/Customer.java
package foodDelivery.model;

/**
 * Customer class demonstrating Inheritance and Polymorphism
 */
public class Customer extends User {
    private String address;

    // Constructor overloading
    public Customer(String name, String email, String phone, String address, String password) {
        super(name, email, phone, password);
        this.address = address;
    }

    // Implementing abstract method (Runtime Polymorphism)
    @Override
    public void displayInfo() {
        System.out.println("\n=== Customer Information ===");
        System.out.println("ID: " + getUserId());
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("Phone: " + getPhone());
        System.out.println("Address: " + address);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
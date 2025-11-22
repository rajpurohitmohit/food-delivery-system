# Food Delivery System

## Compile the Project
Creates a `bin` folder and compiles all Java files:

```bash
mkdir -p bin && javac -d bin -cp ".:mysql-connector-j-9.5.0.jar" src/main/java/foodDelivery/**/*.java
```

## Run the Application
Executes the program and initializes the database:

```bash
java -cp "bin:mysql-connector-j-9.5.0.jar" foodDelivery.FoodDeliverySystem
```


## Setup Database
Update MySQL credentials in `src/main/java/foodDelivery/util/DatabaseConnection.java`:
```java
private static final String USERNAME = "root";  // Your MySQL username
private static final String PASSWORD = "yourpassword";  // Your MySQL password
```

## Project Structure
```
food-delivery-system/
├── .gitignore
├── pom.xml
├── mysql-connector-j-9.5.0.jar
├── README.md
└── src/
    └── main/
        └── java/
            └── foodDelivery/
                ├── FoodDeliverySystem.java
                ├── exception/
                ├── model/
                ├── service/
                ├── thread/
                └── util/
```

## Database Tables
The system automatically creates 6 tables:
1. customers
2. restaurants
3. menu_items
4. orders
5. order_items
6. delivery

## Features
- Customer & Restaurant Registration/Login
- Menu Management
- Order Placement & Tracking
- Multi-threaded Order Processing
- Sales Report Generation
- File I/O Operations
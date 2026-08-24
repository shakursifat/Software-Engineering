/**
 * FACTORY DESIGN PATTERN TEMPLATE
 * 
 * Use this pattern when:
 * 1. You have a superclass/interface and multiple subclasses.
 * 2. You need to create objects dynamically based on some input (e.g., String, Enum).
 * 3. You want to hide the object creation logic (the 'new' keyword) from the client code.
 */

// ==========================================
// STEP 1: Define the Common Interface
// ==========================================
/**
 * Replace 'Product' with the general name of what you are creating.
 * Examples: Transport, Notification, PaymentMethod, Document.
 */

/*
[ ] Did I create a single interface?

[ ] Do all my concrete classes implement that interface?

[ ] Did I create a Factory class with a method returning the interface?

[ ] Does my factory use a switch or if-else to return the new concrete objects?

[ ] Does my main/client class only use the Factory and the interface (and never uses new ConcreteProduct() directly)?
 */
interface Product {
    // Replace this method with the common action all products share.
    // Examples: deliver(), send(), processPayment(), print()
    void doSomething(); 
}

// ==========================================
// STEP 2: Create Concrete Implementations
// ==========================================
/**
 * Create a class for EACH specific type. They must implement the interface.
 * Replace 'ConcreteProductA/B' with specific names (e.g., Truck, EmailNotification).
 */
class ConcreteProductA implements Product {
    @Override
    public void doSomething() {
        System.out.println("Executing behavior for Product A");
        // Add specific logic for A here
    }
}

class ConcreteProductB implements Product {
    @Override
    public void doSomething() {
        System.out.println("Executing behavior for Product B");
        // Add specific logic for B here
    }
}

// ==========================================
// STEP 3: Create the Factory Class
// ==========================================
/**
 * This is the core of the pattern. It centralizes all object creation.
 * Replace 'ProductFactory' with your specific factory name.
 */
class ProductFactory {
    
    /**
     * The factory method.
     * @param type The condition/input used to determine which object to create.
     * @return An object implementing the Product interface.
     */
    public static Product createProduct(String type) {
        if (type == null || type.isEmpty()) {
            return null; // Or throw an exception
        }
        
        // Use the input to decide which concrete class to instantiate.
        switch (type.toUpperCase()) {
            case "TYPE_A":
                return new ConcreteProductA();
            case "TYPE_B":
                return new ConcreteProductB();
                
            // To add new types in the future, just add a new 'case' here!
            
            default:
                throw new IllegalArgumentException("Unknown product type: " + type);
        }
    }
}

// ==========================================
// STEP 4: Client Code
// ==========================================
/**
 * The client code doesn't know HOW objects are created.
 * It only interacts with the Factory and the Product interface.
 */
public class ClientApplication {
    public static void main(String[] args) {
        
        // 1. Client asks the factory for an object based on input.
        Product item1 = ProductFactory.createProduct("TYPE_A");
        // 2. Client uses the common interface method.
        item1.doSomething(); 
        
        
        // Requesting a different type
        Product item2 = ProductFactory.createProduct("TYPE_B");
        item2.doSomething();
        
        // If you pass an invalid type, the factory handles the error safely.
        // Product errorItem = ProductFactory.createProduct("UNKNOWN");
    }
}

/**
 * FACTORY METHOD DESIGN PATTERN TEMPLATE
 * 
 * Use this pattern when:
 * 1. A class cannot anticipate the class of objects it must create.
 * 2. You want a class to define a standard workflow, but delegate the specific 
 *    instantiation to its subclasses.
 * 3. You want to localize the knowledge of which helper subclass is the delegate.
 */

// ==========================================
// STEP 1: Define the Product Interface
// ==========================================
/**
 * The Product interface declares the operations that all concrete products 
 * must implement.
 */
interface Product {
    void doStuff();
}

// ==========================================
// STEP 2: Create Concrete Products
// ==========================================
/**
 * Concrete Products provide various implementations of the Product interface.
 */
class ConcreteProductA implements Product {
    @Override
    public void doStuff() {
        System.out.println("Doing stuff using ConcreteProductA.");
    }
}

class ConcreteProductB implements Product {
    @Override
    public void doStuff() {
        System.out.println("Doing stuff using ConcreteProductB.");
    }
}

// ==========================================
// STEP 3: Define the Creator (Base Factory)
// ==========================================
/**
 * The Creator class declares the factory method that is supposed to return an 
 * object of a Product class. The Creator's subclasses usually provide the 
 * implementation of this method.
 */
abstract class Creator {
    
    /**
     * The core business logic that relies on the Product objects.
     * Note: The Creator is NOT just a factory; it usually contains important 
     * core logic that operates on the products returned by the factory method.
     */
    public void someOperation() {
        System.out.println("Creator: Commencing standard workflow...");
        
        // 1. Call the factory method to create a Product object.
        Product product = factoryMethod();
        
        // 2. Use the product.
        product.doStuff();
        
        System.out.println("Creator: Standard workflow finished.\n");
    }

    /**
     * The Factory Method. 
     * Subclasses MUST override this to instantiate specific products.
     */
    protected abstract Product factoryMethod();
}

// ==========================================
// STEP 4: Create Concrete Creators
// ==========================================
/**
 * Concrete Creators override the factory method to change the resulting product's type.
 */
class ConcreteCreatorA extends Creator {
    // Overriding the factory method to return Product A
    @Override
    protected Product factoryMethod() {
        return new ConcreteProductA();
    }
}

class ConcreteCreatorB extends Creator {
    // Overriding the factory method to return Product B
    @Override
    protected Product factoryMethod() {
        return new ConcreteProductB();
    }
}

// ==========================================
// STEP 5: Client Code
// ==========================================
/**
 * The client code works with an instance of a concrete creator, albeit through 
 * its base interface (or abstract class). As long as the client keeps working 
 * with the creator via the base class, you can pass it any creator's subclass.
 */
public class FactoryMethodTemplateApp {
    public static void main(String[] args) {
        
        System.out.println("--- Client needs workflow with Product A ---");
        // Instantiate the specific creator
        Creator creatorA = new ConcreteCreatorA();
        // Execute the common workflow. It will automatically use Product A.
        creatorA.someOperation();

        System.out.println("--- Client needs workflow with Product B ---");
        // Instantiate the specific creator
        Creator creatorB = new ConcreteCreatorB();
        // Execute the common workflow. It will automatically use Product B.
        creatorB.someOperation();
    }
}

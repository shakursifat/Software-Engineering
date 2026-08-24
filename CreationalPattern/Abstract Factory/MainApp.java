/**
 * ABSTRACT FACTORY DESIGN PATTERN TEMPLATE
 * 
 * Use this pattern when:
 * 1. Your system needs to be independent of how its products are created, composed, and represented.
 * 2. You have multiple families of related products (Theme A, Theme B).
 * 3. A family of related product objects is designed to be used together, and you need to enforce this constraint.
 */

// ==========================================
// STEP 1: Define Abstract Products
// ==========================================
/**
 * Define distinct interfaces for each distinct product.
 * Examples: Button, Checkbox, Chair, Sofa.
 */
interface AbstractProductA {
    void performActionA();
}

interface AbstractProductB {
    void performActionB();
}

// ==========================================
// STEP 2: Create Concrete Products (The Matrix)
// ==========================================
/**
 * Implement the abstract products for EACH family (variant/theme).
 */

// --- Family 1 (e.g., Light Theme, Modern Style) ---
class Family1ProductA implements AbstractProductA {
    @Override
    public void performActionA() {
        System.out.println("Doing action A the Family 1 way.");
    }
}

class Family1ProductB implements AbstractProductB {
    @Override
    public void performActionB() {
        System.out.println("Doing action B the Family 1 way.");
    }
}

// --- Family 2 (e.g., Dark Theme, Victorian Style) ---
class Family2ProductA implements AbstractProductA {
    @Override
    public void performActionA() {
        System.out.println("Doing action A the Family 2 way.");
    }
}

class Family2ProductB implements AbstractProductB {
    @Override
    public void performActionB() {
        System.out.println("Doing action B the Family 2 way.");
    }
}

// ==========================================
// STEP 3: Define the Abstract Factory
// ==========================================
/**
 * The Abstract Factory interface declares a set of methods that return 
 * different abstract products.
 */
interface AbstractFactory {
    AbstractProductA createProductA();
    AbstractProductB createProductB();
}

// ==========================================
// STEP 4: Create Concrete Factories
// ==========================================
/**
 * Concrete Factories implement the creation methods of the abstract factory.
 * Each concrete factory corresponds to a specific variant (family) of products 
 * and creates only those product variants.
 */

class Family1Factory implements AbstractFactory {
    @Override
    public AbstractProductA createProductA() {
        return new Family1ProductA(); // Returns Family 1's version of Product A
    }

    @Override
    public AbstractProductB createProductB() {
        return new Family1ProductB(); // Returns Family 1's version of Product B
    }
}

class Family2Factory implements AbstractFactory {
    @Override
    public AbstractProductA createProductA() {
        return new Family2ProductA(); // Returns Family 2's version of Product A
    }

    @Override
    public AbstractProductB createProductB() {
        return new Family2ProductB(); // Returns Family 2's version of Product B
    }
}

// ==========================================
// STEP 5: Client Code
// ==========================================
/**
 * The client code works with factories and products only through abstract types: 
 * AbstractFactory, AbstractProductA, and AbstractProductB.
 * This lets you pass any factory or product subclass to the client code without breaking it.
 */
class ClientApplication {
    private AbstractProductA productA;
    private AbstractProductB productB;

    // The client strictly requires a factory, guaranteeing products match
    public ClientApplication(AbstractFactory factory) {
        this.productA = factory.createProductA();
        this.productB = factory.createProductB();
    }

    public void executeOperations() {
        productA.performActionA();
        productB.performActionB();
    }
}

// ==========================================
// STEP 6: Main Runner
// ==========================================
public class MainApp {
    public static void main(String[] args) {
        
        System.out.println("--- Booting System with Family 1 ---");
        // 1. Instantiate the specific factory
        AbstractFactory factory1 = new Family1Factory();
        // 2. Inject it into the client application
        ClientApplication app1 = new ClientApplication(factory1);
        // 3. Application runs using only Family 1 objects
        app1.executeOperations();

        System.out.println("\n--- Switching System to Family 2 ---");
        // To switch the entire family of objects, we only change the factory
        AbstractFactory factory2 = new Family2Factory();
        ClientApplication app2 = new ClientApplication(factory2);
        app2.executeOperations();
    }
}

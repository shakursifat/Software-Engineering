/**
 * SINGLETON DESIGN PATTERN TEMPLATE
 * 
 * Use this pattern when:
 * 1. You need exactly ONE instance of a class across the entire application.
 * 2. You need a global point of access to that instance.
 * 3. Common use cases: Loggers, Database Connections, Configuration Managers.
 */


/*
[ ] Is my instance variable private and static?

[ ] Is my constructor private?

[ ] Is my getInstance() method public and static?

[ ] Does my getInstance() method check if (instance == null) before using the new keyword?

[ ] In the Client/Main class, am I using ClassName.getInstance() instead of new ClassName()?
 */

class Singleton {

    // ==========================================
    // STEP 1: Private Static Instance
    // ==========================================
    /**
     * Create a private static variable to hold the single instance of the class.
     * It is static because it belongs to the class itself, not to any object.
     */
    private static Singleton instance;

    // Add your normal class variables here
    // private String someData;

    // ==========================================
    // STEP 2: Private Constructor
    // ==========================================
    /**
     * The constructor MUST be private. 
     * This prevents any other class from using the 'new' keyword to create a second object.
     */
    private Singleton() {
        // Optional: Put setup/initialization logic here
        // this.someData = "Initialized Data";
    }

    // ==========================================
    // STEP 3: Public Static Access Method
    // ==========================================
    /**
     * This method acts as the global access point.
     * It uses "Lazy Initialization" - the object is only created the first time this method is called.
     */
    public static Singleton getInstance() {
        // Check if the instance already exists
        if (instance == null) {
            // If not, create it (this is the ONLY place 'new Singleton()' is allowed)
            instance = new Singleton();
        }
        // Return the existing instance
        return instance;
    }

    // ==========================================
    // STEP 4: Business Logic Methods
    // ==========================================
    /**
     * Add the normal methods your class needs to perform its duties.
     */
    public void doSomething() {
        System.out.println("Singleton is doing some work!");
    }
}

// ==========================================
// STEP 5: Client Code
// ==========================================
/**
 * The client code CANNOT use 'new Singleton()'. It must call getInstance().
 */
public class ClientApplication {
    public static void main(String[] args) {
        
        // Correct way to get the object
        Singleton obj1 = Singleton.getInstance();
        obj1.doSomething();

        // Getting the object again from somewhere else in the code
        Singleton obj2 = Singleton.getInstance();

        // Verification: Both variables point to the exact same memory location
        // System.out.println(obj1 == obj2); // This will print 'true'
    }
}

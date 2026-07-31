// ==========================================
// 1. The Singleton Class
// ==========================================
class Logger {
    // A private static variable to hold the single instance of the class
    private static Logger instance;

    // A private constructor prevents other classes from using 'new Logger()'
    private Logger() {
        System.out.println("--- System: Logger instance successfully created. ---");
    }

    // The public static method that provides global access to the instance
    public static Logger getInstance() {
        // Lazy initialization: only create the instance if it doesn't exist yet
        if (instance == null) {
            instance = new Logger();
        }
        return instance; // Return the existing instance
    }

    // The business logic method for logging messages
    public void log(String message) {
        System.out.println("[AUDIT LOG]: " + message);
    }
}

// ==========================================
// 2. The Client Code
// ==========================================
public class BankingApplication {
    public static void main(String[] args) {
        
        System.out.println("Module 1 (Deposit): Requesting Logger...");
        // Client 1 requests the logger
        Logger module1Logger = Logger.getInstance();
        module1Logger.log("User John deposited $500.");

        System.out.println("\nModule 2 (Withdrawal): Requesting Logger...");
        // Client 2 requests the logger
        Logger module2Logger = Logger.getInstance();
        module2Logger.log("User Jane withdrew $200.");

        System.out.println("\n--- Verification ---");
        // Verify that both modules are using the exact same object in memory
        if (module1Logger == module2Logger) {
            System.out.println("SUCCESS: Both modules share the exact same Logger instance.");
        } else {
            System.out.println("ERROR: Different Logger instances were created.");
        }
    }
}

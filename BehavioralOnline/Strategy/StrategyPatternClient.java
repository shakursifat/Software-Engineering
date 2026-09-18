/**
 * 1. THE STRATEGY INTERFACE
 * Declares the method(s) that all concrete strategies must implement. 
 * This is the common "algorithm" that will be swapped out dynamically.
 * Rename to match your problem (e.g., DiscountPolicy, SchedulingPolicy, PaymentMethod).
 */
interface Strategy {
    /**
     * The core algorithm to execute. 
     * Update the parameters and return type based on your exam question.
     * (e.g., double calculateDiscount(Purchase p), Task selectTask(List tasks))
     */
    void executeAlgorithm(String data);
}

/**
 * 2. CONCRETE STRATEGY A
 * A specific implementation of the algorithm.
 * Rename to match your specific rule (e.g., FCFSPolicy, PurchaseAmountDiscount, CreditCardPayment).
 */
class ConcreteStrategyA implements Strategy {
    @Override
    public void executeAlgorithm(String data) {
        System.out.println("Strategy A: Processing data using Algorithm A... [" + data + "]");
        // Insert specific mathematical logic, sorting logic, or business rules here
    }
}

/**
 * 3. CONCRETE STRATEGY B
 * Another interchangeable implementation of the algorithm.
 */
class ConcreteStrategyB implements Strategy {
    @Override
    public void executeAlgorithm(String data) {
        System.out.println("Strategy B: Processing data using a TOTALLY DIFFERENT Algorithm B... [" + data + "]");
        // Insert alternative logic here
    }
}

/**
 * 4. THE CONTEXT CLASS
 * This is the object that *uses* the strategy. It doesn't know how the algorithm 
 * works internally; it just delegates the work to the currently active Strategy object.
 * Rename to match your problem (e.g., SmartDiscountCalculator, TaskScheduler, ShoppingCart).
 */
class Context {
    // Holds a reference to the active strategy
    private Strategy currentStrategy;

    /**
     * The context usually requires a default strategy when created.
     */
    public Context(Strategy initialStrategy) {
        this.currentStrategy = initialStrategy;
    }

    /**
     * The setter allows the client to change the algorithm AT RUNTIME.
     * This is the defining feature of the Strategy Pattern.
     */
    public void setStrategy(Strategy newStrategy) {
        System.out.println("Context: Switched active strategy.");
        this.currentStrategy = newStrategy;
    }

    /**
     * The context delegates the actual work to the active strategy object.
     */
    public void executeActiveStrategy(String data) {
        if (currentStrategy == null) {
            System.out.println("Context: No strategy set!");
            return;
        }
        
        // Delegate the work
        currentStrategy.executeAlgorithm(data);
    }
}

/**
 * 5. CLIENT CODE (Testing the Pattern)
 */
public class StrategyPatternClient {
    public static void main(String[] args) {
        String sampleData = "Sample Input";

        // 1. Initialize the Context with a specific Strategy
        Context context = new Context(new ConcreteStrategyA());
        
        System.out.println("--- Executing First Strategy ---");
        // 2. The Context executes the logic via Strategy A
        context.executeActiveStrategy(sampleData);

        System.out.println("\n--- Swapping Strategies ---");
        // 3. Change the strategy dynamically at runtime!
        context.setStrategy(new ConcreteStrategyB());
        
        // 4. Execute again. The context stays the same, but the behavior completely changes.
        context.executeActiveStrategy(sampleData);
    }
}

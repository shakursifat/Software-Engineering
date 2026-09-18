/**
 * 1. THE CONTEXT CLASS
 * The main object the client interacts with. It maintains a reference to a 
 * ConcreteState object which defines its current behavior.
 * Rename to match your problem (e.g., ReturnRequest, VendingMachine, PatientSubscription).
 */
class Context {
    // Holds the current state of the object
    private State currentState;
    
    // Optional: Additional data the states might need to read or modify
    private String data; 

    public Context() {
        // Initialize with the starting state required by the problem
        this.currentState = new ConcreteStateA(); 
        this.data = "Initial Data";
    }

    /**
     * Allows states to transition the context to a new state.
     */
    public void setState(State newState) {
        this.currentState = newState;
    }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    // ---------------------------------------------------------
    // DELEGATED ACTIONS
    // The context does not implement the logic. It delegates to the current state!
    // Rename these methods to match your problem (e.g., approve(), cancel(), insertCoin()).
    // ---------------------------------------------------------
    
    public void doAction1() {
        currentState.handleAction1(this);
    }

    public void doAction2() {
        currentState.handleAction2(this);
    }
}

/**
 * 2. THE STATE INTERFACE
 * Declares the methods for all operations that change based on state.
 * Rename to match your problem (e.g., ReturnState, VendingState).
 */
interface State {
    
    /**
     * Using Java 8 'default' methods is a great trick for exams. 
     * It allows you to define a default "Invalid Operation" message for actions 
     * that aren't allowed in certain states, saving you from writing redundant code 
     * in every concrete state class.
     */
    default void handleAction1(Context context) {
        System.out.println("Invalid operation: Cannot perform Action 1 in the current state.");
    }

    default void handleAction2(Context context) {
        System.out.println("Invalid operation: Cannot perform Action 2 in the current state.");
    }
}

/**
 * 3. CONCRETE STATE A (The Starting State)
 * Implements behavior specific to this state and handles transitions.
 * Rename to match your problem (e.g., RequestedState, IdleState, CommonTier).
 */
class ConcreteStateA implements State {

    @Override
    public void handleAction1(Context context) {
        System.out.println("State A handling Action 1. Modifying data...");
        context.setData("Data modified by State A");
        
        // Transitioning to State B
        System.out.println("Transitioning from State A to State B.");
        context.setState(new ConcreteStateB());
    }
    
    // Notice we do NOT override handleAction2 here. 
    // If handleAction2 is called while in State A, it will print the default "Invalid" message.
}

/**
 * 4. CONCRETE STATE B
 * Another state the context can transition into.
 */
class ConcreteStateB implements State {

    @Override
    public void handleAction1(Context context) {
        System.out.println("State B handling Action 1 differently. No transition occurs.");
    }

    @Override
    public void handleAction2(Context context) {
        System.out.println("State B handling Action 2. Transitioning back to State A.");
        context.setState(new ConcreteStateA());
    }
}

/**
 * 5. CONCRETE STATE C (A Final/Terminal State)
 * Terminal states often don't override any actions, completely relying 
 * on the default interface methods to block all further operations.
 */
class FinalState implements State {
    // Inherits all default "Invalid operation" messages.
    // The object is permanently locked in this state.
}

/**
 * 6. CLIENT CODE (Testing the Pattern)
 */
public class StatePatternClient {
    public static void main(String[] args) {
        // 1. Create the Context (Starts in State A)
        Context context = new Context();

        System.out.println("--- Attempting actions in State A ---");
        context.doAction2(); // Should be invalid (State A doesn't override this)
        context.doAction1(); // Valid -> Modifies data and transitions to State B

        System.out.println("\n--- Attempting actions in State B ---");
        context.doAction1(); // Valid -> Does something, stays in State B
        context.doAction2(); // Valid -> Transitions back to State A
        
        System.out.println("\n--- Forcing a terminal state ---");
        context.setState(new FinalState());
        context.doAction1(); // Invalid (Terminal State)
        context.doAction2(); // Invalid (Terminal State)
    }
}

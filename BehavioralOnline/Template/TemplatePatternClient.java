/**
 * 1. THE ABSTRACT CLASS (The Blueprint)
 * This class defines the overall algorithm and provides common functionality.
 * Rename this to match your specific problem (e.g., DataParser, GameAI, HospitalVisit).
 */
abstract class AbstractAlgorithmTemplate {

    /**
     * THE TEMPLATE METHOD
     * This is the core of the pattern. It outlines the exact sequence of steps.
     * IMPORTANT: It is marked as 'final' so that subclasses CANNOT change the overall flow.
     */
    public final void executeAlgorithm() {
        // Step 1: A common step shared by all subclasses
        commonStep1();

        // Step 2: A custom step that subclasses MUST implement
        requiredCustomStep1();

        // Step 3: An optional hook that subclasses CAN override if they want to
        if (optionalHook()) {
            System.out.println("Executing optional logic because hook returned true.");
        }

        // Step 4: Another custom step
        requiredCustomStep2();
        
        // Step 5: Another common finishing step
        commonStep2();
    }

    // ---------------------------------------------------------
    // BASE METHODS (Common to all subclasses)
    // ---------------------------------------------------------
    private void commonStep1() {
        System.out.println("Executing common setup step.");
    }

    private void commonStep2() {
        System.out.println("Executing common teardown/cleanup step.\n");
    }

    // ---------------------------------------------------------
    // ABSTRACT METHODS (Must be implemented by subclasses)
    // ---------------------------------------------------------
    protected abstract void requiredCustomStep1();
    protected abstract void requiredCustomStep2();

    // ---------------------------------------------------------
    // HOOK METHODS (Optional to override)
    // ---------------------------------------------------------
    /**
     * Hooks provide a way for subclasses to inject logic or skip steps 
     * without breaking the template. They usually have empty bodies or return defaults.
     */
    protected boolean optionalHook() {
        return false; // Default behavior is to skip the optional logic
    }
}

/**
 * 2. CONCRETE SUBCLASS A
 * Implements the abstract methods to provide specific behavior.
 * Rename to your specific variant (e.g., CSVParser, AggressiveGameAI, EmergencyDeptVisit).
 */
class ConcreteImplementationA extends AbstractAlgorithmTemplate {

    @Override
    protected void requiredCustomStep1() {
        System.out.println("Implementation A: Doing custom step 1 in a specific way.");
    }

    @Override
    protected void requiredCustomStep2() {
        System.out.println("Implementation A: Doing custom step 2 in a specific way.");
    }

    // Overriding the hook to change the default flow
    @Override
    protected boolean optionalHook() {
        return true; 
    }
}

/**
 * 3. CONCRETE SUBCLASS B
 * Another variant with completely different internal step implementations.
 */
class ConcreteImplementationB extends AbstractAlgorithmTemplate {

    @Override
    protected void requiredCustomStep1() {
        System.out.println("Implementation B: Doing custom step 1 in a TOTALLY different way.");
    }

    @Override
    protected void requiredCustomStep2() {
        System.out.println("Implementation B: Doing custom step 2 in a TOTALLY different way.");
    }
    
    // Notice: We don't override the hook here, so it uses the default 'false' behavior.
}

/**
 * 4. CLIENT CODE (Testing the Template)
 */
public class TemplatePatternClient {
    public static void main(String[] args) {
        
        System.out.println("--- Running Implementation A ---");
        AbstractAlgorithmTemplate variantA = new ConcreteImplementationA();
        variantA.executeAlgorithm();

        System.out.println("--- Running Implementation B ---");
        AbstractAlgorithmTemplate variantB = new ConcreteImplementationB();
        variantB.executeAlgorithm();
    }
}
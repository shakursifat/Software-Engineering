import java.util.ArrayList;
import java.util.List;

// ==============================================================================
// PART 1: THE BRIDGE PATTERN (IMPLEMENTOR HIERARCHY)
// ==============================================================================
// This section defines the interchangeable behaviors or platform-specific
// implementations that the hierarchical components will use.
// ==============================================================================

/**
 * The Implementor interface.
 * This defines the primitive operations that the Abstraction/Components will rely on.
 * It is completely decoupled from the tree structure.
 */
interface IImplementor {
    void executePrimitiveOperation(String nodeName);
    Object calculateMetric(double baseValue);
}

/**
 * Concrete Implementor A (e.g., Windows OS, Standard Pricing, OpenGL Rendering)
 */
class ConcreteImplementorA implements IImplementor {
    @Override
    public void executePrimitiveOperation(String nodeName) {
        System.out.println("Implementation A executing for: " + nodeName);
    }

    @Override
    public Object calculateMetric(double baseValue) {
        return baseValue * 1.0; // Example metric calculation
    }
}

/**
 * Concrete Implementor B (e.g., MacOS, Premium Pricing, DirectX Rendering)
 */
class ConcreteImplementorB implements IImplementor {
    @Override
    public void executePrimitiveOperation(String nodeName) {
        System.out.println("Implementation B executing for: " + nodeName);
    }

    @Override
    public Object calculateMetric(double baseValue) {
        return baseValue * 1.5; // Different metric calculation
    }
}

// ==============================================================================
// PART 2: THE COMPOSITE & BRIDGE PATTERN (ABSTRACTION & COMPONENT HIERARCHY)
// ==============================================================================
// This section defines the tree structure. The base component holds the 
// reference to the Implementor, effectively merging the "Component" role of 
// the Composite pattern with the "Abstraction" role of the Bridge pattern.
// ==============================================================================

/**
 * The Component (Composite) AND Abstraction (Bridge).
 * 
 * - As a Component: It declares the common interface for leaves and composites.
 * - As an Abstraction: It holds a reference to the IImplementor and delegates work to it.
 */
abstract class CoreComponent {
    protected String name;
    
    // THE BRIDGE: Every component in the tree has access to an implementor.
    protected IImplementor implementor;

    /**
     * Constructor injects the implementor, establishing the bridge.
     */
    public CoreComponent(String name, IImplementor implementor) {
        this.name = name;
        this.implementor = implementor;
    }

    /**
     * Standard component operation that will rely on the bridged implementor.
     */
    public abstract void performOperation();

    /**
     * A metric calculation relying on the bridged implementor.
     */
    public abstract double getTotalMetric();

    // --------------------------------------------------------------------------
    // Composite Management Methods
    // Placed at the base level to provide transparency, but default to throwing 
    // exceptions so Leaf nodes don't have to implement meaningless logic.
    // --------------------------------------------------------------------------
    public void add(CoreComponent component) {
        throw new UnsupportedOperationException("Cannot add to a leaf component.");
    }

    public void remove(CoreComponent component) {
        throw new UnsupportedOperationException("Cannot remove from a leaf component.");
    }
    
    public CoreComponent getChild(int index) {
        throw new UnsupportedOperationException("Leaf components do not have children.");
    }
}

// ==============================================================================
// PART 3: THE LEAF NODES (END-POINTS OF THE TREE)
// ==============================================================================

/**
 * The Leaf represents individual objects that do not have children.
 * It uses the inherited Implementor to perform its specific tasks.
 */
class LeafNode extends CoreComponent {
    private double baseMetricValue;

    public LeafNode(String name, IImplementor implementor, double baseMetricValue) {
        super(name, implementor);
        this.baseMetricValue = baseMetricValue;
    }

    @Override
    public void performOperation() {
        // Delegate the primitive work to the bridged implementor
        implementor.executePrimitiveOperation("Leaf [" + name + "]");
    }

    @Override
    public double getTotalMetric() {
        // Calculate metric based on the specific implementor's rules
        return (Double) implementor.calculateMetric(baseMetricValue);
    }
}

// ==============================================================================
// PART 4: THE COMPOSITE NODES (BRANCHES OF THE TREE)
// ==============================================================================

/**
 * The Composite container represents a node that has children.
 * It iterates through its children to execute operations, and also uses 
 * its own bridged implementor for container-level logic.
 */
class CompositeContainer extends CoreComponent {
    // Stores the child components (which can be either Leaves or other Composites)
    private List<CoreComponent> children = new ArrayList<>();
    private double containerOverheadMetric;

    public CompositeContainer(String name, IImplementor implementor, double containerOverheadMetric) {
        super(name, implementor);
        this.containerOverheadMetric = containerOverheadMetric;
    }

    // Overriding the default structural methods
    @Override
    public void add(CoreComponent component) {
        children.add(component);
    }

    @Override
    public void remove(CoreComponent component) {
        children.remove(component);
    }

    @Override
    public CoreComponent getChild(int index) {
        return children.get(index);
    }

    @Override
    public void performOperation() {
        // 1. Container-level execution via the Bridge
        implementor.executePrimitiveOperation("Composite Container [" + name + "]");
        
        // 2. Cascade the operation down the Composite tree
        for (CoreComponent child : children) {
            child.performOperation();
        }
    }

    @Override
    public double getTotalMetric() {
        // 1. Calculate container's own overhead via the Bridge
        double total = (Double) implementor.calculateMetric(containerOverheadMetric);
        
        // 2. Aggregate the metrics from all children in the Composite tree
        for (CoreComponent child : children) {
            total += child.getTotalMetric();
        }
        
        return total;
    }
}

// ==============================================================================
// PART 5: CLIENT USAGE (ORCHESTRATION)
// ==============================================================================

public class BridgeCompositeTemplateDemo {
    public static void main(String[] args) {
        
        // 1. Instantiate the specific implementations (The Bridge targets)
        IImplementor implA = new ConcreteImplementorA();
        IImplementor implB = new ConcreteImplementorB();

        // 2. Build the Tree Structure using the selected implementor
        // Let's build a tree that strictly uses Implementor A
        CoreComponent rootA = new CompositeContainer("Root-A", implA, 10.0);
        
        CoreComponent branch1 = new CompositeContainer("Branch-1", implA, 5.0);
        branch1.add(new LeafNode("Leaf-1.1", implA, 2.0));
        branch1.add(new LeafNode("Leaf-1.2", implA, 3.0));

        CoreComponent leaf2 = new LeafNode("Leaf-2", implA, 8.0);

        rootA.add(branch1);
        rootA.add(leaf2);

        // Execute operations across the whole tree seamlessly
        System.out.println("--- Executing Operations (Implementation A Tree) ---");
        rootA.performOperation();
        
        System.out.println("\nTotal Metric (Implementation A Tree): " + rootA.getTotalMetric());
        
        System.out.println("\n----------------------------------------------------\n");

        // 3. Independent Variation
        // We can create an identical tree structure but switch the underlying 
        // behavior to Implementor B without altering any logic in the Leaf/Composite classes.
        CoreComponent rootB = new CompositeContainer("Root-B", implB, 10.0);
        rootB.add(new LeafNode("Leaf-1.1", implB, 2.0)); // Skipping the branch for brevity
        
        System.out.println("--- Executing Operations (Implementation B Tree) ---");
        rootB.performOperation();
        System.out.println("\nTotal Metric (Implementation B Tree): " + rootB.getTotalMetric());
    }
}

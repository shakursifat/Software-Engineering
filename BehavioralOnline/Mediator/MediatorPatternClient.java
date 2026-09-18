/**
 * 1. THE MEDIATOR INTERFACE
 * Declares the communication method that colleagues will use to send events.
 * Rename to match your problem (e.g., SmartHomeHub, ChatRoom, AirTrafficControl).
 */
interface Mediator {
    /**
     * @param sender The colleague that is sending the message/event.
     * @param event  The payload or action name (can be a String, Enum, or Object).
     */
    void notify(Colleague sender, String event);
}

/**
 * 2. THE ABSTRACT COLLEAGUE (Base Component)
 * All communicating components must extend this class so they share a reference 
 * to the Mediator. They NEVER hold references to each other.
 * Rename to match your problem (e.g., SmartDevice, User, Airplane).
 */
abstract class Colleague {
    protected Mediator mediator;

    public Colleague(Mediator mediator) {
        this.mediator = mediator;
    }
}

/**
 * 3. CONCRETE COLLEAGUE A
 * A specific component that performs its own tasks and notifies the mediator 
 * when something important happens.
 * Rename to your specific variant (e.g., LightSensor, Doctor, Runway).
 */
class ConcreteColleagueA extends Colleague {
    
    public ConcreteColleagueA(Mediator mediator) {
        super(mediator);
    }

    public void doActionA() {
        System.out.println("Colleague A: Performing my specific task.");
        // Instead of calling Colleague B directly, tell the mediator!
        mediator.notify(this, "Event_A_Completed");
    }

    public void reactOnMediatorCommand() {
        System.out.println("Colleague A: Reacting to a command routed by the mediator.");
    }
}

/**
 * 4. CONCRETE COLLEAGUE B
 * Another specific component in the system.
 */
class ConcreteColleagueB extends Colleague {

    public ConcreteColleagueB(Mediator mediator) {
        super(mediator);
    }

    public void doActionB() {
        System.out.println("Colleague B: Performing my specific task.");
        mediator.notify(this, "Event_B_Completed");
    }
}

/**
 * 5. CONCRETE MEDIATOR
 * The central hub that contains all the routing and coordination logic.
 * It knows about all colleagues and decides what happens when an event occurs.
 */
class ConcreteMediator implements Mediator {
    // The mediator holds references to all the specific colleagues
    private ConcreteColleagueA colleagueA;
    private ConcreteColleagueB colleagueB;

    // Registration methods (Setup)
    public void setColleagueA(ConcreteColleagueA colleagueA) {
        this.colleagueA = colleagueA;
    }

    public void setColleagueB(ConcreteColleagueB colleagueB) {
        this.colleagueB = colleagueB;
    }

    /**
     * THE ROUTING LOGIC
     * This is where you write the rules of the system (the "If X happens, do Y" logic).
     */
    @Override
    public void notify(Colleague sender, String event) {
        
        // Rule 1: If Colleague A finishes its task, trigger Colleague B
        if (sender == colleagueA && event.equals("Event_A_Completed")) {
            System.out.println("[Mediator] Detected Event A. Routing command to Colleague B...");
            colleagueB.doActionB();
        }
        
        // Rule 2: If Colleague B finishes its task, trigger Colleague A's reaction
        else if (sender == colleagueB && event.equals("Event_B_Completed")) {
            System.out.println("[Mediator] Detected Event B. Routing command to Colleague A...");
            colleagueA.reactOnMediatorCommand();
        }
    }
}

/**
 * 6. CLIENT CODE (Testing the Pattern)
 */
public class MediatorPatternClient {
    public static void main(String[] args) {
        // 1. Create the Mediator
        ConcreteMediator mediator = new ConcreteMediator();

        // 2. Create the Colleagues, passing the Mediator into their constructors
        ConcreteColleagueA compA = new ConcreteColleagueA(mediator);
        ConcreteColleagueB compB = new ConcreteColleagueB(mediator);

        // 3. Register the Colleagues with the Mediator
        mediator.setColleagueA(compA);
        mediator.setColleagueB(compB);

        System.out.println("--- Starting Interaction ---");
        
        // 4. Trigger an action on one component. 
        // The Mediator will automatically handle the chain reaction.
        compA.doActionA();
    }
}

import java.util.ArrayList;
import java.util.List;

/**
 * 1. THE OBSERVER INTERFACE (The Subscriber / Listener)
 * All objects that want to be notified of changes must implement this interface.
 * Rename to match your problem (e.g., TraderWidget, Department, MobileApp).
 */
interface Observer {
    /**
     * The method called by the Subject when a change occurs.
     * UPDATE THIS PARAMETER: Change 'String eventData' to whatever data 
     * needs to be passed (e.g., 'double newPrice', 'Message messageObj').
     */
    void update(String eventData);
}

/**
 * 2. THE SUBJECT INTERFACE (The Publisher / Broadcaster)
 * Declares the standard methods for managing and notifying observers.
 * Rename to match your problem (e.g., StockFeed, WeatherStation, MessageBoard).
 */
interface Subject {
    void subscribe(Observer observer);    // Also commonly called attach() or register()
    void unsubscribe(Observer observer);  // Also commonly called detach() or remove()
    void notifyObservers();
}

/**
 * 3. CONCRETE SUBJECT
 * Maintains the list of observers and the core state/data.
 * When its state changes, it alerts everyone on the list.
 */
class ConcreteSubject implements Subject {
    // List to keep track of all subscribed observers
    private List<Observer> observers;
    
    // The state or data that changes and triggers notifications
    private String currentState;

    public ConcreteSubject() {
        this.observers = new ArrayList<>();
    }

    @Override
    public void subscribe(Observer observer) {
        observers.add(observer);
        System.out.println("System: A new observer has subscribed.");
    }

    @Override
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
        System.out.println("System: An observer has unsubscribed.");
    }

    @Override
    public void notifyObservers() {
        // Loop through all subscribers and send them the updated data
        for (Observer observer : observers) {
            observer.update(currentState);
        }
    }

    /**
     * Custom method to change the state.
     * When the state changes, it automatically triggers notifyObservers().
     */
    public void changeState(String newState) {
        System.out.println("\n--- Subject state changing to: " + newState + " ---");
        this.currentState = newState;
        
        // CRITICAL STEP: Notify everyone that the state has changed!
        notifyObservers();
    }
}

/**
 * 4. CONCRETE OBSERVER A
 * A specific class that reacts to the Subject's updates.
 * Rename to your specific variant (e.g., GraphWidget, Scouts, EmailNotifier).
 */
class ConcreteObserverA implements Observer {
    private String name;

    public ConcreteObserverA(String name) {
        this.name = name;
    }

    @Override
    public void update(String eventData) {
        // Custom logic for how THIS specific observer handles the new data
        System.out.println("Observer [" + name + "] received update. Data: " + eventData);
        System.out.println(" -> Taking Action A based on new data.");
    }
}

/**
 * 5. CONCRETE OBSERVER B
 * Another variant that reacts differently to the exact same update.
 */
class ConcreteObserverB implements Observer {
    @Override
    public void update(String eventData) {
        // Custom logic for how THIS specific observer handles the new data
        System.out.println("Observer [Type B] received update. Data: " + eventData);
        System.out.println(" -> Taking Action B (doing something totally different).");
    }
}

/**
 * 6. CLIENT CODE (Testing the Pattern)
 */
public class ObserverPatternClient {
    public static void main(String[] args) {
        // 1. Create the Subject (Publisher)
        ConcreteSubject publisher = new ConcreteSubject();

        // 2. Create the Observers (Subscribers)
        Observer observer1 = new ConcreteObserverA("Module 1");
        Observer observer2 = new ConcreteObserverA("Module 2");
        Observer observer3 = new ConcreteObserverB();

        // 3. Register observers with the Subject
        publisher.subscribe(observer1);
        publisher.subscribe(observer2);
        publisher.subscribe(observer3);

        // 4. Trigger an event / state change
        // This will automatically notify all 3 registered observers
        publisher.changeState("Event Trigger 101");

        System.out.println("\n--- Removing Module 1 ---");
        // 5. Unsubscribe an observer at runtime
        publisher.unsubscribe(observer1);

        // 6. Trigger another event
        // Only observer2 and observer3 will receive this one
        publisher.changeState("Event Trigger 102");
    }
}

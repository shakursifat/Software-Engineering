import java.util.*;

/**
 * Task 1: Disaster Alert Notification System
 * Pattern: Observer Design Pattern
 * 
 * BD Alert - A government disaster management system that publishes emergency alerts
 * for earthquakes, floods, and fires. Citizens may subscribe to one or more alert
 * categories and get notified automatically whenever an alert is published.
 */

// ─────────────────────────────────────────────────────────────────────────────
// Enum: AlertCategory
// ─────────────────────────────────────────────────────────────────────────────

enum AlertCategory {
    EARTHQUAKE, FLOOD, FIRE
}

// ─────────────────────────────────────────────────────────────────────────────
// Data Class: Alert
// Represents an emergency alert with all required fields.
// ─────────────────────────────────────────────────────────────────────────────

class Alert {
    private final String title;
    private final AlertCategory category;
    private final String affectedLocation;
    private final String severityLevel;
    private final String safetyInstructions;

    public Alert(String title, AlertCategory category, String affectedLocation,
                 String severityLevel, String safetyInstructions) {
        this.title = title;
        this.category = category;
        this.affectedLocation = affectedLocation;
        this.severityLevel = severityLevel;
        this.safetyInstructions = safetyInstructions;
    }

    public String getTitle()             { return title; }
    public AlertCategory getCategory()   { return category; }
    public String getAffectedLocation()  { return affectedLocation; }
    public String getSeverityLevel()     { return severityLevel; }
    public String getSafetyInstructions(){ return safetyInstructions; }

    @Override
    public String toString() {
        return "[" + category + " ALERT] " + title
                + "\n  Location  : " + affectedLocation
                + "\n  Severity  : " + severityLevel
                + "\n  Safety    : " + safetyInstructions;
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Interface: Observer
// All concrete observers (citizens) must implement this interface.
// ─────────────────────────────────────────────────────────────────────────────

interface Observer {
    void update(Alert alert);
    String getName();
}

// ─────────────────────────────────────────────────────────────────────────────
// Interface: Subject
// Defines the contract for the observable alert system.
// ─────────────────────────────────────────────────────────────────────────────

interface Subject {
    void subscribe(AlertCategory category, Observer observer);
    void unsubscribe(AlertCategory category, Observer observer);
    void publish(Alert alert);
}

// ─────────────────────────────────────────────────────────────────────────────
// Concrete Subject: BDAlertSystem
// Maintains per-category subscriber lists and dispatches alerts only to
// citizens subscribed to the relevant category.
// ─────────────────────────────────────────────────────────────────────────────

class BDAlertSystem implements Subject {

    // Map from category → list of subscribed observers
    private final Map<AlertCategory, List<Observer>> subscribers = new HashMap<>();

    // Registered citizens (for display / management purposes)
    private final Set<String> registeredCitizens = new LinkedHashSet<>();

    public BDAlertSystem() {
        // Initialise lists for every category
        for (AlertCategory cat : AlertCategory.values()) {
            subscribers.put(cat, new ArrayList<>());
        }
    }

    // Register a citizen name in the system
    public void registerCitizen(Observer citizen) {
        registeredCitizens.add(citizen.getName());
        System.out.println(">> Citizen registered: " + citizen.getName());
    }

    @Override
    public void subscribe(AlertCategory category, Observer observer) {
        List<Observer> list = subscribers.get(category);
        if (!list.contains(observer)) {
            list.add(observer);
            System.out.println(">> " + observer.getName()
                    + " subscribed to " + category + " alerts.");
        } else {
            System.out.println(">> " + observer.getName()
                    + " is already subscribed to " + category + " alerts.");
        }
    }

    @Override
    public void unsubscribe(AlertCategory category, Observer observer) {
        List<Observer> list = subscribers.get(category);
        if (list.remove(observer)) {
            System.out.println(">> " + observer.getName()
                    + " unsubscribed from " + category + " alerts.");
        } else {
            System.out.println(">> " + observer.getName()
                    + " was not subscribed to " + category + " alerts.");
        }
    }

    @Override
    public void publish(Alert alert) {
        System.out.println("\n========================================");
        System.out.println("  PUBLISHING ALERT: " + alert.getTitle());
        System.out.println("========================================");
        System.out.println(alert);
        System.out.println("----------------------------------------");

        List<Observer> list = subscribers.get(alert.getCategory());
        if (list.isEmpty()) {
            System.out.println("  No citizens subscribed to "
                    + alert.getCategory() + " alerts.");
        } else {
            System.out.println("  Notifying " + list.size() + " subscriber(s)...");
            // Notify every subscribed citizen
            for (Observer o : list) {
                o.update(alert);
            }
        }
        System.out.println("========================================\n");
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Concrete Observer: Citizen
// Receives and stores notifications from the alert system.
// ─────────────────────────────────────────────────────────────────────────────

class Citizen implements Observer {
    private final String name;
    private final List<Alert> receivedNotifications = new ArrayList<>();

    public Citizen(String name) {
        this.name = name;
    }

    @Override
    public String getName() { return name; }

    @Override
    public void update(Alert alert) {
        receivedNotifications.add(alert);
        System.out.println("  [NOTIFIED] " + name + " received: " + alert.getTitle());
    }

    // Display all notifications received so far
    public void displayNotifications() {
        System.out.println("\n------ Notifications for " + name + " ------");
        if (receivedNotifications.isEmpty()) {
            System.out.println("  (No notifications received yet.)");
        } else {
            for (int i = 0; i < receivedNotifications.size(); i++) {
                System.out.println("  Notification #" + (i + 1) + ":");
                System.out.println("  " + receivedNotifications.get(i).toString()
                        .replace("\n", "\n  "));
                System.out.println();
            }
        }
        System.out.println("--------------------------------------");
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main Driver Class
// Demonstrates the complete BD Alert system as required by the specification.
// ─────────────────────────────────────────────────────────────────────────────

public class DisasterAlertSystem {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   BD Alert — Disaster Notification System ║");
        System.out.println("║       Observer Design Pattern Demo        ║");
        System.out.println("╚══════════════════════════════════════════╝\n");

        // ── Step 1: Create the alert system (subject) ──────────────────────
        BDAlertSystem bdAlert = new BDAlertSystem();

        // ── Step 2: Create and register citizens ───────────────────────────
        System.out.println("=== Registering Citizens ===");
        Citizen rahim  = new Citizen("Rahim");
        Citizen karim  = new Citizen("Karim");
        Citizen fatima = new Citizen("Fatima");
        Citizen nadia  = new Citizen("Nadia");

        bdAlert.registerCitizen(rahim);
        bdAlert.registerCitizen(karim);
        bdAlert.registerCitizen(fatima);
        bdAlert.registerCitizen(nadia);

        // ── Step 3: Subscribe citizens to different categories ─────────────
        System.out.println("\n=== Setting Up Subscriptions ===");
        // Rahim: EARTHQUAKE + FLOOD
        bdAlert.subscribe(AlertCategory.EARTHQUAKE, rahim);
        bdAlert.subscribe(AlertCategory.FLOOD, rahim);

        // Karim: FIRE only
        bdAlert.subscribe(AlertCategory.FIRE, karim);

        // Fatima: all three
        bdAlert.subscribe(AlertCategory.EARTHQUAKE, fatima);
        bdAlert.subscribe(AlertCategory.FLOOD, fatima);
        bdAlert.subscribe(AlertCategory.FIRE, fatima);

        // Nadia: EARTHQUAKE only
        bdAlert.subscribe(AlertCategory.EARTHQUAKE, nadia);

        // ── Step 4: Publish one alert per category ─────────────────────────
        System.out.println("\n=== Publishing Initial Alerts ===");

        Alert earthquakeAlert = new Alert(
                "Strong Earthquake Detected",
                AlertCategory.EARTHQUAKE,
                "Sylhet Division",
                "HIGH",
                "Move away from buildings. Drop, Cover, and Hold On. " +
                "Avoid elevators. Stay indoors until shaking stops."
        );
        bdAlert.publish(earthquakeAlert);

        Alert floodAlert = new Alert(
                "Flash Flood Warning",
                AlertCategory.FLOOD,
                "Coastal Chittagong",
                "CRITICAL",
                "Move to higher ground immediately. Do not attempt to " +
                "wade through flood water. Follow evacuation routes."
        );
        bdAlert.publish(floodAlert);

        Alert fireAlert = new Alert(
                "Industrial Fire Outbreak",
                AlertCategory.FIRE,
                "Dhaka BSCIC Industrial Zone",
                "MEDIUM",
                "Evacuate the area. Do not re-enter buildings. " +
                "Call 999 for emergency services."
        );
        bdAlert.publish(fireAlert);

        // ── Step 5: Update subscriptions ───────────────────────────────────
        System.out.println("\n=== Updating Subscriptions ===");

        // Karim now also wants FLOOD alerts
        bdAlert.subscribe(AlertCategory.FLOOD, karim);

        // Nadia unsubscribes from EARTHQUAKE
        bdAlert.unsubscribe(AlertCategory.EARTHQUAKE, nadia);

        // Nadia subscribes to FIRE
        bdAlert.subscribe(AlertCategory.FIRE, nadia);

        // ── Step 6: Publish another alert to verify subscription changes ───
        System.out.println("\n=== Publishing Alerts After Subscription Changes ===");

        Alert earthquake2 = new Alert(
                "Aftershock — Secondary Earthquake",
                AlertCategory.EARTHQUAKE,
                "Sylhet Division",
                "MODERATE",
                "Stay alert for aftershocks. Check for gas leaks. " +
                "Do not use open flame until area is declared safe."
        );
        bdAlert.publish(earthquake2);
        // Note: Nadia unsubscribed from EARTHQUAKE, so she should NOT be notified.

        Alert flood2 = new Alert(
                "River Overflow Alert",
                AlertCategory.FLOOD,
                "Sunamganj District",
                "HIGH",
                "Prepare emergency kits. Avoid low-lying areas. " +
                "Keep livestock on higher ground."
        );
        bdAlert.publish(flood2);
        // Note: Karim just subscribed to FLOOD, so he SHOULD be notified.

        Alert fire2 = new Alert(
                "Residential Fire Hazard",
                AlertCategory.FIRE,
                "Mirpur, Dhaka",
                "HIGH",
                "Evacuate the building. Call 999 immediately. " +
                "Use stairs, not elevators. Meet at assembly point."
        );
        bdAlert.publish(fire2);
        // Note: Nadia just subscribed to FIRE, so she SHOULD be notified.

        // ── Step 7: Display notifications received by each citizen ─────────
        System.out.println("\n=== Notification Summary for All Citizens ===");
        rahim.displayNotifications();
        karim.displayNotifications();
        fatima.displayNotifications();
        nadia.displayNotifications();

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║          Demo Completed Successfully      ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
}

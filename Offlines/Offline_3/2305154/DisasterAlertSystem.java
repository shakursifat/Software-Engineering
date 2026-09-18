import java.util.*;

enum AlertCategory {
    EARTHQUAKE, FLOOD, FIRE
}


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

interface Observer {
    void update(Alert alert);
    String getName();
}

interface Subject {
    void subscribe(AlertCategory category, Observer observer);
    void unsubscribe(AlertCategory category, Observer observer);
    void publish(Alert alert);
}


class BDAlertSystem implements Subject {
    // Map from category , list of subscribed observers
    private final Map<AlertCategory, List<Observer>> subscribers = new HashMap<>();

    // Registered citizens
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
        if (!registeredCitizens.contains(observer.getName())) {
            System.out.println(">> ERROR: " + observer.getName() + " cannot subscribe. They are not registered in the system.");
            return;
        }

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
        System.out.println("\nPUBLISHING ALERT: " + alert.getTitle());
        System.out.println(alert);

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
    }
}


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

    // Display all notifications received
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
    }
}


public class DisasterAlertSystem {

    public static void main(String[] args) {

        System.out.println("BD Alert — Disaster Notification System \n");

        BDAlertSystem bdAlert = new BDAlertSystem();

        System.out.println("=== Registering Citizens ===");
        Citizen saleh  = new Citizen("Saleh");
        Citizen wasik  = new Citizen("Wasik");
        Citizen ananta = new Citizen("Ananta");
        Citizen shafnan  = new Citizen("Shafnan");

        Citizen unregisteredCitizen = new Citizen("Farhan");

        bdAlert.registerCitizen(saleh);
        bdAlert.registerCitizen(wasik);
        bdAlert.registerCitizen(ananta);
        bdAlert.registerCitizen(shafnan);

        System.out.println("\n=== Setting Up Subscriptions ===");

        bdAlert.subscribe(AlertCategory.EARTHQUAKE, unregisteredCitizen);

        // saleh: EARTHQUAKE + FLOOD
        bdAlert.subscribe(AlertCategory.EARTHQUAKE, saleh);
        bdAlert.subscribe(AlertCategory.FLOOD, saleh);

        // wasik: FIRE only
        bdAlert.subscribe(AlertCategory.FIRE, wasik);

        // ananta: all three
        bdAlert.subscribe(AlertCategory.EARTHQUAKE, ananta);
        bdAlert.subscribe(AlertCategory.FLOOD, ananta);
        bdAlert.subscribe(AlertCategory.FIRE, ananta);

        // shafnan: EARTHQUAKE only
        bdAlert.subscribe(AlertCategory.EARTHQUAKE, shafnan);

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

        System.out.println("\n=== Updating Subscriptions ===");

        // wasik now also wants FLOOD alerts
        bdAlert.subscribe(AlertCategory.FLOOD, wasik);

        // shafnan unsubscribes from EARTHQUAKE
        bdAlert.unsubscribe(AlertCategory.EARTHQUAKE, shafnan);

        // shafnan subscribes to FIRE
        bdAlert.subscribe(AlertCategory.FIRE, shafnan);

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

        Alert flood2 = new Alert(
                "River Overflow Alert",
                AlertCategory.FLOOD,
                "Sunamganj District",
                "HIGH",
                "Prepare emergency kits. Avoid low-lying areas. " +
                "Keep livestock on higher ground."
        );
        bdAlert.publish(flood2);

        Alert fire2 = new Alert(
                "Residential Fire Hazard",
                AlertCategory.FIRE,
                "Mirpur, Dhaka",
                "HIGH",
                "Evacuate the building. Call 999 immediately. " +
                "Use stairs, not elevators. Meet at assembly point."
        );
        bdAlert.publish(fire2);

        System.out.println("\n=== Notification Summary for All Citizens ===");
        saleh.displayNotifications();
        wasik.displayNotifications();
        ananta.displayNotifications();
        shafnan.displayNotifications();

        unregisteredCitizen.displayNotifications();

        System.out.println("\n Demo Completed Successfully");
    }
}

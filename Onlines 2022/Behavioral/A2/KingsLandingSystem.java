import java.util.ArrayList;
import java.util.List;

// --- OBSERVER INTERFACE ---
interface Group {
    void update(String message);
    String getName();
}

// --- SUBJECT INTERFACE ---
interface MessageBoard {
    void subscribe(Group group);
    void unsubscribe(Group group);
    void notifyGroups(String message);
}

// --- CONCRETE SUBJECT ---
class RavenBoard implements MessageBoard {
    private List<Group> activeGroups;

    public RavenBoard() {
        this.activeGroups = new ArrayList<>();
    }

    @Override
    public void subscribe(Group group) {
        activeGroups.add(group);
        System.out.println(group.getName() + " has subscribed to the RavenBoard.");
    }

    @Override
    public void unsubscribe(Group group) {
        activeGroups.remove(group);
        System.out.println(group.getName() + " has left the board room (unsubscribed).");
    }

    @Override
    public void notifyGroups(String message) {
        System.out.println("\n[RAVEN ARRIVED] Scroll says: \"" + message + "\"");
        for (Group group : activeGroups) {
            group.update(message);
        }
    }

    // Helper method to simulate receiving a scroll
    public void receiveScroll(String message) {
        notifyGroups(message);
    }
}

// --- CONCRETE OBSERVERS ---
class Scouts implements Group {
    @Override
    public void update(String message) {
        System.out.print("[Scouts] read the message. ");
        if (message.toLowerCase().contains("enemy")) {
            System.out.println("Action: Dispatch riders!"); // Scouts act based on the message
        } else {
            System.out.println("Action: Keep watch.");
        }
    }

    @Override
    public String getName() { return "Scouts"; }
}

class SupplyTeam implements Group {
    @Override
    public void update(String message) {
        System.out.print("[Supply Team] read the message. ");
        if (message.toLowerCase().contains("winter supplies")) {
            System.out.println("Action: Update inventory!"); // Supply Team acts based on the message[cite: 5]
        } else {
            System.out.println("Action: Continue current rationing.");
        }
    }

    @Override
    public String getName() { return "Supply Team"; }
}

class Commander implements Group {
    @Override
    public void update(String message) {
        System.out.print("[Commander] read the message. ");
        if (message.toLowerCase().contains("ships")) {
            System.out.println("Action: Prepare the coastal defenses!"); 
        } else {
            System.out.println("Action: Hold the line.");
        }
    }

    @Override
    public String getName() { return "Commander"; }
}

// --- DEMONSTRATION ---
public class KingsLandingSystem {
    public static void main(String[] args) {
        // 1. Create the Subject (RavenBoard)
        RavenBoard board = new RavenBoard();

        // 2. Create the Observers (Groups)[cite: 5]
        Group scouts = new Scouts();
        Group supplyTeam = new SupplyTeam();
        Group commander = new Commander();

        System.out.println("--- Subscribing Groups ---");
        // Groups subscribe at runtime[cite: 5]
        board.subscribe(scouts);
        board.subscribe(supplyTeam);
        board.subscribe(commander);

        System.out.println("\n--- Processing Messages ---");
        
        // Message 1[cite: 5]
        board.receiveScroll("Enemy spotted near the river");
        
        // Message 2[cite: 5]
        board.receiveScroll("Winter supplies running low");

        System.out.println("\n--- Unsubscribing a Group ---");
        // Scouts leaving the board room[cite: 5]
        board.unsubscribe(scouts);

        System.out.println("\n--- Processing Final Message ---");
        // Message 3[cite: 5]
        board.receiveScroll("Ships seen in the east");
    }
}

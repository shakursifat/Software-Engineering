// --- CONTEXT ---
class PatientSubscription {
    private SubscriptionTier currentTier;

    public PatientSubscription() {
        // Starts with the base tier
        this.currentTier = new CommonTier();
    }

    public void setTier(SubscriptionTier tier) {
        this.currentTier = tier;
    }

    public SubscriptionTier getTier() {
        return currentTier;
    }

    public void travelCheck(int km) {
        currentTier.travelCheck(this, km);
    }

    public void promote() {
        currentTier.promote(this);
    }

    public void demote() {
        currentTier.demote(this);
    }

    public void setMood(String mood) {
        currentTier.setMood(mood);
    }

    /**
     * Temporarily switches service to Lux, sleeps, and returns to the prior tier.
     */
    public void activateLux(int hours) {
        System.out.println("\n--- Activating Lux temporarily for " + hours + " hour(s) ---");
        SubscriptionTier previousTier = this.currentTier;
        this.currentTier = new LuxTier();
        
        try {
            // Simulating hours as milliseconds for demonstration purposes
            Thread.sleep(hours * 100L); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        this.currentTier = previousTier;
        System.out.println("--- Lux duration ended. Returned to " + currentTier.getName() + " tier. ---\n");
    }
}

// --- STATE INTERFACE ---
interface SubscriptionTier {
    void travelCheck(PatientSubscription context, int km);
    void setMood(String mood);
    void promote(PatientSubscription context);
    void demote(PatientSubscription context);
    String getName();

    // Reusable distance evaluation logic
    default void evaluateDistance(PatientSubscription context, int km, int safeRange) {
        if (km <= safeRange) {
            System.out.println("Patient is STABLE at " + km + " km under " + getName() + " tier.");
        } else {
            System.out.println("Patient is UNSTABLE (blacks out) at " + km + " km under " + getName() + " tier!");
            System.out.println("ALERT: Bring the patient back into coverage immediately!");
            // Simulation continues by calling travelCheck(0km)
            context.travelCheck(0); 
            System.out.println("Patient has regained consciousness.\n");
        }
    }
}

// --- CONCRETE STATES ---
class CommonTier implements SubscriptionTier {
    @Override
    public void travelCheck(PatientSubscription context, int km) {
        evaluateDistance(context, km, 10); // Safe range is 0-10 km[cite: 5]
    }

    @Override
    public void setMood(String mood) {
        System.out.println("Mood control unavailable."); // Only works while Lux is active[cite: 5]
    }

    @Override
    public void promote(PatientSubscription context) {
        System.out.println("Promoting from Common to Plus.");
        context.setTier(new PlusTier());
    }

    @Override
    public void demote(PatientSubscription context) {
        System.out.println("Demote called: Common to Common (no change).");
    }

    @Override
    public String getName() { return "Common"; }
}

class PlusTier implements SubscriptionTier {
    @Override
    public void travelCheck(PatientSubscription context, int km) {
        evaluateDistance(context, km, 50); // Safe range is 0-50 km[cite: 5]
    }

    @Override
    public void setMood(String mood) {
        System.out.println("Mood control unavailable."); // Only works while Lux is active[cite: 5]
    }

    @Override
    public void promote(PatientSubscription context) {
        System.out.println("Promoting from Plus to Lux.");
        context.setTier(new LuxTier());
    }

    @Override
    public void demote(PatientSubscription context) {
        System.out.println("Demoting from Plus to Common.");
        context.setTier(new CommonTier());
    }

    @Override
    public String getName() { return "Plus"; }
}

class LuxTier implements SubscriptionTier {
    @Override
    public void travelCheck(PatientSubscription context, int km) {
        evaluateDistance(context, km, 50); // Safe range is 0-50 km[cite: 5]
    }

    @Override
    public void setMood(String mood) {
        System.out.println("Mood successfully set to: " + mood); // Lux allows mood changes[cite: 5]
    }

    @Override
    public void promote(PatientSubscription context) {
        System.out.println("Promote called: Lux to Lux (no change).");
    }

    @Override
    public void demote(PatientSubscription context) {
        System.out.println("Demoting from Lux to Plus.");
        context.setTier(new PlusTier());
    }

    @Override
    public String getName() { return "Lux"; }
}

// --- DEMONSTRATION ---
public class BrainSupportSimulator {
    public static void main(String[] args) {
        PatientSubscription patient = new PatientSubscription();

        System.out.println("--- Testing Distance & Mood Checks (Common Tier) ---");
        patient.travelCheck(5);    // Should be STABLE
        patient.setMood("happy");  // Should be unavailable
        patient.travelCheck(15);   // Should become UNSTABLE and auto-recover[cite: 5]

        System.out.println("--- Testing Promotions/Demotions ---");
        patient.promote();         // Common -> Plus
        patient.travelCheck(40);   // Should be STABLE in Plus
        patient.promote();         // Plus -> Lux
        patient.setMood("calm");   // Should succeed in Lux
        patient.demote();          // Lux -> Plus

        // Test temporary Lux activation and return[cite: 5]
        patient.activateLux(2);
        
        System.out.println("--- Back in Plus Tier ---");
        patient.setMood("exhausted"); // Should be unavailable again
        patient.travelCheck(60);      // Should become UNSTABLE
    }
}

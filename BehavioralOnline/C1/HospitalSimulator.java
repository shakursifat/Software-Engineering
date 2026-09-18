import java.util.UUID;

// --- ABSTRACT CLASS (The Template) ---
abstract class HospitalVisit {
    
    // The Template Method: marked as final so subclasses cannot change the overall flow.
    public final void executeVisit(String patientName) {
        System.out.println("--- Starting Visit for: " + patientName + " ---");
        checkIn(patientName);
        recordVitals();
        assessment();
        treatment();
        dischargeSummary(patientName);
        System.out.println("----------------------------------------\n");
    }

    // Common Step 1: Check-In (register patient name + assign visit ID)[cite: 5]
    private void checkIn(String patientName) {
        String visitId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("1. Check-In: Patient '" + patientName + "' registered. Visit ID: " + visitId);
    }

    // Common Step 2: Record Vitals (print temperature + blood pressure)[cite: 5]
    private void recordVitals() {
        System.out.println("2. Record Vitals: Temperature 98.6°F, Blood Pressure 120/80");
    }

    // Common Step 5: Discharge Summary (print "patient discharged" + notes)[cite: 5]
    private void dischargeSummary(String patientName) {
        System.out.println("5. Discharge Summary: Patient discharged. Notes: Follow up if symptoms persist.");
    }

    // Steps to be customized by each specific department
    protected abstract void assessment();
    protected abstract void treatment();
}

// --- CONCRETE SUBCLASSES (Departments) ---

class GeneralDepartment extends HospitalVisit {
    @Override
    protected void assessment() {
        System.out.println("3. Assessment: Doctor performs normal diagnosis"); // Custom step[cite: 5]
    }

    @Override
    protected void treatment() {
        System.out.println("4. Treatment: Prescribe standard medicine"); // Custom step[cite: 5]
    }
}

class PediatricsDepartment extends HospitalVisit {
    @Override
    protected void assessment() {
        System.out.println("3. Assessment: Doctor checks symptoms by ensuring child comfort level"); // Custom step[cite: 5]
    }

    @Override
    protected void treatment() {
        System.out.println("4. Treatment: Give child-safe medicine, friendly reassurance message"); // Custom step[cite: 5]
    }
}

class EmergencyDepartment extends HospitalVisit {
    @Override
    protected void assessment() {
        System.out.println("3. Assessment: Quick triage check (urgent/non-urgent)"); // Custom step[cite: 5]
    }

    @Override
    protected void treatment() {
        System.out.println("4. Treatment: Immediate emergency procedure"); // Custom step[cite: 5]
    }
}

// --- DEMONSTRATION ---
public class HospitalSimulator {
    public static void main(String[] args) {
        
        HospitalVisit generalVisit = new GeneralDepartment();
        HospitalVisit pediatricsVisit = new PediatricsDepartment();
        HospitalVisit emergencyVisit = new EmergencyDepartment();

        // Run the simulation for 1 patient in each department[cite: 5]
        System.out.println("=== General Department ===");
        generalVisit.executeVisit("John Doe");

        System.out.println("=== Pediatrics Department ===");
        pediatricsVisit.executeVisit("Little Timmy");

        System.out.println("=== Emergency Department ===");
        emergencyVisit.executeVisit("Jane Smith");
    }
}

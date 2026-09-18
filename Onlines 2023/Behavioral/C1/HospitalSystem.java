import java.util.*;

// --- ENUMS ---
enum InvestigationType {
    PATHOLOGY, RADIOLOGY
}

// --- MEDIATOR INTERFACE ---
interface EmergencyCenter {
    void requestInvestigations(String patientId, List<InvestigationType> types);
    void submitResult(String patientId, InvestigationType type, String result);
    void registerDoctor(Doctor doctor);
    void registerPathologyLab(PathologyLab lab);
    void registerRadiologyUnit(RadiologyUnit unit);
    void registerPatient(Patient patient);
}

// --- RECORD TRACKER ---
// The Emergency Center must track requested, pending, and completed investigations separately.
class PatientRecord {
    String patientId;
    Set<InvestigationType> requested = new HashSet<>();
    Map<InvestigationType, String> completedResults = new HashMap<>();

    public PatientRecord(String patientId) {
        this.patientId = patientId;
    }

    public boolean isComplete() {
        return requested.size() == completedResults.size();
    }
}

// --- CONCRETE MEDIATOR ---
class HospitalEmergencyCenter implements EmergencyCenter {
    private Doctor doctor;
    private PathologyLab pathologyLab;
    private RadiologyUnit radiologyUnit;
    private Map<String, Patient> patients = new HashMap<>();
    private Map<String, PatientRecord> records = new HashMap<>();

    @Override
    public void registerDoctor(Doctor doctor) { this.doctor = doctor; }
    @Override
    public void registerPathologyLab(PathologyLab lab) { this.pathologyLab = lab; }
    @Override
    public void registerRadiologyUnit(RadiologyUnit unit) { this.radiologyUnit = unit; }
    @Override
    public void registerPatient(Patient patient) { patients.put(patient.getId(), patient); }

    @Override
    public void requestInvestigations(String patientId, List<InvestigationType> types) {
        PatientRecord record = records.computeIfAbsent(patientId, PatientRecord::new);
        record.requested.addAll(types);

        for (InvestigationType type : types) {
            if (type == InvestigationType.PATHOLOGY) {
                System.out.println("Pathology test requested for Patient " + patientId + ".");
                pathologyLab.performTest(patientId);
            } else if (type == InvestigationType.RADIOLOGY) {
                System.out.println("Radiology investigation requested for Patient " + patientId + ".");
                radiologyUnit.performInvestigation(patientId);
            }
        }
    }

    @Override
    public void submitResult(String patientId, InvestigationType type, String result) {
        PatientRecord record = records.get(patientId);
        if (record == null) return;

        record.completedResults.put(type, result);
        Patient patient = patients.get(patientId);

        // Check for urgent results: Pathology=CRITICAL or Radiology=NOT OK
        boolean isUrgent = (type == InvestigationType.PATHOLOGY && result.equals("CRITICAL")) ||
                           (type == InvestigationType.RADIOLOGY && result.equals("NOT OK"));

        if (isUrgent) {
            System.out.println("Critical " + type.name().toLowerCase() + " result received for Patient " + patientId + ".");
            System.out.println("URGENT notification sent to Doctor.");
            doctor.receiveUrgentNotification(patientId, type, result);
            
            System.out.println("URGENT notification sent to Patient " + patientId + ".");
            if (patient != null) patient.receiveUrgentNotification(type, result);
        } else {
             System.out.println(type.name().charAt(0) + type.name().substring(1).toLowerCase() + 
                                " result received for Patient " + patientId + ".");
        }

        // Case 1 & Case 2: Send complete results only when all requested tests are done[cite: 4]
        if (record.isComplete()) {
            System.out.println("All requested investigations completed for Patient " + patientId + ".");
            System.out.println("Complete results sent to Doctor.");
            doctor.receiveCompleteResults(patientId, record.completedResults);
            
            System.out.println("Complete results sent to Patient " + patientId + ".");
            if (patient != null) patient.receiveCompleteResults(record.completedResults);
        }
    }
}

// --- COLLEAGUES (Medical Units & Patient) ---
// They only hold a reference to the Mediator, never to each other[cite: 4].

class Doctor {
    private EmergencyCenter mediator;

    public Doctor(EmergencyCenter mediator) {
        this.mediator = mediator;
        mediator.registerDoctor(this);
    }

    public void requestTests(String patientId, List<InvestigationType> types) {
        mediator.requestInvestigations(patientId, types);
    }

    public void receiveUrgentNotification(String patientId, InvestigationType type, String result) {
        // Internal doctor logic for urgent results
    }

    public void receiveCompleteResults(String patientId, Map<InvestigationType, String> results) {
         // Internal doctor logic for complete results
    }
}

class PathologyLab {
    private EmergencyCenter mediator;

    public PathologyLab(EmergencyCenter mediator) {
        this.mediator = mediator;
        mediator.registerPathologyLab(this);
    }

    public void performTest(String patientId) {
        // Simulating the lab doing work...
    }

    // A pathology result is reported as NORMAL or CRITICAL[cite: 4].
    public void submitResult(String patientId, String result) {
        mediator.submitResult(patientId, InvestigationType.PATHOLOGY, result);
    }
}

class RadiologyUnit {
    private EmergencyCenter mediator;

    public RadiologyUnit(EmergencyCenter mediator) {
        this.mediator = mediator;
        mediator.registerRadiologyUnit(this);
    }

    public void performInvestigation(String patientId) {
        // Simulating the unit doing work...
    }

    // A radiology result is reported as OK or NOT OK[cite: 4].
    public void submitResult(String patientId, String result) {
        mediator.submitResult(patientId, InvestigationType.RADIOLOGY, result);
    }
}

class Patient {
    private String id;

    public Patient(String id, EmergencyCenter mediator) {
        this.id = id;
        mediator.registerPatient(this);
    }

    public String getId() { return id; }

    public void receiveUrgentNotification(InvestigationType type, String result) {}
    public void receiveCompleteResults(Map<InvestigationType, String> results) {}
}

// --- MAIN CLASS (TESTING) ---
public class HospitalSystem {
    public static void main(String[] args) {
        // Setup Mediator and Colleagues
        HospitalEmergencyCenter emergencyCenter = new HospitalEmergencyCenter();
        Doctor doctor = new Doctor(emergencyCenter);
        PathologyLab pathologyLab = new PathologyLab(emergencyCenter);
        RadiologyUnit radiologyUnit = new RadiologyUnit(emergencyCenter);
        Patient patientP101 = new Patient("P101", emergencyCenter);

        // Doctor requests both a pathology test and a radiology investigation[cite: 4].
        List<InvestigationType> requestedTests = Arrays.asList(InvestigationType.PATHOLOGY, InvestigationType.RADIOLOGY);
        doctor.requestTests("P101", requestedTests);

        System.out.println(); // Formatting

        // Pathology result arrives first and is CRITICAL[cite: 4].
        pathologyLab.submitResult("P101", "CRITICAL");

        System.out.println(); // Formatting

        // Later, the Radiology Unit reports OK[cite: 4].
        radiologyUnit.submitResult("P101", "OK");
    }
}

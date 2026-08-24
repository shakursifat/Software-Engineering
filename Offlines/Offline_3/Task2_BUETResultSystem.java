import java.util.*;

/**
 * Task 2: BUET Final Result Publication System
 * Pattern: Mediator Design Pattern
 *
 * A centralised result-processing coordinator manages all communication
 * between the Department Office, Controller of Examinations, DSW, and the
 * student.  No office directly calls or controls another office.
 *
 * Required Sequence (per student):
 *   1. Department Office  → confirms academic requirements
 *   2. Controller of Examinations → issues the office order
 *   3. DSW                → issues the testimonial
 *   4. Controller of Examinations → issues certificate & transcript
 */

// ─────────────────────────────────────────────────────────────────────────────
// Interface: ResultMediator
// Defines the contract for the central coordinator.
// ─────────────────────────────────────────────────────────────────────────────

interface ResultMediator {
    /**
     * Colleagues call this method to communicate through the mediator.
     *
     * @param sender    the colleague initiating the action
     * @param event     a string identifier for the action/event
     * @param studentId the student this action concerns
     */
    void notify(Colleague sender, String event, String studentId);
}

// ─────────────────────────────────────────────────────────────────────────────
// Abstract Class: Colleague
// Base class for all participants (offices and student).
// Each colleague holds a reference to the mediator instead of directly
// referencing other colleagues.
// ─────────────────────────────────────────────────────────────────────────────

abstract class Colleague {
    protected ResultMediator mediator;
    protected final String officeName;

    public Colleague(String officeName) {
        this.officeName = officeName;
    }

    public void setMediator(ResultMediator mediator) {
        this.mediator = mediator;
    }

    public String getOfficeName() {
        return officeName;
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Concrete Colleague: DepartmentOffice
// Confirms a student has completed all academic requirements.
// ─────────────────────────────────────────────────────────────────────────────

class DepartmentOffice extends Colleague {

    public DepartmentOffice() {
        super("Department Office");
    }

    /**
     * Confirms that the given student has completed all academic requirements.
     */
    public void confirmStudent(String studentId) {
        System.out.println("\n[" + officeName + "] Submitting departmental confirmation"
                + " for student: " + studentId);
        mediator.notify(this, "DEPARTMENTAL_CONFIRMATION", studentId);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Concrete Colleague: ControllerOfExaminations
// Issues the office order, certificate, and academic transcript.
// ─────────────────────────────────────────────────────────────────────────────

class ControllerOfExaminations extends Colleague {

    public ControllerOfExaminations() {
        super("Office of the Controller of Examinations");
    }

    /**
     * Requests issuance of the final-result publication office order.
     */
    public void issueOfficeOrder(String studentId) {
        System.out.println("\n[" + officeName + "] Requesting office order"
                + " for student: " + studentId);
        mediator.notify(this, "ISSUE_OFFICE_ORDER", studentId);
    }

    /**
     * Requests issuance of the certificate and academic transcript.
     */
    public void issueCertificateAndTranscript(String studentId) {
        System.out.println("\n[" + officeName + "] Requesting certificate & transcript"
                + " for student: " + studentId);
        mediator.notify(this, "ISSUE_CERTIFICATE_TRANSCRIPT", studentId);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Concrete Colleague: DSW (Directorate of Students' Welfare)
// Issues the testimonial after the office order has been issued.
// ─────────────────────────────────────────────────────────────────────────────

class DSW extends Colleague {

    public DSW() {
        super("Directorate of Students' Welfare (DSW)");
    }

    /**
     * Requests issuance of the testimonial for the student.
     */
    public void issueTestimonial(String studentId) {
        System.out.println("\n[" + officeName + "] Requesting testimonial"
                + " for student: " + studentId);
        mediator.notify(this, "ISSUE_TESTIMONIAL", studentId);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Concrete Colleague: Student
// Receives notifications from the mediator throughout the process.
// ─────────────────────────────────────────────────────────────────────────────

class Student extends Colleague {
    private final String studentId;
    private final List<String> notifications = new ArrayList<>();

    public Student(String studentId, String name) {
        super("Student [" + name + " | " + studentId + "]");
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    /**
     * Receives a notification message from the mediator.
     */
    public void receiveNotification(String message) {
        notifications.add(message);
        System.out.println("  >>> [STUDENT NOTIFICATION] " + officeName
                + ": " + message);
    }

    /**
     * Displays all notifications the student has received.
     */
    public void displayNotifications() {
        System.out.println("\n------ Notifications for " + officeName + " ------");
        if (notifications.isEmpty()) {
            System.out.println("  (No notifications received yet.)");
        } else {
            for (int i = 0; i < notifications.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + notifications.get(i));
            }
        }
        System.out.println("----------------------------------------------");
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Student Processing State
// Tracks the completion status of each required step per student.
// ─────────────────────────────────────────────────────────────────────────────

class StudentProcessingState {
    boolean departmentalConfirmed    = false;
    boolean officeOrderIssued        = false;
    boolean testimonialIssued        = false;
    boolean certificateIssued        = false;

    /**
     * Returns a human-readable summary of the current state.
     */
    public String getSummary() {
        return "  Departmental Confirmation : " + status(departmentalConfirmed) + "\n"
             + "  Office Order Issued       : " + status(officeOrderIssued)     + "\n"
             + "  Testimonial Issued        : " + status(testimonialIssued)     + "\n"
             + "  Certificate & Transcript  : " + status(certificateIssued);
    }

    private String status(boolean done) {
        return done ? "✔  DONE" : "✘  PENDING";
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Concrete Mediator: ResultProcessingCoordinator
// Central hub that enforces the required processing sequence and routes all
// communication.  No colleague is allowed to call another colleague directly.
// ─────────────────────────────────────────────────────────────────────────────

class ResultProcessingCoordinator implements ResultMediator {

    // Registered offices
    private DepartmentOffice           departmentOffice;
    private ControllerOfExaminations   controller;
    private DSW                        dsw;

    // Registered students (studentId → Student)
    private final Map<String, Student>                students     = new LinkedHashMap<>();
    // Processing state per student
    private final Map<String, StudentProcessingState> stateMap     = new LinkedHashMap<>();

    // ── Registration Methods ─────────────────────────────────────────────────

    public void registerDepartmentOffice(DepartmentOffice office) {
        this.departmentOffice = office;
        office.setMediator(this);
        System.out.println("[Coordinator] Registered: " + office.getOfficeName());
    }

    public void registerController(ControllerOfExaminations ctrl) {
        this.controller = ctrl;
        ctrl.setMediator(this);
        System.out.println("[Coordinator] Registered: " + ctrl.getOfficeName());
    }

    public void registerDSW(DSW dsw) {
        this.dsw = dsw;
        dsw.setMediator(this);
        System.out.println("[Coordinator] Registered: " + dsw.getOfficeName());
    }

    public void registerStudent(Student student) {
        students.put(student.getStudentId(), student);
        stateMap.put(student.getStudentId(), new StudentProcessingState());
        student.setMediator(this);
        System.out.println("[Coordinator] Registered: " + student.getOfficeName());
    }

    // ── Core Mediator Logic ──────────────────────────────────────────────────

    @Override
    public void notify(Colleague sender, String event, String studentId) {

        Student student = students.get(studentId);
        if (student == null) {
            System.out.println("  [Coordinator] ERROR: Student ID '" + studentId
                    + "' is not registered.");
            return;
        }

        StudentProcessingState state = stateMap.get(studentId);

        switch (event) {

            // ── Event 1: Departmental Confirmation ──────────────────────────
            case "DEPARTMENTAL_CONFIRMATION":
                if (state.departmentalConfirmed) {
                    System.out.println("  [Coordinator] INFO: Departmental confirmation"
                            + " already received for student " + studentId + ".");
                } else {
                    state.departmentalConfirmed = true;
                    System.out.println("  [Coordinator] Departmental confirmation"
                            + " recorded for student " + studentId + ".");
                    student.receiveNotification(
                            "Your departmental confirmation has been received.");
                }
                break;

            // ── Event 2: Issue Office Order ──────────────────────────────────
            case "ISSUE_OFFICE_ORDER":
                if (!state.departmentalConfirmed) {
                    System.out.println("  [Coordinator] REJECTED: Cannot issue office order."
                            + " Departmental confirmation is missing for student "
                            + studentId + ".");
                    student.receiveNotification(
                            "Result publication rejected: departmental confirmation is missing.");
                } else if (state.officeOrderIssued) {
                    System.out.println("  [Coordinator] INFO: Office order already"
                            + " issued for student " + studentId + ".");
                } else {
                    state.officeOrderIssued = true;
                    System.out.println("  [Coordinator] Office order issued for"
                            + " student " + studentId + ".");
                    student.receiveNotification(
                            "Final-result publication office order has been issued.");
                }
                break;

            // ── Event 3: Issue Testimonial ───────────────────────────────────
            case "ISSUE_TESTIMONIAL":
                if (!state.officeOrderIssued) {
                    System.out.println("  [Coordinator] REJECTED: Cannot issue testimonial."
                            + " Office order has not been issued for student "
                            + studentId + ".");
                    student.receiveNotification(
                            "Testimonial issuance rejected: office order not yet issued.");
                } else if (state.testimonialIssued) {
                    System.out.println("  [Coordinator] INFO: Testimonial already"
                            + " issued for student " + studentId + ".");
                } else {
                    state.testimonialIssued = true;
                    System.out.println("  [Coordinator] Testimonial issued for"
                            + " student " + studentId + ".");
                    student.receiveNotification(
                            "Your testimonial has been issued by DSW.");
                }
                break;

            // ── Event 4: Issue Certificate & Transcript ──────────────────────
            case "ISSUE_CERTIFICATE_TRANSCRIPT":
                if (!state.departmentalConfirmed) {
                    System.out.println("  [Coordinator] REJECTED: Departmental"
                            + " confirmation missing for student " + studentId + ".");
                    student.receiveNotification(
                            "Certificate/Transcript rejected: departmental confirmation missing.");
                } else if (!state.officeOrderIssued) {
                    System.out.println("  [Coordinator] REJECTED: Office order not"
                            + " yet issued for student " + studentId + ".");
                    student.receiveNotification(
                            "Certificate/Transcript rejected: office order not yet issued.");
                } else if (!state.testimonialIssued) {
                    System.out.println("  [Coordinator] REJECTED: Testimonial not"
                            + " yet issued for student " + studentId + ".");
                    student.receiveNotification(
                            "Certificate/Transcript rejected: testimonial not yet issued.");
                } else if (state.certificateIssued) {
                    System.out.println("  [Coordinator] INFO: Certificate & transcript"
                            + " already issued for student " + studentId + ".");
                } else {
                    state.certificateIssued = true;
                    System.out.println("  [Coordinator] Certificate & transcript"
                            + " issued for student " + studentId + ".");
                    student.receiveNotification(
                            "Your certificate and academic transcript have been issued.");
                }
                break;

            default:
                System.out.println("  [Coordinator] Unknown event: " + event);
        }
    }

    // ── Status Display ───────────────────────────────────────────────────────

    /**
     * Prints the current processing status for a student.
     */
    public void displayStatus(String studentId) {
        Student student = students.get(studentId);
        StudentProcessingState state  = stateMap.get(studentId);

        System.out.println("\n========================================");
        System.out.println("  Processing Status for: "
                + (student != null ? student.getOfficeName() : studentId));
        System.out.println("========================================");
        if (state != null) {
            System.out.println(state.getSummary());
        } else {
            System.out.println("  Student not found.");
        }
        System.out.println("========================================");
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main Driver Class
// Demonstrates the BUET Final Result Publication System in the exact sequence
// required by the specification.
// ─────────────────────────────────────────────────────────────────────────────

public class Task2_BUETResultSystem {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   BUET Final Result Publication System        ║");
        System.out.println("║        Mediator Design Pattern Demo           ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        // ── Step 1: Create the mediator (coordinator) ──────────────────────
        ResultProcessingCoordinator coordinator = new ResultProcessingCoordinator();

        // ── Step 2: Create colleagues ──────────────────────────────────────
        DepartmentOffice         deptOffice  = new DepartmentOffice();
        ControllerOfExaminations controller  = new ControllerOfExaminations();
        DSW                      dsw         = new DSW();
        Student                  student     = new Student("2005001", "Arif Hossain");

        // ── Step 3: Register all colleagues with the coordinator ───────────
        System.out.println("=== Registering Participants with Coordinator ===");
        coordinator.registerDepartmentOffice(deptOffice);
        coordinator.registerController(controller);
        coordinator.registerDSW(dsw);
        coordinator.registerStudent(student);

        String sid = student.getStudentId();

        // ──────────────────────────────────────────────────────────────────
        // Demo Step 1: Attempt to publish result BEFORE departmental
        //              confirmation → should be REJECTED.
        // ──────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║  Step 1: Attempt to publish result before     ║");
        System.out.println("║          departmental confirmation            ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        controller.issueOfficeOrder(sid);   // Expected: REJECTED

        // ──────────────────────────────────────────────────────────────────
        // Demo Step 2: Submit departmental confirmation.
        // ──────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║  Step 2: Submit departmental confirmation     ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        deptOffice.confirmStudent(sid);     // Expected: CONFIRMED

        // ──────────────────────────────────────────────────────────────────
        // Demo Step 3: Early attempt to issue certificate/transcript BEFORE
        //              the office order and testimonial → should be REJECTED.
        // ──────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║  Step 3: Early attempt to issue certificate   ║");
        System.out.println("║          or transcript (out-of-sequence)      ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        controller.issueCertificateAndTranscript(sid);  // Expected: REJECTED (no order yet)

        // ──────────────────────────────────────────────────────────────────
        // Demo Step 4: Issuance of the final-result office order.
        // ──────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║  Step 4: Issuance of the final-result         ║");
        System.out.println("║          office order                         ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        controller.issueOfficeOrder(sid);   // Expected: ISSUED

        // ──────────────────────────────────────────────────────────────────
        // Demo Step 5: Issuance of the testimonial by DSW.
        // ──────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║  Step 5: Issuance of the testimonial by DSW   ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        dsw.issueTestimonial(sid);          // Expected: ISSUED

        // ──────────────────────────────────────────────────────────────────
        // Demo Step 6: Issuance of the certificate and academic transcript.
        // ──────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║  Step 6: Issuance of certificate & transcript ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        controller.issueCertificateAndTranscript(sid);  // Expected: ISSUED

        // ──────────────────────────────────────────────────────────────────
        // Demo Step 7: Student notifications and final processing status.
        // ──────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║  Step 7: Student notifications & final status ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        student.displayNotifications();
        coordinator.displayStatus(sid);

        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║           Demo Completed Successfully          ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }
}

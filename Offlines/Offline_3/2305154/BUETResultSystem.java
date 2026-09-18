import java.util.*;

interface ResultMediator {
    void notify(Colleague sender, String event, String studentId);
}

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

class DepartmentOffice extends Colleague {

    public DepartmentOffice() {
        super("Department Office");
    }

    public void confirmStudent(String studentId) {
        System.out.println("\n[" + officeName + "] Submitting departmental confirmation"
                + " for student: " + studentId);
        mediator.notify(this, "DEPARTMENTAL_CONFIRMATION", studentId);
    }
}

class ControllerOfExaminations extends Colleague {

    public ControllerOfExaminations() {
        super("Office of the Controller of Examinations");
    }

    public void issueOfficeOrder(String studentId) {
        System.out.println("\n[" + officeName + "] Requesting office order"
                + " for student: " + studentId);
        mediator.notify(this, "ISSUE_OFFICE_ORDER", studentId);
    }

    public void issueCertificateAndTranscript(String studentId) {
        System.out.println("\n[" + officeName + "] Requesting certificate & transcript"
                + " for student: " + studentId);
        mediator.notify(this, "ISSUE_CERTIFICATE_TRANSCRIPT", studentId);
    }
}

class DSW extends Colleague {

    public DSW() {
        super("Directorate of Students' Welfare (DSW)");
    }

    public void issueTestimonial(String studentId) {
        System.out.println("\n[" + officeName + "] Requesting testimonial"
                + " for student: " + studentId);
        mediator.notify(this, "ISSUE_TESTIMONIAL", studentId);
    }
}

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

    public void receiveNotification(String message) {
        notifications.add(message);
        System.out.println("  >>> [STUDENT NOTIFICATION] " + officeName
                + ": " + message);
    }

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

class StudentProcessingState {
    boolean departmentalConfirmed = false;
    boolean officeOrderIssued = false;
    boolean testimonialIssued = false;
    boolean certificateIssued = false;

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

class ResultProcessingCoordinator implements ResultMediator {

    // Registered offices
    private DepartmentOffice departmentOffice;
    private ControllerOfExaminations controller;
    private DSW dsw;

    private final Map<String, Student> students     = new LinkedHashMap<>();
    private final Map<String, StudentProcessingState> stateMap = new LinkedHashMap<>();

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

    public void displayStatus(String studentId) {
        Student student = students.get(studentId);
        StudentProcessingState state  = stateMap.get(studentId);

        System.out.println("\n  Processing Status for: "
                + (student != null ? student.getOfficeName() : studentId));
        System.out.println();
        if (state != null) {
            System.out.println(state.getSummary());
        } else {
            System.out.println("  Student not found.");
        }
    }
}

public class BUETResultSystem {

    public static void main(String[] args) {
        System.out.println("   BUET Final Result Publication System    ");

        ResultProcessingCoordinator coordinator = new ResultProcessingCoordinator();

        DepartmentOffice deptOffice  = new DepartmentOffice();
        ControllerOfExaminations controller  = new ControllerOfExaminations();
        DSW dsw = new DSW();
        Student student = new Student("2005001", "Arif Hossain");


        System.out.println("=== Registering Participants with Coordinator ===");
        coordinator.registerDepartmentOffice(deptOffice);
        coordinator.registerController(controller);
        coordinator.registerDSW(dsw);
        coordinator.registerStudent(student);

        String sid = student.getStudentId();

        System.out.println("\nStep 1: Attempt to publish result before departmental confirmation");
        controller.issueOfficeOrder(sid);

        System.out.println("\nStep 2: Submit departmental confirmation");
        deptOffice.confirmStudent(sid);

        System.out.println("\nStep 3: Early attempt to issue certificate or transcript (out-of-sequence)") ;
        controller.issueCertificateAndTranscript(sid);


        System.out.println("\nStep 4: Issuance of the final-result office order");
        controller.issueOfficeOrder(sid);

        System.out.println("\nStep 5: Issuance of the testimonial by DSW");
        dsw.issueTestimonial(sid);

        System.out.println("\nStep 6: Issuance of certificate & transcript");
        controller.issueCertificateAndTranscript(sid);

        System.out.println("\nStep 7: Student notifications & final status");
        student.displayNotifications();
        coordinator.displayStatus(sid);

        System.out.println("\nDemo Completed Successfully");
    }
}

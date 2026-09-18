// --- CONTEXT CLASS ---
class ReturnRequest {
    private ReturnState currentState;
    private String reason;

    public ReturnRequest(String reason) {
        this.reason = reason;
        // A return request starts in the Requested condition
        this.currentState = new RequestedState(); 
    }

    public void setState(ReturnState state) {
        this.currentState = state;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }

    // Delegate operations to the current state
    public void updateReason(String reason) { currentState.updateReason(this, reason); }
    public void approve() { currentState.approve(this); }
    public void reject() { currentState.reject(this); }
    public void cancel() { currentState.cancel(this); }
    public void itemDelivered() { currentState.itemDelivered(this); }
    public void inspect(boolean eligible) { currentState.inspect(this, eligible); }
    public void refundSuccessful() { currentState.refundSuccessful(this); }
    public void refundFailed() { currentState.refundFailed(this); }
}

// --- STATE INTERFACE ---
interface ReturnState {
    // Default methods handle invalid operations by producing an appropriate message
    default void updateReason(ReturnRequest request, String reason) {
        System.out.println("Invalid operation: Cannot update reason in this condition.");
    }
    default void approve(ReturnRequest request) {
        System.out.println("Invalid operation: Cannot approve in this condition.");
    }
    default void reject(ReturnRequest request) {
        System.out.println("Invalid operation: Cannot reject in this condition.");
    }
    default void cancel(ReturnRequest request) {
        System.out.println("Invalid operation: Cannot cancel in this condition.");
    }
    default void itemDelivered(ReturnRequest request) {
        System.out.println("Invalid operation: Cannot mark item delivered in this condition.");
    }
    default void inspect(ReturnRequest request, boolean eligible) {
        System.out.println("Invalid operation: Cannot inspect item in this condition.");
    }
    default void refundSuccessful(ReturnRequest request) {
        System.out.println("Invalid operation: Cannot process successful refund in this condition.");
    }
    default void refundFailed(ReturnRequest request) {
        System.out.println("Invalid operation: Cannot process failed refund in this condition.");
    }
}

// --- CONCRETE STATES ---

class RequestedState implements ReturnState {
    @Override
    public void updateReason(ReturnRequest request, String reason) {
        request.setReason(reason);
        System.out.println("Return reason updated to: " + reason);
    }

    @Override
    public void approve(ReturnRequest request) {
        System.out.println("Request approved. Moving to Approved state.");
        request.setState(new ApprovedState());
    }

    @Override
    public void reject(ReturnRequest request) {
        System.out.println("Request rejected. Moving to Rejected state.");
        request.setState(new RejectedState());
    }

    @Override
    public void cancel(ReturnRequest request) {
        System.out.println("Request cancelled. Moving to Cancelled state.");
        request.setState(new CancelledState());
    }
}

class ApprovedState implements ReturnState {
    @Override
    public void cancel(ReturnRequest request) {
        // Can be cancelled if the returned item has not yet been delivered[cite: 2]
        System.out.println("Request cancelled. Moving to Cancelled state.");
        request.setState(new CancelledState());
    }

    @Override
    public void itemDelivered(ReturnRequest request) {
        System.out.println("Item delivered. Moving to Delivered state.");
        request.setState(new DeliveredState());
    }
}

class DeliveredState implements ReturnState {
    @Override
    public void inspect(ReturnRequest request, boolean eligible) {
        if (eligible) {
            // If it satisfies the return policy, refund processing begins[cite: 2]
            System.out.println("Inspection passed. Moving to Processing Refund state.");
            request.setState(new ProcessingRefundState());
        } else {
            // Otherwise, the request becomes rejected[cite: 2]
            System.out.println("Inspection failed. Moving to Rejected state.");
            request.setState(new RejectedState());
        }
    }
}

class ProcessingRefundState implements ReturnState {
    @Override
    public void refundSuccessful(ReturnRequest request) {
        System.out.println("Refund successful. Moving to Refunded state.");
        request.setState(new RefundedState());
    }

    @Override
    public void refundFailed(ReturnRequest request) {
        // If it fails, it may be attempted again later (stays in same state)[cite: 2]
        System.out.println("Refund failed. Will retry later. Staying in Processing Refund state.");
    }
}

// Final conditions: No further operation should change the request[cite: 2]
class RefundedState implements ReturnState { /* Inherits all default invalid messages */ }
class RejectedState implements ReturnState { /* Inherits all default invalid messages */ }
class CancelledState implements ReturnState { /* Inherits all default invalid messages */ }

// --- MAIN CLASS (TESTING) ---
public class ReturnSystem {
    public static void main(String[] args) {
        System.out.println("--- Starting Return Workflow ---");
        ReturnRequest request = new ReturnRequest("Defective product");
        
        request.updateReason("Wrong size");     // Valid
        request.approve();                      // Valid -> Approved
        request.updateReason("Color mismatch"); // Invalid (Approved state)
        request.itemDelivered();                // Valid -> Delivered
        request.cancel();                       // Invalid (Delivered state)
        request.inspect(true);                  // Valid -> Processing Refund
        request.refundFailed();                 // Valid -> Retries later
        request.refundSuccessful();             // Valid -> Refunded
        request.reject();                       // Invalid (Final state)
    }
}

// ==========================================
// 1. BRIDGE PATTERN: IMPLEMENTOR HIERARCHY
// ==========================================
// This interface defines the delivery mechanism. It focuses purely on HOW 
// the message gets sent, not what the message says.

interface CommunicationChannel {
    void deliverMessage(String content);
}

class EmailChannel implements CommunicationChannel {
    @Override
    public void deliverMessage(String content) {
        System.out.println("Sending via EMAIL: " + content);
    }
}

class SmsChannel implements CommunicationChannel {
    @Override
    public void deliverMessage(String content) {
        System.out.println("Sending via SMS: " + content);
    }
}

class WhatsAppChannel implements CommunicationChannel {
    @Override
    public void deliverMessage(String content) {
        System.out.println("Sending via WHATSAPP: " + content);
    }
}

class PushChannel implements CommunicationChannel {
    @Override
    public void deliverMessage(String content) {
        System.out.println("Sending via PUSH NOTIFICATION: " + content);
    }
}

// ==========================================
// 2. BRIDGE PATTERN: ABSTRACTION HIERARCHY
// ==========================================
// This defines the notification events. It focuses on WHAT the message says 
// and delegates the actual delivery to the bridged CommunicationChannel.

abstract class NotificationEvent {
    // The Bridge: A reference to the implementor
    protected CommunicationChannel channel;

    public NotificationEvent(CommunicationChannel channel) {
        this.channel = channel;
    }

    // Allows changing the channel at runtime if needed
    public void setChannel(CommunicationChannel channel) {
        this.channel = channel;
    }

    public abstract void processNotification();
}

// ==========================================
// 3. REFINED ABSTRACTIONS (Event Types)
// ==========================================
// These classes define the specific structure and tone for different alerts.

class PaymentFailedAlert extends NotificationEvent {
    public PaymentFailedAlert(CommunicationChannel channel) {
        super(channel);
    }

    @Override
    public void processNotification() {
        String content = "[URGENT] Your recent payment failed. Please update your payment method to ensure your monthly bazar is not paused.";
        channel.deliverMessage(content);
    }
}

class BazarDispatchedAlert extends NotificationEvent {
    public BazarDispatchedAlert(CommunicationChannel channel) {
        super(channel);
    }

    @Override
    public void processNotification() {
        String content = "[UPDATE] Great news! Your Dalchal monthly bazar has been dispatched and is on its way to your home.";
        channel.deliverMessage(content);
    }
}

class BazarConfirmedAlert extends NotificationEvent {
    public BazarConfirmedAlert(CommunicationChannel channel) {
        super(channel);
    }

    @Override
    public void processNotification() {
        String content = "[CONFIRMED] Your monthly bazar order has been confirmed and is currently being packed.";
        channel.deliverMessage(content);
    }
}

// ==========================================
// 4. CLIENT CODE (Demonstration)
// ==========================================

public class DalchalNotificationSystem {
    public static void main(String[] args) {
        // 1. Initialize our communication channels
        CommunicationChannel email = new EmailChannel();
        CommunicationChannel sms = new SmsChannel();
        CommunicationChannel whatsapp = new WhatsAppChannel();

        System.out.println("--- Scenario 1: User prefers SMS for urgent alerts ---");
        NotificationEvent paymentAlert = new PaymentFailedAlert(sms);
        paymentAlert.processNotification();

        System.out.println("\n--- Scenario 2: User prefers WhatsApp for delivery updates ---");
        NotificationEvent dispatchAlert = new BazarDispatchedAlert(whatsapp);
        dispatchAlert.processNotification();

        System.out.println("\n--- Scenario 3: Platform scales to new users (Email for Confirmations) ---");
        NotificationEvent confirmationAlert = new BazarConfirmedAlert(email);
        confirmationAlert.processNotification();

        System.out.println("\n--- Scenario 4: User updates their preference from Email to WhatsApp ---");
        // We can dynamically switch the bridge without recreating the event logic
        confirmationAlert.setChannel(whatsapp);
        confirmationAlert.processNotification();
    }
}

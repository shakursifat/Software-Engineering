// ==========================================
// 1. COMPONENT INTERFACE
// ==========================================
// The common interface for both the base notifications and the decorators.

interface Notification {
    void send(String message);
}

// ==========================================
// 2. CONCRETE COMPONENTS (Base Notifications)
// ==========================================
// These are the core communication channels.

class EmailNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("Delivering via EMAIL: " + message);
    }
}

class SMSNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("Delivering via SMS: " + message);
    }
}

class PushNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("Delivering via PUSH: " + message);
    }
}

// ==========================================
// 3. BASE DECORATOR
// ==========================================
// The core wrapper class. It holds a reference to a Notification object 
// and delegates the send() operation to it.

abstract class NotificationDecorator implements Notification {
    protected Notification wrappedNotification;

    public NotificationDecorator(Notification notification) {
        this.wrappedNotification = notification;
    }

    @Override
    public void send(String message) {
        wrappedNotification.send(message);
    }
}

// ==========================================
// 4. CONCRETE DECORATORS (The Enhancements)
// ==========================================
// These classes intercept the send() call to add their specific behavior 
// either before or after delegating to the wrapped object.

class EncryptionDecorator extends NotificationDecorator {
    public EncryptionDecorator(Notification notification) {
        super(notification);
    }

    @Override
    public void send(String message) {
        // Feature: Encrypt before sending
        String encryptedMessage = encrypt(message);
        super.send(encryptedMessage);
    }

    private String encrypt(String msg) {
        // Simulating an encryption process
        return "[ENCRYPTED_PAYLOAD: " + Integer.toHexString(msg.hashCode()) + "]";
    }
}

class PriorityDecorator extends NotificationDecorator {
    public PriorityDecorator(Notification notification) {
        super(notification);
    }

    @Override
    public void send(String message) {
        // Feature: Tag as High Priority before delivery
        String priorityMessage = "[HIGH PRIORITY] " + message;
        super.send(priorityMessage);
    }
}

class LoggingDecorator extends NotificationDecorator {
    public LoggingDecorator(Notification notification) {
        super(notification);
    }

    @Override
    public void send(String message) {
        // Feature: Send the message normally, then log it
        super.send(message);
        logActivity(message);
    }

    private void logActivity(String msg) {
        System.out.println("   -> [SYSTEM LOG]: Notification event recorded in IoT device auditing module.");
    }
}

// ==========================================
// 5. CLIENT CODE (Testing the Configurations)
// ==========================================

public class IoTNotificationSystem {

    public static void main(String[] args) {
        String alertMessage = "Motion detected at the front door!";

        System.out.println("--- Scenario 1: Basic Push Notification (No enhancements) ---");
        Notification basicPush = new PushNotification();
        basicPush.send(alertMessage);
        
        System.out.println("\n--- Scenario 2: SMS with Priority Label ---");
        Notification prioritySms = new SMSNotification();
        prioritySms = new PriorityDecorator(prioritySms);
        prioritySms.send(alertMessage);

        System.out.println("\n--- Scenario 3: Email with Encryption and Logging ---");
        // We can stack decorators infinitely in any order
        Notification secureEmail = new EmailNotification();
        secureEmail = new EncryptionDecorator(secureEmail);
        secureEmail = new LoggingDecorator(secureEmail);
        secureEmail.send(alertMessage);

        System.out.println("\n--- Scenario 4: The Ultimate Stack (All features enabled on Push) ---");
        Notification fullyLoaded = new PushNotification();
        fullyLoaded = new PriorityDecorator(fullyLoaded);
        fullyLoaded = new EncryptionDecorator(fullyLoaded);
        fullyLoaded = new LoggingDecorator(fullyLoaded);
        fullyLoaded.send(alertMessage);
    }
}
// ==========================================
// 1. The Common Product Interface
// ==========================================
interface Notification {
    void notifyUser();
}

// ==========================================
// 2. Concrete Products
// ==========================================
class SMSNotification implements Notification {
    @Override
    public void notifyUser() {
        System.out.println("Sending an SMS notification...");
    }
}

class EmailNotification implements Notification {
    @Override
    public void notifyUser() {
        System.out.println("Sending an Email notification...");
    }
}

class PushNotification implements Notification {
    @Override
    public void notifyUser() {
        System.out.println("Sending a Push notification...");
    }
}

// Future Expansion Example: 
// To add Slack, you just create this class and add one case to the Factory.
class SlackNotification implements Notification {
    @Override
    public void notifyUser() {
        System.out.println("Sending a Slack message...");
    }
}

// ==========================================
// 3. The Factory Class
// ==========================================
class NotificationFactory {
    
    // The factory method centralizes the creation logic
    public static Notification createNotification(String channel) {
        if (channel == null || channel.trim().isEmpty()) {
            return null;
        }
        
        // Using toUpperCase() makes the factory input case-insensitive
        switch (channel.toUpperCase()) {
            case "SMS":
                return new SMSNotification();
            case "EMAIL":
                return new EmailNotification();
            case "PUSH":
                return new PushNotification();
            case "SLACK": // Minimal change required for future expansion
                return new SlackNotification();
            default:
                throw new IllegalArgumentException("Unknown notification channel: " + channel);
        }
    }
}

// ==========================================
// 4. The Client Code
// ==========================================
public class NotificationService {
    public static void main(String[] args) {
        
        System.out.println("--- Processing Notifications ---");
        
        // The client only interacts with the Factory and the Notification interface.
        // It has no idea that classes like 'SMSNotification' even exist.
        
        Notification sms = NotificationFactory.createNotification("SMS");
        sms.notifyUser();
        
        Notification email = NotificationFactory.createNotification("Email");
        email.notifyUser();
        
        Notification push = NotificationFactory.createNotification("push");
        push.notifyUser();

        // Testing the new Slack channel
        Notification slack = NotificationFactory.createNotification("Slack");
        slack.notifyUser();
    }
}

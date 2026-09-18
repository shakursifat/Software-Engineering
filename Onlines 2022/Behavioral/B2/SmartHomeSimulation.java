// --- MEDIATOR INTERFACE ---
interface SmartHomeHub {
    void notify(SmartDevice sender, String event);
}

// --- COLLEAGUE (Base Device) ---
abstract class SmartDevice {
    protected SmartHomeHub hub;

    public SmartDevice(SmartHomeHub hub) {
        this.hub = hub;
    }
}

// --- CONCRETE COLLEAGUES (Devices) ---
class LightSensor extends SmartDevice {
    public LightSensor(SmartHomeHub hub) {
        super(hub);
    }

    public void detectBrightness(String level) {
        System.out.println("Light Sensor detects: " + level);
        if ("High Brightness".equals(level)) {
            // Notifies the Hub instead of talking to Blinds directly
            hub.notify(this, "High Brightness"); 
        }
    }
}

class AutomaticBlinds extends SmartDevice {
    public AutomaticBlinds(SmartHomeHub hub) {
        super(hub);
    }

    public void close() {
        System.out.println("Automatic Blinds: Closing...");
        // When closed, notifies the Hub[cite: 5]
        hub.notify(this, "Blinds Closed");
    }
}

class AirConditioner extends SmartDevice {
    public AirConditioner(SmartHomeHub hub) {
        super(hub);
    }

    public void turnOn() {
        System.out.println("Air Conditioner: Turning ON to prevent stuffiness.");
    }
}

// --- CONCRETE MEDIATOR ---
class CentralHub implements SmartHomeHub {
    private LightSensor lightSensor;
    private AutomaticBlinds blinds;
    private AirConditioner airConditioner;

    // Device registration
    public void setLightSensor(LightSensor lightSensor) { this.lightSensor = lightSensor; }
    public void setBlinds(AutomaticBlinds blinds) { this.blinds = blinds; }
    public void setAirConditioner(AirConditioner airConditioner) { this.airConditioner = airConditioner; }

    @Override
    public void notify(SmartDevice sender, String event) {
        // The Hub contains all the routing logic[cite: 5]
        if (sender == lightSensor && event.equals("High Brightness")) {
            System.out.println("[Hub] Light Sensor reported high brightness. Commanding Blinds to close.");
            blinds.close(); // Hub tells the Blinds to close[cite: 5]
        } 
        else if (sender == blinds && event.equals("Blinds Closed")) {
            System.out.println("[Hub] Blinds reported they are closed. Commanding AC to turn on.");
            airConditioner.turnOn(); // Hub tells the Air Conditioner to turn on[cite: 5]
        }
    }
}

// --- DEMONSTRATION ---
public class SmartHomeSimulation {
    public static void main(String[] args) {
        // 1. Create the Mediator (Hub)
        CentralHub hub = new CentralHub();

        // 2. Create the Colleagues (Devices)
        LightSensor sensor = new LightSensor(hub);
        AutomaticBlinds blinds = new AutomaticBlinds(hub);
        AirConditioner ac = new AirConditioner(hub);

        // 3. Register devices with the Hub
        hub.setLightSensor(sensor);
        hub.setBlinds(blinds);
        hub.setAirConditioner(ac);

        System.out.println("--- System Ready. Simulating Environment ---");
        
        // Simulating the trigger event
        // The sensor detects high brightness, and the Hub coordinates the rest automatically[cite: 5].
        sensor.detectBrightness("High Brightness");
    }
}

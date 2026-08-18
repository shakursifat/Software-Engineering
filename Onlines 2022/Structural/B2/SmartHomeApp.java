import java.util.ArrayList;
import java.util.List;

// ==========================================
// 1. TARGET INTERFACE
// ==========================================
// The standard interface that the Smart Home App expects.

interface SmartDevice {
    void turnOn();
    void turnOff();
}

// ==========================================
// 2. EXISTING COMPATIBLE DEVICES
// ==========================================
// These devices already implement the target interface perfectly.

class SmartLight implements SmartDevice {
    @Override
    public void turnOn() {
        System.out.println("Smart Light is turned ON.");
    }

    @Override
    public void turnOff() {
        System.out.println("Smart Light is turned OFF.");
    }
}

class SmartFan implements SmartDevice {
    @Override
    public void turnOn() {
        System.out.println("Smart Fan is turned ON.");
    }

    @Override
    public void turnOff() {
        System.out.println("Smart Fan is turned OFF.");
    }
}

// ==========================================
// 3. ADAPTEES (Third-Party/Legacy Devices)
// ==========================================
// These classes have incompatible interfaces and CANNOT be modified.

class OldSmartBulb {
    public void powerOn() {
        System.out.println("Old Smart Bulb is powering ON.");
    }

    public void powerOff() {
        System.out.println("Old Smart Bulb is powering OFF.");
    }
}

class LegacyHeater {
    public void startHeating() {
        System.out.println("Legacy Heater has started HEATING.");
    }

    public void stopHeating() {
        System.out.println("Legacy Heater has stopped HEATING.");
    }
}

// ==========================================
// 4. ADAPTERS
// ==========================================
// These classes bridge the gap. They implement the SmartDevice interface 
// but delegate the actual work to the third-party objects.

class OldSmartBulbAdapter implements SmartDevice {
    private OldSmartBulb bulb;

    public OldSmartBulbAdapter(OldSmartBulb bulb) {
        this.bulb = bulb;
    }

    @Override
    public void turnOn() {
        // Translates the standard call to the specific legacy method
        bulb.powerOn();
    }

    @Override
    public void turnOff() {
        bulb.powerOff();
    }
}

class LegacyHeaterAdapter implements SmartDevice {
    private LegacyHeater heater;

    public LegacyHeaterAdapter(LegacyHeater heater) {
        this.heater = heater;
    }

    @Override
    public void turnOn() {
        // Translates the standard call to the specific legacy method
        heater.startHeating();
    }

    @Override
    public void turnOff() {
        heater.stopHeating();
    }
}

// ==========================================
// 5. CLIENT CODE (The Smart Home App)
// ==========================================

public class SmartHomeApp {
    public static void main(String[] args) {
        // The app maintains a list of SmartDevices
        List<SmartDevice> devices = new ArrayList<>();

        // 1. Adding standard devices
        devices.add(new SmartLight());
        devices.add(new SmartFan());

        // 2. Integrating third-party devices using Adapters
        OldSmartBulb legacyBulb = new OldSmartBulb();
        devices.add(new OldSmartBulbAdapter(legacyBulb));

        LegacyHeater oldHeater = new LegacyHeater();
        devices.add(new LegacyHeaterAdapter(oldHeater));

        // 3. System execution: The app treats all devices uniformly
        System.out.println("--- Executing 'Turn On' command for all devices ---");
        for (SmartDevice device : devices) {
            device.turnOn();
        }

        System.out.println("\n--- Executing 'Turn Off' command for all devices ---");
        for (SmartDevice device : devices) {
            device.turnOff();
        }
    }
}

import java.util.*;


interface SmartDevice {
    void activate();

    void deactivate();

    double getPowerUsage();

    String getStatus();

    Class<?> getBaseType();
}


interface CompositeDevice extends SmartDevice {
    List<SmartDevice> getChildren();
}

class SmartLight implements SmartDevice {
    private boolean active = false;

    @Override public void activate()   { active = true; }
    @Override public void deactivate() { active = false; }
    @Override public double getPowerUsage() { return active ? 10.0 : 0.0; }
    @Override public String getStatus() { return "Light: " + (active ? "ON" : "OFF"); }
    @Override public Class<?> getBaseType() { return SmartLight.class; }
}

class SmartThermostat implements SmartDevice {
    private boolean active = false;

    @Override public void activate()   { active = true; }
    @Override public void deactivate() { active = false; }
    @Override public double getPowerUsage() { return active ? 150.0 : 0.0; }
    @Override public String getStatus() { return "Thermostat: " + (active ? "ON" : "OFF"); }
    @Override public Class<?> getBaseType() { return SmartThermostat.class; }
}

class SmartSpeaker implements SmartDevice {
    private boolean active = false;

    @Override public void activate()   { active = true; }
    @Override public void deactivate() { active = false; }
    @Override public double getPowerUsage() { return active ? 5.0 : 0.0; }
    @Override public String getStatus() { return "Speaker: " + (active ? "Playing" : "Idle"); }
    @Override public Class<?> getBaseType() { return SmartSpeaker.class; }
}


class Room implements CompositeDevice {
    private final String name;
    private final List<SmartDevice> devices = new ArrayList<>();

    Room(String name) { this.name = name; }

    /** Adds a device (plain or decorated) to this room. */
    void addDevice(SmartDevice device) { devices.add(device); }

    @Override public List<SmartDevice> getChildren() { return Collections.unmodifiableList(devices); }

    String getName() { return name; }

    @Override public void activate()   { for (SmartDevice d : devices) d.activate(); }
    @Override public void deactivate() { for (SmartDevice d : devices) d.deactivate(); }

    @Override public double getPowerUsage() {
        double total = 0;
        for (SmartDevice d : devices) total += d.getPowerUsage();
        return total;
    }

    @Override public String getStatus() {
        StringBuilder sb = new StringBuilder("[" + name + "]");
        for (SmartDevice d : devices) sb.append("\n  ").append(d.getStatus());
        return sb.toString();
    }

    @Override public Class<?> getBaseType() { return Room.class; }
}


class Home implements CompositeDevice {
    private final String name;
    private final List<SmartDevice> rooms = new ArrayList<>();

    Home(String name) { this.name = name; }

    void addRoom(SmartDevice room) { rooms.add(room); }

    @Override public List<SmartDevice> getChildren() { return Collections.unmodifiableList(rooms); }

    @Override public void activate()   { for (SmartDevice r : rooms) r.activate(); }
    @Override public void deactivate() { for (SmartDevice r : rooms) r.deactivate(); }

    @Override public double getPowerUsage() {
        double total = 0;
        for (SmartDevice r : rooms) total += r.getPowerUsage();
        return total;
    }

    @Override public String getStatus() {
        StringBuilder sb = new StringBuilder("=== " + name + " ===");
        for (SmartDevice r : rooms) sb.append("\n").append(r.getStatus());
        return sb.toString();
    }

    @Override public Class<?> getBaseType() { return Home.class; }
}


abstract class DeviceDecorator implements CompositeDevice {
    protected final SmartDevice wrapped;

    DeviceDecorator(SmartDevice wrapped) { this.wrapped = wrapped; }

    @Override public void activate()            { wrapped.activate(); }
    @Override public void deactivate()          { wrapped.deactivate(); }
    @Override public double getPowerUsage()     { return wrapped.getPowerUsage(); }
    @Override public String getStatus()         { return wrapped.getStatus(); }

    @Override public Class<?> getBaseType()     { return wrapped.getBaseType(); }

    @Override public List<SmartDevice> getChildren() {
        return (wrapped instanceof CompositeDevice)
            ? ((CompositeDevice) wrapped).getChildren()
            : Collections.emptyList();
    }
}


class AccessRestricted extends DeviceDecorator {
    private final int correctPin;
    private boolean locked = true;   // starts locked

    AccessRestricted(SmartDevice wrapped, int pin) {
        super(wrapped);
        this.correctPin = pin;
    }


    void unlock(int pin) {
        if (pin == correctPin) locked = false;
    }

    @Override public void activate()   { if (!locked) wrapped.activate(); }
    @Override public void deactivate() { if (!locked) wrapped.deactivate(); }

    @Override public String getStatus() {
        return locked ? wrapped.getStatus() + " [LOCKED]" : wrapped.getStatus();
    }

}


class TimerControlled extends DeviceDecorator {
    private final int timerSeconds;
    private boolean timerRunning = false;

    TimerControlled(SmartDevice wrapped, int timerSeconds) {
        super(wrapped);
        this.timerSeconds = timerSeconds;
    }

    @Override public void activate() {
        double powerBefore = wrapped.getPowerUsage();
        wrapped.activate();

        if (wrapped.getPowerUsage() > powerBefore) {
            timerRunning = true;
        }
    }

    @Override public void deactivate() {
        wrapped.deactivate();
        timerRunning = false;   // manual deactivate cancels the timer
    }

    void simulateTimerExpiry() {
        if (timerRunning) {
            wrapped.deactivate();
            timerRunning = false;
        }
    }

    @Override public String getStatus() {
        String s = wrapped.getStatus();
        if (timerRunning) s += " (auto-off in " + timerSeconds + "s)";
        return s;
    }
}


class PowerThrottled extends DeviceDecorator {
    private final double cap;

    PowerThrottled(SmartDevice wrapped, double cap) {
        super(wrapped);
        this.cap = cap;
    }

    @Override public double getPowerUsage() {
        return Math.min(cap, wrapped.getPowerUsage());
    }

    @Override public String getStatus() {
        String s = wrapped.getStatus();
        if (wrapped.getPowerUsage() > cap) s += " [throttled to " + cap + "W]";
        return s;
    }
}



class EcoMode implements CompositeDevice {
    private final CompositeDevice composite;
    private final double budget;

    EcoMode(CompositeDevice composite, double budget) {
        this.composite = composite;
        this.budget = budget;
    }

    @Override public void activate() {
        composite.activate();
        // Shed in reverse insertion order until within budget
        List<SmartDevice> children = composite.getChildren();
        for (int i = children.size() - 1; i >= 0 && getPowerUsage() > budget; i--) {
            children.get(i).deactivate();
        }
    }

    @Override public void deactivate() { composite.deactivate(); }

    @Override public double getPowerUsage() { return composite.getPowerUsage(); }

    @Override public List<SmartDevice> getChildren() { return composite.getChildren(); }

    @Override public String getStatus() {
        return "[ECO: " + budget + "W budget]\n" + composite.getStatus();
    }

    @Override public Class<?> getBaseType() { return EcoMode.class; }
}


class GuestMode implements CompositeDevice {
    private final CompositeDevice composite;
    private final Set<Class<?>> allowedTypes;

    GuestMode(CompositeDevice composite, Set<Class<?>> allowedTypes) {
        this.composite = composite;
        this.allowedTypes = allowedTypes;
    }

    private boolean isAllowed(SmartDevice device) {
        return allowedTypes.contains(device.getBaseType());
    }

    
    @Override public void activate() {
        for (SmartDevice d : composite.getChildren()) {
            if (d instanceof CompositeDevice && !((CompositeDevice) d).getChildren().isEmpty()) {
                new GuestMode((CompositeDevice) d, allowedTypes).activate();
            } else if (isAllowed(d)) {
                // Leaf device or decorator wrapping a leaf — check type and activate.
                d.activate();
            }
        }
    }

    @Override public void deactivate() { composite.deactivate(); }

    
    @Override public double getPowerUsage() {
        double total = 0;
        for (SmartDevice d : composite.getChildren()) {
            if (d instanceof CompositeDevice && !((CompositeDevice) d).getChildren().isEmpty()) {
                total += new GuestMode((CompositeDevice) d, allowedTypes).getPowerUsage();
            } else if (isAllowed(d)) {
                total += d.getPowerUsage();
            }
        }
        return total;
    }

    @Override public List<SmartDevice> getChildren() { return composite.getChildren(); }

    @Override public String getStatus() {
        String status = "[GUEST MODE]\n" + composite.getStatus();

        for (SmartDevice blocked : collectNonAllowedLeaves(composite)) {
            String deviceLine = blocked.getStatus();
            String tagged    = deviceLine + " [guest-restricted]";
            if (!status.contains(tagged)) {
                status = status.replaceFirst(
                    java.util.regex.Pattern.quote(deviceLine),
                    java.util.regex.Matcher.quoteReplacement(tagged)
                );
            }
        }
        return status;
    }

    
    private List<SmartDevice> collectNonAllowedLeaves(CompositeDevice cd) {
        List<SmartDevice> blocked = new ArrayList<>();
        for (SmartDevice d : cd.getChildren()) {
            if (d instanceof CompositeDevice && !((CompositeDevice) d).getChildren().isEmpty()) {
                // Real composite (or decorator-wrapping-composite) — recurse.
                blocked.addAll(collectNonAllowedLeaves((CompositeDevice) d));
            } else if (!isAllowed(d)) {
                // Leaf or decorator-wrapping-leaf — check type.
                blocked.add(d);
            }
        }
        return blocked;
    }

    @Override public Class<?> getBaseType() { return GuestMode.class; }
}

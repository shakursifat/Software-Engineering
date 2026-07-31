import java.util.*;

// ============================================================
//  SmartHome.java — Refactored Implementation
//  CSE 213 — Software Engineering, Assignment 2 (Structural DP)
//
//  Design Patterns Used:
//    • Composite  — SmartDevice interface unifies leaves (SmartLight,
//                   SmartThermostat, SmartSpeaker) and composites (Room, Home).
//    • Decorator  — AccessRestricted, TimerControlled, PowerThrottled wrap
//                   *any* SmartDevice (devices, rooms, even other decorators).
//                   EcoMode and GuestMode wrap Room specifically for
//                   compile-time safety (cannot be applied to a bare leaf).
//
//  SOLID Compliance:
//    SRP — Each class has one reason to change.
//    OCP — New device types and upgrades require no changes to existing code.
//    LSP — Every SmartDevice can be substituted for any other without
//           callers needing to know the concrete type.
//    ISP — The SmartDevice interface is minimal; no fat interfaces.
//    DIP — Room and Home depend on SmartDevice abstraction, not concretes.
// ============================================================


// ============================================================
//  COMPONENT INTERFACE
// ============================================================

/**
 * The uniform contract for every entity in the system:
 * a single device, a room, the entire home, or any upgraded version of any of these.
 *
 * This is the "Component" role in both the Composite and Decorator patterns.
 * Code that operates on a SmartLight works unchanged on a Room or an entire Home.
 */
interface SmartDevice {
    /** Activates this entity (and all children if composite). */
    void activate();

    /** Deactivates this entity (and all children if composite). */
    void deactivate();

    /** Returns current power draw in watts; 0 when inactive. */
    double getPowerUsage();

    /** Returns a human-readable status string for this entity. */
    String getStatus();

    /**
     * Returns the underlying concrete leaf-device class.
     * Leaf devices return their own class; decorators delegate down to the
     * wrapped component. This allows GuestMode to identify the real device
     * type through any number of decorator layers — without instanceof checks.
     */
    Class<?> getBaseType();
}


/**
 * CompositeDevice — marks an entity as a container of child SmartDevices.
 * Extends SmartDevice so composites are still usable everywhere a SmartDevice
 * is expected. EcoMode and GuestMode accept CompositeDevice so they can wrap
 * a Room, an entire Home, or even each other (GuestMode wrapping EcoMode, etc.),
 * while still rejecting bare leaf devices at compile time.
 */
interface CompositeDevice extends SmartDevice {
    /**
     * Returns the direct children of this composite in insertion order.
     * EcoMode uses this to shed in reverse order; GuestMode uses it to filter.
     */
    List<SmartDevice> getChildren();
}

// ============================================================
//  LEAF DEVICES  (Composite — leaf role)
// ============================================================

/** SmartLight: 10W when active. */
class SmartLight implements SmartDevice {
    private boolean active = false;

    @Override public void activate()   { active = true; }
    @Override public void deactivate() { active = false; }
    @Override public double getPowerUsage() { return active ? 10.0 : 0.0; }
    @Override public String getStatus() { return "Light: " + (active ? "ON" : "OFF"); }
    @Override public Class<?> getBaseType() { return SmartLight.class; }
}

/** SmartThermostat: 150W when active. */
class SmartThermostat implements SmartDevice {
    private boolean active = false;

    @Override public void activate()   { active = true; }
    @Override public void deactivate() { active = false; }
    @Override public double getPowerUsage() { return active ? 150.0 : 0.0; }
    @Override public String getStatus() { return "Thermostat: " + (active ? "ON" : "OFF"); }
    @Override public Class<?> getBaseType() { return SmartThermostat.class; }
}

/** SmartSpeaker: 5W when active. */
class SmartSpeaker implements SmartDevice {
    private boolean active = false;

    @Override public void activate()   { active = true; }
    @Override public void deactivate() { active = false; }
    @Override public double getPowerUsage() { return active ? 5.0 : 0.0; }
    @Override public String getStatus() { return "Speaker: " + (active ? "Playing" : "Idle"); }
    @Override public Class<?> getBaseType() { return SmartSpeaker.class; }
}


// ============================================================
//  COMPOSITE NODES
// ============================================================

/**
 * Room — holds any number of SmartDevice children in insertion order.
 * Implements CompositeDevice so it can be passed to EcoMode and GuestMode.
 * Cascades activate/deactivate to all children uniformly — no instanceof checks.
 */
class Room implements CompositeDevice {
    private final String name;
    private final List<SmartDevice> devices = new ArrayList<>();

    Room(String name) { this.name = name; }

    /** Adds a device (plain or decorated) to this room. */
    void addDevice(SmartDevice device) { devices.add(device); }

    /**
     * Returns an unmodifiable view of the device list in insertion order.
     * EcoMode uses this to shed in reverse order; GuestMode uses it to filter.
     */
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

/**
 * Home — top-level composite; aggregates rooms (or decorated rooms).
 * Implements CompositeDevice so EcoMode and GuestMode can be applied to an
 * entire home, not just individual rooms.
 * addRoom accepts any SmartDevice so that wrapped rooms can be added uniformly.
 */
class Home implements CompositeDevice {
    private final String name;
    private final List<SmartDevice> rooms = new ArrayList<>();

    Home(String name) { this.name = name; }

    /** Accepts any SmartDevice, including decorated rooms. */
    void addRoom(SmartDevice room) { rooms.add(room); }

    /** Exposes the room list so home-level EcoMode/GuestMode can iterate children. */
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


// ============================================================
//  DEVICE-LEVEL DECORATORS  (wrap any SmartDevice)
// ============================================================

/**
 * Abstract base for all device-level decorators.
 * Implements CompositeDevice so that a decorator wrapping a Room (e.g.
 * AccessRestricted(room) or PowerThrottled(room)) is still recognised as a
 * composite by EcoMode and GuestMode — fixing the composability and
 * decorator-masking bugs.
 *
 * getChildren() transparently delegates to the wrapped component when it is
 * itself a CompositeDevice (room / home / premium decorator) and returns an
 * empty list when it wraps a bare leaf device.  GuestMode uses the empty-list
 * result to distinguish "decorator wrapping a leaf" from "decorator wrapping
 * a composite", so it never recurses into a decorator that wraps a leaf.
 */
abstract class DeviceDecorator implements CompositeDevice {
    protected final SmartDevice wrapped;

    DeviceDecorator(SmartDevice wrapped) { this.wrapped = wrapped; }

    @Override public void activate()            { wrapped.activate(); }
    @Override public void deactivate()          { wrapped.deactivate(); }
    @Override public double getPowerUsage()     { return wrapped.getPowerUsage(); }
    @Override public String getStatus()         { return wrapped.getStatus(); }
    /** Delegates to the innermost leaf so GuestMode can detect device type. */
    @Override public Class<?> getBaseType()     { return wrapped.getBaseType(); }
    /**
     * Delegates to the wrapped entity's children when it is a CompositeDevice;
     * returns an empty list when wrapping a bare leaf device.
     * EcoMode and GuestMode use the empty result as the signal that this
     * decorator is wrapping a leaf, not a composite.
     */
    @Override public List<SmartDevice> getChildren() {
        return (wrapped instanceof CompositeDevice)
            ? ((CompositeDevice) wrapped).getChildren()
            : Collections.emptyList();
    }
}

// ─────────────────────────────────────────
//  Upgrade 1: AccessRestricted
// ─────────────────────────────────────────

/**
 * PIN-protects any SmartDevice (leaf or composite).
 *
 * • A locked entity silently ignores activate() and deactivate().
 * • Power is still reported if the device was already running before locking —
 *   locking blocks further control, not the electricity.
 * • Status is annotated with [LOCKED] while locked.
 */
class AccessRestricted extends DeviceDecorator {
    private final int correctPin;
    private boolean locked = true;   // starts locked

    AccessRestricted(SmartDevice wrapped, int pin) {
        super(wrapped);
        this.correctPin = pin;
    }

    /** Unlocks the device if the supplied PIN matches the registered PIN. */
    void unlock(int pin) {
        if (pin == correctPin) locked = false;
    }

    @Override public void activate()   { if (!locked) wrapped.activate(); }
    @Override public void deactivate() { if (!locked) wrapped.deactivate(); }

    @Override public String getStatus() {
        return locked ? wrapped.getStatus() + " [LOCKED]" : wrapped.getStatus();
    }

    // getPowerUsage() intentionally inherits the delegation from DeviceDecorator:
    // a locked-but-already-running device still draws and reports power.
}

// ─────────────────────────────────────────
//  Upgrade 2: TimerControlled
// ─────────────────────────────────────────

/**
 * Adds an automatic shutoff timer to any SmartDevice.
 *
 * • When activated, a countdown begins (simulated via simulateTimerExpiry()).
 * • When the timer expires, the device is automatically deactivated.
 * • Manual deactivate() cancels the timer.
 * • Status shows the remaining time while the timer is running.
 *
 * Note: NexaHome policy currently excludes SmartSpeaker from TimerControlled,
 * but that is a policy constraint enforced elsewhere — nothing here prevents
 * applying TimerControlled to a speaker if the policy changes.
 */
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
        // Only start the timer if the wrapped entity actually became active.
        // If activation was silently rejected (e.g. AccessRestricted while locked)
        // the power usage will not have changed, so the timer must not run.
        if (wrapped.getPowerUsage() > powerBefore) {
            timerRunning = true;
        }
    }

    @Override public void deactivate() {
        wrapped.deactivate();
        timerRunning = false;   // manual deactivate cancels the timer
    }

    /** Simulates timer expiry: auto-deactivates the wrapped entity if timer is running. */
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

// ─────────────────────────────────────────
//  Upgrade 3: PowerThrottled
// ─────────────────────────────────────────

/**
 * Caps the reported power draw of any SmartDevice to a specified maximum.
 *
 * The device operates normally (activate/deactivate unchanged); only the
 * power figure is clamped. This is useful for circuit-limited installations.
 *
 * Note: EcoMode and PowerThrottled are fundamentally different — EcoMode is an
 * aggregate budget constraint; PowerThrottled limits a single device's draw.
 */
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


// ============================================================
//  ROOM-LEVEL DECORATORS  (Premium Plan — wrap CompositeDevice specifically)
//
//  Taking CompositeDevice (not SmartDevice) in the constructor enforces at compile time
//  that these features cannot be accidentally applied to a bare leaf device.
// ============================================================

// ─────────────────────────────────────────
//  Premium 1: EcoMode
// ─────────────────────────────────────────

/**
 * EcoMode — Enforces a total power budget on any CompositeDevice
 * (a Room, an entire Home, or another room-level decorator).
 *
 * On activation:
 *   1. All children of the composite are activated normally.
 *   2. If total power exceeds the budget, children are deactivated in reverse
 *      insertion order (most-recently-added first) until the total fits.
 *
 * This is an aggregate budget constraint — NOT individual throttling.
 * A composite with three 60W devices under a 100W budget keeps one or two
 * active; it does NOT reduce each device to 33W (that is PowerThrottled's job).
 *
 * Constructor takes CompositeDevice so that:
 *   • {@code new EcoMode(new SmartLight(), 100)} is a compile-time error.
 *   • {@code new EcoMode(home, 500)} — home-level budget — works correctly.
 *   • {@code new EcoMode(new GuestMode(room, allowed), 100)} — stacked
 *     premium features — works correctly.
 */
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

    /** Reports actual power after shedding (never exceeds budget post-activate). */
    @Override public double getPowerUsage() { return composite.getPowerUsage(); }

    @Override public List<SmartDevice> getChildren() { return composite.getChildren(); }

    @Override public String getStatus() {
        return "[ECO: " + budget + "W budget]\n" + composite.getStatus();
    }

    @Override public Class<?> getBaseType() { return EcoMode.class; }
}

// ─────────────────────────────────────────
//  Premium 2: GuestMode
// ─────────────────────────────────────────

/**
 * GuestMode — Restricts which device types guests may operate, applied to any
 * CompositeDevice (a Room, an entire Home, or another room-level decorator).
 *
 * • Only devices whose base type is in the allowed set respond to activate().
 * • Non-allowed devices are silently skipped on activate.
 * • getPowerUsage() counts only allowed-type devices.
 * • getStatus() annotates non-allowed devices with [guest-restricted].
 * • Uses SmartDevice.getBaseType() to inspect through any decorator layers —
 *   an AccessRestricted(SmartThermostat) is still seen as SmartThermostat.
 *
 * Constructor takes CompositeDevice so that:
 *   • {@code new GuestMode(new SmartLight(), ...)} is a compile-time error.
 *   • {@code new GuestMode(home, allowed)} — home-level guest mode — works.
 *   • {@code new EcoMode(new GuestMode(room, allowed), budget)} — stacked
 *     premium features — works correctly.
 */
class GuestMode implements CompositeDevice {
    private final CompositeDevice composite;
    private final Set<Class<?>> allowedTypes;

    GuestMode(CompositeDevice composite, Set<Class<?>> allowedTypes) {
        this.composite = composite;
        this.allowedTypes = allowedTypes;
    }

    /** Returns true if this device's base type is in the allowed set. */
    private boolean isAllowed(SmartDevice device) {
        return allowedTypes.contains(device.getBaseType());
    }

    /**
     * Activates only allowed-type children.
     *
     * The recursion guard uses a non-empty getChildren() check rather than a
     * bare instanceof CompositeDevice check.  Because DeviceDecorator now
     * implements CompositeDevice, a decorator wrapping a leaf (e.g.
     * TimerControlled(light)) would also pass instanceof — but its getChildren()
     * returns an empty list, so we treat it as a leaf and check isAllowed()
     * instead of recursing (which would activate nothing).
     *
     * A decorator wrapping a composite (e.g. PowerThrottled(room)) has
     * non-empty children, so GuestMode correctly recurses into it.
     */
    @Override public void activate() {
        for (SmartDevice d : composite.getChildren()) {
            if (d instanceof CompositeDevice && !((CompositeDevice) d).getChildren().isEmpty()) {
                // Decorator-wrapped composite or raw composite — recurse.
                new GuestMode((CompositeDevice) d, allowedTypes).activate();
            } else if (isAllowed(d)) {
                // Leaf device or decorator wrapping a leaf — check type and activate.
                d.activate();
            }
        }
    }

    @Override public void deactivate() { composite.deactivate(); }

    /**
     * Sums power from allowed-type leaf children only.
     * Uses the same non-empty-children guard as activate() for the same reasons.
     */
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

    /** Exposes children so this GuestMode can itself be wrapped in EcoMode. */
    @Override public List<SmartDevice> getChildren() { return composite.getChildren(); }

    /**
     * Lets the wrapped composite build its full, correctly-formatted status
     * string (preserving room names, decorator headers like [ECO: ...], etc.),
     * then tags every non-allowed leaf device's status substring in-place with
     * [guest-restricted].  This avoids the two failure modes of manual string
     * slicing:
     *   • Stacking decorators (e.g. GuestMode wrapping EcoMode): no header is
     *     accidentally truncated.
     *   • Nested composites (rooms inside a home): only one [GUEST MODE] tag
     *     appears at the top, not one per room.
     */
    @Override public String getStatus() {
        // Let the composite generate its complete, well-structured status so
        // that room names, [ECO: ...] headers and decorator annotations are all
        // preserved — we only add [guest-restricted] on top.
        String status = "[GUEST MODE]\n" + composite.getStatus();

        for (SmartDevice blocked : collectNonAllowedLeaves(composite)) {
            String deviceLine = blocked.getStatus();
            String tagged    = deviceLine + " [guest-restricted]";
            // Guard: if the tag is already present (e.g. from an inner GuestMode
            // that already formatted this composite's status), do not double-tag.
            if (!status.contains(tagged)) {
                status = status.replaceFirst(
                    java.util.regex.Pattern.quote(deviceLine),
                    java.util.regex.Matcher.quoteReplacement(tagged)
                );
            }
        }
        return status;
    }

    /**
     * Recursively collects all non-allowed leaf SmartDevices.
     * Uses the same non-empty-children guard as activate() / getPowerUsage():
     * a decorator wrapping a leaf has empty getChildren() and is treated as a
     * leaf here too, so it is correctly included in (or excluded from) the
     * blocked set based on its base type.
     */
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

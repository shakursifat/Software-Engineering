# Smart Home Automation Hub — Refactor Implementation Plan

## Background

The existing `SmartHomeSpaghettiDemo.java` uses separate, unrelated `Light`, `Thermostat`, and `Speaker` classes with copy-pasted flag fields for upgrades. `MessyRoom` holds three separate typed lists and has `instanceof` checks everywhere. The design collapses when trying to apply upgrades to a room as a whole.

The task is to produce a clean `SmartHome.java` that passes all tests in `SmartHomeTestRunner.java` (which is **not to be modified**).

---

## Design Patterns Used

| Pattern | Where Applied |
|---|---|
| **Composite** | `SmartDevice` (interface) → `Room` and `Home` treat children uniformly |
| **Decorator** | `AccessRestricted`, `TimerControlled`, `PowerThrottled` wrap any `SmartDevice` |
| **Decorator (Premium)** | `EcoMode`, `GuestMode` wrap a `Room` (or any composite) |

---

## Key Constraints Derived From Test Runner

From reading `SmartHomeTestRunner.java`:

1. **Interface**: `SmartDevice` with methods `activate()`, `deactivate()`, `getPowerUsage()`, `getStatus()`
2. **Leaf devices**: `SmartLight` (10W), `SmartThermostat` (150W), `SmartSpeaker` (5W)
3. **Room**: `Room(String name)` with `addDevice(SmartDevice)` — single typed list, no special-casing
4. **Home**: `Home(String name)` with `addRoom(SmartDevice)` — accepts upgraded rooms too (test `testUpgradedRoomAddableToHome`)
5. **Decorators**: `AccessRestricted(SmartDevice, int pin)`, `TimerControlled(SmartDevice, int seconds)`, `PowerThrottled(SmartDevice, double cap)`
6. **`TimerControlled`**: has `simulateTimerExpiry()` method
7. **`AccessRestricted`**: has `unlock(int pin)` method
8. **Premium**: `EcoMode(Room, double budget)` — takes `Room` specifically (compile-time safety), `GuestMode(Room, Set<Class<?>>)` — takes `Room` specifically
9. **GuestMode**: checks actual device class type (not the wrapper) — `allowed` set contains `SmartLight.class` etc.
10. **GuestMode power reporting**: only counts allowed-type devices' power
11. **EcoMode**: after activate, sheds devices in reverse insertion order until within budget

---

## Important Design Decisions

### GuestMode type checking
`GuestMode` receives `Set<Class<?>> allowed` (e.g., `SmartLight.class`). When iterating room devices, it needs to determine if a device (possibly wrapped in a decorator) is of an allowed base type. The wrapper's class won't match. We need to unwrap or use a "type tag" approach.

**Solution**: Add a `getDeviceType()` method to `SmartDevice`, or alternatively, use an `instanceof` hierarchy. However, since decorators wrap devices, we need decorators to delegate `getDeviceType()` to their wrapped component. Leaf devices return their own class. This is clean.

> [!IMPORTANT]
> The test `testGuestModeWithMixedEnhancements` adds `new AccessRestricted(thermo, 9999)` and `new TimerControlled(light, 120)` to the room. When `GuestMode` activates, it checks if the device is of allowed type (`SmartLight.class`, `SmartSpeaker.class`). The raw `thermo` is `SmartThermostat` (blocked) but wrapped in `AccessRestricted`. The raw `light` is `SmartLight` (allowed) but wrapped in `TimerControlled`. So `GuestMode` must unwrap to check base type.

### EcoMode takes Room specifically
Test `testEcoModeRejectsLeafAtCompileTime` documents: `new EcoMode(new SmartLight(), 100)` should NOT compile. So `EcoMode` constructor takes `Room` (not `SmartDevice`). Same for `GuestMode`.

### EcoMode shed order
EcoMode sheds devices in reverse insertion order. It must call `deactivate()` on the child devices inside the room. Room needs to expose its device list for this purpose.

### Home.addRoom accepts SmartDevice
Test `testUpgradedRoomAddableToHome` calls `h.addRoom(upgraded)` where `upgraded` is a `TimerControlled(AccessRestricted(r, 0), 3600)`. So `Home.addRoom` must accept `SmartDevice`, not `Room`.

---

## Proposed Class Structure

```
SmartDevice (interface)
├── activate()
├── deactivate()
├── getPowerUsage() : double
├── getStatus() : String
└── getBaseType() : Class<?>   ← for GuestMode type-checking

SmartLight implements SmartDevice
SmartThermostat implements SmartDevice
SmartSpeaker implements SmartDevice

Room implements SmartDevice
├── addDevice(SmartDevice)
├── getDevices() : List<SmartDevice>   ← needed by EcoMode/GuestMode
└── ... delegates to children

Home implements SmartDevice
├── addRoom(SmartDevice)  ← accepts any SmartDevice
└── ... aggregates rooms

// Device-level Decorators (wrap any SmartDevice)
DeviceDecorator (abstract) implements SmartDevice
├── AccessRestricted extends DeviceDecorator
│   └── unlock(int pin)
├── TimerControlled extends DeviceDecorator
│   └── simulateTimerExpiry()
└── PowerThrottled extends DeviceDecorator

// Room-level Decorators (wrap Room specifically)
EcoMode implements SmartDevice
└── EcoMode(Room, double budget)

GuestMode implements SmartDevice
└── GuestMode(Room, Set<Class<?>>)
```

---

## Proposed Changes

### [NEW] SmartHome.java

All classes in a single file (as required by assignment: "refactor SmartHome.java only").

#### Classes to implement:

**`SmartDevice` (interface)**
- `void activate()`
- `void deactivate()`
- `double getPowerUsage()`
- `String getStatus()`
- `Class<?> getBaseType()` — leaf returns own class, decorators delegate to wrapped

**`SmartLight`** — 10W, status: "Light: ON/OFF"

**`SmartThermostat`** — 150W, status: "Thermostat: ON/OFF"

**`SmartSpeaker`** — 5W, status: "Speaker: Playing/Idle"

**`Room`** — holds `List<SmartDevice>`, cascades all ops, exposes `getDevices()`

**`Home`** — holds `List<SmartDevice>` (rooms or upgraded rooms), cascades all ops

**`DeviceDecorator` (abstract)** — wraps `SmartDevice`, delegates everything

**`AccessRestricted extends DeviceDecorator`**
- Starts locked
- `unlock(int pin)` — sets locked=false if pin matches
- `activate()`/`deactivate()` — ignored if locked
- `getStatus()` — appends `[LOCKED]` if locked
- Power — delegates (locked device that was already on still reports power)

**`TimerControlled extends DeviceDecorator`**
- `activate()` — delegates + sets timerRunning=true
- `deactivate()` — delegates + cancels timer
- `simulateTimerExpiry()` — calls deactivate on wrapped if timer running
- `getStatus()` — appends `(auto-off in Xs)` if timer running

**`PowerThrottled extends DeviceDecorator`**
- `getPowerUsage()` — `Math.min(cap, wrapped.getPowerUsage())`
- `getStatus()` — appends `[throttled to Xw]` if actual > cap

**`EcoMode implements SmartDevice`** — wraps `Room`
- `activate()`:
  1. Call `room.activate()`
  2. While `getPowerUsage() > budget`: shed last device (call `deactivate()`)
- `getPowerUsage()` — delegates to room (actual power after shedding)
- `getStatus()` — prepends `[ECO: Xw budget]`

**`GuestMode implements SmartDevice`** — wraps `Room`
- `activate()` — only activates allowed-type devices in room
- `deactivate()` — deactivates all
- `getPowerUsage()` — sum only allowed-type devices
- `getStatus()` — marks non-allowed as `[guest-restricted]`

---

## Verification Plan

### Automated Tests
```bash
cd "c:\Users\Sifat\Academics\Software Engineering\Offlines\Assignment 2 on Structural DP\Code"
javac SmartHome.java SmartHomeTestRunner.java
java SmartHomeTestRunner
```
All 40+ tests must pass with `0 failed`.

### Manual Verification
- Confirm `new EcoMode(new SmartLight(), 100)` would not compile (constructor takes `Room`)
- Confirm upgrades stack: `TimerControlled(AccessRestricted(PowerThrottled(device)))` works
- Confirm order sensitivity: throttled-then-eco vs raw-eco produce different results

---

## Open Questions

None — the test runner is explicit about all required class names, method signatures, and behaviors.

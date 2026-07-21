# FoodFlow – Creational Design Pattern Refactoring

## Problem Summary

FoodFlow is a working Java food-ordering CLI app. The object-creation flow for `Order` has four concrete design problems that the assignment wants fixed using the **Builder pattern**.

---

## The Four Design Issues (Detailed)

### Issue 1 – Long Constructor with Many Optional Parameters

`Order` has a single 14-parameter constructor:

```java
new Order(orderId, customerName, phone, deliveryType, deliveryAddress,
          paymentMethod, scheduledTime, couponCode, giftWrap,
          cutleryRequired, loyaltyPointsToRedeem, rushOrder,
          items, specialInstructions)
```

- 14 arguments in one call — unreadable at a glance.
- Most parameters are optional (null / false / 0 / empty string by default).
- Every call site must supply all 14 values even when only 3 are meaningful.

### Issue 2 – Repeated Default Values

In `OrderService`, the same defaults appear in every factory method:

| Default | Where it appears |
|---|---|
| `PaymentMethod.CASH` | `createDeliveryOrder`, `createPickupOrder` |
| `""` (no coupon) | `createDeliveryOrder`, `createPickupOrder` |
| `false` (no gift wrap) | `createDeliveryOrder`, `createPickupOrder`, `createSampleFamilyOrder` |
| `true` (cutlery required) | `createDeliveryOrder`, `createPickupOrder`, `createSampleFamilyOrder` |
| `0` (no loyalty points) | `createDeliveryOrder`, `createPickupOrder` |
| `false` (no rush order) | `createPickupOrder` |
| `""` (no instructions) | `createPickupOrder` |
| `null` (no scheduled time) | `createDeliveryOrder`, `createPickupOrder`, `createSampleFamilyOrder` |

If any default policy changes (e.g., loyalty points default to 10), it must be updated in many places.

### Issue 3 – Fragile Parameter Ordering

Adjacent parameters of the same type:
- `String couponCode`, `boolean giftWrap`, `boolean cutleryRequired` — two booleans in a row
- `boolean rushOrder` at position 12 — far from related `boolean giftWrap` at position 9
- Swapping `giftWrap` and `cutleryRequired` **compiles fine** but produces a silently wrong order

### Issue 4 – Mixed Responsibilities in Constructor

The `Order` constructor does too many things at once:
1. Validates required fields (`requireNonBlank`)
2. Normalizes optional strings (trim, uppercase for coupon)
3. Decides defaults (null → PICKUP, null → CASH, etc.)
4. Copies item list to unmodifiable list
5. Stores all fields

These concerns should be separated — ideally validation and defaulting happen in a Builder's `build()` method, and the `Order` constructor just stores already-validated data.

---

## Solution: Builder Pattern on `Order`

The **Builder pattern** is the textbook solution here:

- Encapsulates all optional parameters with named setter methods → readable call sites
- Centralizes all default values in one place (the Builder fields)
- Eliminates positional errors — named methods can't be accidentally swapped
- Builder's `build()` method handles validation and normalization, keeping `Order`'s constructor simple
- Required fields (`customerName`, `phone`, `items`) can be enforced by the Builder's `build()` or by a constructor that takes only those required fields

---

## Proposed Changes

### Component 1: `model/` — Add `Order.Builder`

#### [MODIFY] [Order.java](file:///c:/Users/Sifat/Academics/Software%20Design%20Pattern/Offline_1_Creational_DP/jan2026-cse-214-offline-1-creational-design-pattern/src/model/Order.java)

- Add a public static inner class `Order.Builder`
- Builder constructor takes the **required** fields: `orderId`, `customerName`, `phone`, `items`
- Builder has named fluent setter methods for every optional field:
  - `deliveryType(DeliveryType)`, `deliveryAddress(String)`, `paymentMethod(PaymentMethod)`
  - `scheduledTime(LocalDateTime)`, `couponCode(String)`, `giftWrap(boolean)`
  - `cutleryRequired(boolean)`, `loyaltyPoints(int)`, `rushOrder(boolean)`, `specialInstructions(String)`
- Default values live **only in the Builder** field declarations
- `build()` validates delivery address (if type is DELIVERY) and item count, then calls the `Order` constructor
- The `Order` constructor becomes a simple field-assignment constructor (no logic)
- Keep the existing short 4-arg convenience constructor **or** remove it if the Builder replaces it

```java
// Before (call site in OrderService):
new Order(nextOrderId(), customerName, phone,
          DeliveryType.DELIVERY, address, PaymentMethod.CASH,
          null, couponCode, false, true, 0, rushOrder,
          items, specialInstructions);

// After:
new Order.Builder(nextOrderId(), customerName, phone, items)
        .deliveryType(DeliveryType.DELIVERY)
        .deliveryAddress(address)
        .couponCode(couponCode)
        .rushOrder(rushOrder)
        .specialInstructions(specialInstructions)
        .build();
```

---

### Component 2: `service/OrderService.java` — Use Builder at Every Call Site

#### [MODIFY] [OrderService.java](file:///c:/Users/Sifat/Academics/Software%20Design%20Pattern/Offline_1_Creational_DP/jan2026-cse-214-offline-1-creational-design-pattern/src/service/OrderService.java)

Replace all `new Order(...)` calls with `Order.Builder` chains:

| Method | What changes |
|---|---|
| `createDeliveryOrder` | Uses Builder, sets only DELIVERY-specific fields |
| `createPickupOrder` | Uses Builder, sets only PICKUP-specific fields (barely any!) |
| `createScheduledGiftOrder` | Uses Builder, clearly names scheduledTime, giftWrap, loyaltyPoints |
| `createSampleFamilyOrder` | Uses Builder, readable even with 14 fields set |

---

### Component 3: `cli/CommandHandler.java` — Update any direct `Order` construction (if any)

#### [MODIFY] [CommandHandler.java](file:///c:/Users/Sifat/Academics/Software%20Design%20Pattern/Offline_1_Creational_DP/jan2026-cse-214-offline-1-creational-design-pattern/src/cli/CommandHandler.java)

If `CommandHandler` directly constructs `Order` objects (likely via `OrderService`), no changes may be needed. If it creates them directly, update to use Builder.

---

## What Does NOT Change

| Item | Why |
|---|---|
| All `Order` getters | `TestHarness` calls them; behavior must be identical |
| Pricing formulas (`getSubtotal`, `getDiscount`, etc.) | Spec explicitly forbids changes |
| Validation rules (non-blank address for DELIVERY, non-empty items) | Spec requires these preserved |
| `TestHarness.java` | Must not be modified |
| `OrderService` public method signatures | `TestHarness` calls `createDeliveryOrder`, `createScheduledGiftOrder`, etc. directly |

---

## Verification Plan

### Automated Tests
```
javac -d out src/**/*.java TestHarness.java   # (Linux/macOS)
java -cp out TestHarness
```
All 6 tests must pass:
- Test 1: Menu loading
- Test 2: Order item pricing
- Test 3: Delivery order pricing
- Test 4: Scheduled gift order pricing
- Test 5: Sample order and receipt
- Test 6: Validation (delivery order without address rejected)

### Manual Verification
- Run the interactive app: `java -cp out Main data/menu.csv`
- Try all sample commands from the spec
- Confirm receipt file is written to `out/receipt.txt`

---

## Open Questions

> [!IMPORTANT]
> Should `OrderItem` also get a Builder? The spec focuses on `Order`, and `OrderItem`'s constructor has only 6 parameters (less problematic). The safest approach is to leave `OrderItem` as-is and only apply the Builder to `Order`.

> [!NOTE]
> The `Order` class currently has a short 4-arg constructor `(orderId, name, phone, items)` used as a convenience. After introducing the Builder, this can either be kept (backward compatibility) or removed (since the Builder replaces it). Removing it is cleaner, but only valid if no code outside the refactored files calls it.


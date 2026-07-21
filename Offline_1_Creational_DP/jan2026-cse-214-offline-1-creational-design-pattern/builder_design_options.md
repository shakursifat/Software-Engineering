# Builder Design Options for FoodFlow's Order Class

When implementing the Builder pattern in Java (and similar languages like C++), there are two common structural approaches:

1. **Nested Inner Class** (The approach we just implemented for `Order`)
2. **Friend Class / Package-Private Class** (A standalone builder class in the same package)

Here is a detailed comparison of both approaches in the context of the FoodFlow `Order` problem.

---

## 1. Nested Inner Class Builder (The Standard / Best Practice in Java)

In this approach, the `Builder` is a `public static class` nested *inside* the `Order` class. The `Order` constructor is made `private`.

### How it looks in FoodFlow:
```java
public class Order {
    private final String orderId;
    // ... other 13 fields ...

    // Private constructor: ONLY the inner Builder can call this
    private Order(Builder b) {
        this.orderId = b.orderId;
        // ...
    }

    public static class Builder {
        private String orderId; // ...
        
        public Order build() {
            return new Order(this);
        }
    }
}
```

### Advantages (Why it's the best option here):
* **Perfect Encapsulation:** Because the `Builder` is an inner class, it has access to the `private` constructor of `Order`. This means **no one else** can create an `Order` except the `Builder`. We guarantee that an `Order` can never be partially constructed or bypass the validation in `build()`.
* **Namespace Cleanliness:** The builder is logically scoped to the class it builds. You use `Order.Builder`, which immediately tells the developer exactly what this builder is for. It doesn't pollute the `model` package namespace with an `OrderBuilder` class.
* **Immutability Guarantee:** We can make all fields in `Order` `final` and initialize them directly in the private constructor. Once `build()` is called, the `Order` is completely immutable.

### Disadvantages:
* **File Size:** The `Order.java` file becomes quite long because it contains both the model data/methods (pricing logic) and the builder logic.

---

## 2. "Friend" / Package-Private Class Builder

Java doesn't have a strict `friend` keyword like C++, but it achieves the same thing using **package-private** visibility (no access modifier). In this approach, `OrderBuilder` is a separate class in the same `model` package.

### How it looks in FoodFlow:
**File 1: model/Order.java**
```java
public class Order {
    private final String orderId;
    // ...

    // Package-private constructor: Classes in the 'model' package can call this
    Order(String orderId, String customerName, /* ... all 14 args ... */) {
        this.orderId = orderId;
        // ...
    }
}
```

**File 2: model/OrderBuilder.java**
```java
public class OrderBuilder {
    private String orderId;
    // ...

    public Order build() {
        // Can call the package-private Order constructor
        return new Order(orderId, customerName, /* ... */);
    }
}
```

### Advantages:
* **Separation of Concerns (Smaller Files):** The `Order` class only contains the business logic (like `getSubtotal()` and `getDiscount()`), while `OrderBuilder` handles the complex construction logic. This makes both files shorter and easier to read.
* **Code Generation:** It's sometimes easier for automated tools or frameworks to generate standalone builder classes rather than inner classes.

### Disadvantages (Why it's worse for FoodFlow):
* **Weakened Encapsulation (The fatal flaw):** To allow `OrderBuilder` to create an `Order`, the `Order` constructor must be at least **package-private**. This means *any* other class in the `model` package (like `OrderItem` or a hypothetical `OrderManager`) could accidentally bypass the builder and call the long, error-prone 14-argument constructor directly. It defeats the primary goal of the assignment: forcing safe object creation.
* **Namespace Clutter:** You now have an `OrderBuilder.java` file sitting next to `Order.java`, which adds clutter to the project structure.

---

## Conclusion: Which is best for this assignment?

For the FoodFlow `Order` problem, **Option 1 (Nested Inner Class) is unequivocally the best choice.**

The assignment explicitly states:
> *"The required fields such as order id... [must be] clear and difficult to omit accidentally."*
> *"Centralize default values and validation rules instead of scattering them"*

The nested inner class is the *only* way in Java to enforce that 100% of `Order` creation goes through the Builder's validation logic, by keeping the `Order` constructor strictly `private`. If we used a package-private separate class, we would leave a "backdoor" open in the `model` package.

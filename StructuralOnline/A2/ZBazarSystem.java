import java.util.ArrayList;
import java.util.List;

// ==========================================
// 1. COMPONENT INTERFACE
// ==========================================
// This interface allows the system to treat single items and 
// nested packages exactly the same way.

interface BazarComponent {
    double getPrice();
    double getWeight();
    void displayStructure(String indent);
}

// ==========================================
// 2. LEAF NODE (Single Items)
// ==========================================
// Represents individual grocery items like rice, oil, or pulse.

class SingleItem implements BazarComponent {
    private String name;
    private double price;
    private double weight;

    // Each item has its specific name, price, and weight.
    public SingleItem(String name, double price, double weight) {
        this.name = name;
        this.price = price;
        this.weight = weight;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void displayStructure(String indent) {
        System.out.printf("%s- %s ($%.2f, %.2f kg)%n", indent, name, price, weight);
    }
}

// ==========================================
// 3. COMPOSITE NODE (Packages)
// ==========================================
// Represents both Preset Packages and Custom Bazars. It can hold single 
// items, other packages, or a mixture of both.

class GroceryPackage implements BazarComponent {
    private String packageName;
    private List<BazarComponent> components = new ArrayList<>();

    public GroceryPackage(String packageName) {
        this.packageName = packageName;
    }

    public void addComponent(BazarComponent component) {
        components.add(component);
    }

    public void removeComponent(BazarComponent component) {
        components.remove(component);
    }

    @Override
    public double getPrice() {
        double total = 0;
        for (BazarComponent component : components) {
            total += component.getPrice(); // Recursive call
        }
        return total;
    }

    @Override
    public double getWeight() {
        double total = 0;
        for (BazarComponent component : components) {
            total += component.getWeight(); // Recursive call
        }
        return total;
    }

    @Override
    public void displayStructure(String indent) {
        System.out.println(indent + "[Package] " + packageName);
        for (BazarComponent component : components) {
            component.displayStructure(indent + "   "); // Increase indent for children
        }
    }
}

// ==========================================
// 4. CLIENT CODE (Demonstration)
// ==========================================

public class ZBazarSystem {

    public static void printCheckoutDetails(BazarComponent order) {
        System.out.println("=========================================");
        System.out.println("ORDER STRUCTURE:");
        order.displayStructure("");
        System.out.println("-----------------------------------------");
        System.out.printf("Total Weight: %.2f kg%n", order.getWeight());
        System.out.printf("Total Price:  $%.2f%n", order.getPrice());
        System.out.println("=========================================\n");
    }

    public static void main(String[] args) {
        // 1. Create Individual Items
        BazarComponent rice = new SingleItem("Rice", 15.00, 5.0);
        BazarComponent oil = new SingleItem("Cooking Oil", 8.50, 2.0);
        BazarComponent pulse = new SingleItem("Pulse", 4.00, 1.0);
        BazarComponent sugar = new SingleItem("Sugar", 3.00, 1.0);

        // 2. Create Preset Packages
        GroceryPackage smallPreset = new GroceryPackage("Small Preset Package");
        smallPreset.addComponent(rice);
        smallPreset.addComponent(pulse);

        GroceryPackage familyPreset = new GroceryPackage("Family Preset Package");
        familyPreset.addComponent(rice);
        familyPreset.addComponent(rice); // Double rice for family
        familyPreset.addComponent(oil);
        familyPreset.addComponent(pulse);
        familyPreset.addComponent(sugar);

        // 3. Create a Custom Bazar (Mixture of single items and packages)
        GroceryPackage myCustomBazar = new GroceryPackage("My Monthly Custom Bazar");
        
        // Users can combine preset packages and single items
        myCustomBazar.addComponent(smallPreset); 
        myCustomBazar.addComponent(oil); // Adding a single item outside the preset
        
        // 4. Create an Advanced Custom Bazar containing a previously created custom package
        GroceryPackage megaHolidayBazar = new GroceryPackage("Mega Holiday Delivery");
        megaHolidayBazar.addComponent(familyPreset);
        megaHolidayBazar.addComponent(myCustomBazar); // Nesting a custom package inside another
        megaHolidayBazar.addComponent(new SingleItem("Premium Chocolate", 12.00, 0.5));

        // Display results
        System.out.println("Testing Custom Bazar (Preset + Single Item):");
        printCheckoutDetails(myCustomBazar);

        System.out.println("Testing Mega Holiday Bazar (Nested Packages):");
        printCheckoutDetails(megaHolidayBazar);
    }
}

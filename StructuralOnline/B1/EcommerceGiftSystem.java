import java.util.ArrayList;
import java.util.List;

// ==========================================
// 1. BRIDGE PATTERN: IMPLEMENTOR HIERARCHY
// ==========================================
// Represents the packaging styles which determine cost and presentation.

interface PackagingStyle {
    double getPackagingCost();
    String getPresentation();
}

class StandardGiftBox implements PackagingStyle {
    @Override
    public double getPackagingCost() { 
        return 0.0; // Adds no extra cost[cite: 2]
    }
    @Override
    public String getPresentation() { 
        return "Packed in a standard gift box"; //[cite: 2]
    }
}

class PremiumGiftBox implements PackagingStyle {
    @Override
    public double getPackagingCost() { 
        return 15.0; // Adds $15[cite: 2]
    }
    @Override
    public String getPresentation() { 
        return "Premium wrapping with a decorative ribbon"; //[cite: 2]
    }
}

class EcoFriendlyGiftBox implements PackagingStyle {
    @Override
    public double getPackagingCost() { 
        return 8.0; // Adds $8[cite: 2]
    }
    @Override
    public String getPresentation() { 
        return "Uses recyclable materials"; //[cite: 2]
    }
}

// ==========================================
// 2. COMPOSITE PATTERN: COMPONENT HIERARCHY
// ==========================================

interface GiftComponent {
    double getCost();
    String getDescription();
}

// Leaf Node: Individual Gift Items
class SingleGiftItem implements GiftComponent {
    private String name;
    private double price;

    public SingleGiftItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public double getCost() { 
        return price; 
    }
    
    @Override
    public String getDescription() { 
        return "- " + name + " ($" + price + ")"; 
    }
}

// Composite Node: Packages containing components and bridged to a PackagingStyle
abstract class GiftPackage implements GiftComponent {
    protected String packageName;
    protected PackagingStyle packagingStyle;
    protected List<GiftComponent> components = new ArrayList<>();

    public GiftPackage(String packageName, PackagingStyle packagingStyle) {
        this.packageName = packageName;
        this.packagingStyle = packagingStyle;
    }

    public void addComponent(GiftComponent component) {
        components.add(component);
    }

    public void removeComponent(GiftComponent component) {
        components.remove(component);
    }

    @Override
    public double getCost() {
        double totalCost = packagingStyle.getPackagingCost();
        for (GiftComponent component : components) {
            totalCost += component.getCost();
        }
        return totalCost;
    }

    protected String getBaseDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(packageName).append(" [").append(packagingStyle.getPresentation()).append("]\n");
        for (GiftComponent component : components) {
            // Indent components for readability
            String[] lines = component.getDescription().split("\n");
            for (String line : lines) {
                sb.append("   ").append(line).append("\n");
            }
        }
        return sb.toString().trim();
    }
}

// ==========================================
// 3. REFINED ABSTRACTIONS (Specific Packages)
// ==========================================

class CompanyPackage extends GiftPackage {
    public CompanyPackage(String packageName) {
        // Company packages always use standard gift boxes[cite: 2]
        super(packageName, new StandardGiftBox());
    }

    @Override
    public String getDescription() {
        return "Company Package: " + getBaseDescription();
    }
}

abstract class UserCraftedPackage extends GiftPackage {
    protected String creatorName;

    public UserCraftedPackage(String packageName, String creatorName, PackagingStyle packagingStyle) {
        super(packageName, packagingStyle);
        this.creatorName = creatorName;
    }
}

class PersonalGiftPackage extends UserCraftedPackage {
    public PersonalGiftPackage(String packageName, String creatorName, PackagingStyle packagingStyle) {
        super(packageName, creatorName, packagingStyle);
    }

    @Override
    public String getDescription() {
        return "Personal Gift Package by " + creatorName + " (For Individual Recipient):\n   " + getBaseDescription(); //[cite: 2]
    }
}

class CorporateGiftPackage extends UserCraftedPackage {
    public CorporateGiftPackage(String packageName, String creatorName, PackagingStyle packagingStyle) {
        super(packageName, creatorName, packagingStyle);
    }

    @Override
    public String getDescription() {
        return "Corporate Gift Package by " + creatorName + " (For Organization Employees):\n   " + getBaseDescription(); //[cite: 2]
    }
}

// ==========================================
// 4. CLIENT CODE
// ==========================================

public class EcommerceGiftSystem {

    public static void printPackageDetails(GiftComponent pkg) {
        System.out.println(pkg.getDescription());
        System.out.println("Total Cost: $" + pkg.getCost());
        System.out.println("-------------------------------------------------");
    }

    public static void main(String[] args) {
        // Create Individual Items[cite: 2]
        GiftComponent chocolates = new SingleGiftItem("Luxury Chocolates", 20.0);
        GiftComponent mug = new SingleGiftItem("Coffee Mug", 10.0);
        GiftComponent perfume = new SingleGiftItem("French Perfume", 55.0);
        GiftComponent book = new SingleGiftItem("Bestseller Book", 15.0);
        GiftComponent flowers = new SingleGiftItem("Bouquet of Roses", 25.0);

        // 1. Create a Company Predefined Package
        GiftPackage companyCoffeeKit = new CompanyPackage("Morning Coffee Kit");
        companyCoffeeKit.addComponent(mug);
        companyCoffeeKit.addComponent(chocolates);
        
        System.out.println("--- PRE-DEFINED COMPANY PACKAGE ---");
        printPackageDetails(companyCoffeeKit);

        // 2. Create a User-Crafted Personal Gift Package
        // Contains two items and uses Eco-Friendly packaging[cite: 2]
        GiftPackage personalPackage = new PersonalGiftPackage(
            "Anniversary Special", 
            "Alice", 
            new EcoFriendlyGiftBox()
        );
        personalPackage.addComponent(perfume);
        personalPackage.addComponent(flowers);

        System.out.println("--- USER-CRAFTED PERSONAL PACKAGE ---");
        printPackageDetails(personalPackage);

        // 3. Create a User-Crafted Corporate Gift Package
        // Demonstrates nested packages: contains individual items AND an existing company package[cite: 2]
        GiftPackage corporatePackage = new CorporateGiftPackage(
            "Employee Appreciation Bundle", 
            "Bob", 
            new PremiumGiftBox()
        );
        corporatePackage.addComponent(book);
        corporatePackage.addComponent(companyCoffeeKit); // Nesting the predefined package

        System.out.println("--- USER-CRAFTED CORPORATE PACKAGE (NESTED) ---");
        printPackageDetails(corporatePackage);
    }
}

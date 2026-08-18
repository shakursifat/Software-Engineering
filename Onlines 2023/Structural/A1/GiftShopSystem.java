// ==========================================
// 1. BRIDGE PATTERN: IMPLEMENTOR HIERARCHY
// ==========================================
// This defines the regions. We use a simple enum to help the Delivery Modes 
// determine standard vs. international timeframes cleanly.

enum RegionCategory { DOMESTIC, INTERNATIONAL }

interface DeliveryRegion {
    double calculateBaseCost(int miles);
    String getStandardTime();
    RegionCategory getCategory();
}

class LocalDelivery implements DeliveryRegion {
    @Override
    public double calculateBaseCost(int miles) {
        return miles * 1.0; // $1 per mile
    }
    @Override
    public String getStandardTime() { return "1 week"; } //
    @Override
    public RegionCategory getCategory() { return RegionCategory.DOMESTIC; }
}

class NationalDelivery implements DeliveryRegion {
    @Override
    public double calculateBaseCost(int miles) {
        return (miles * 1.0) + 20.0; // $1 per mile + $20 surcharge
    }
    @Override
    public String getStandardTime() { return "1-2 weeks"; } //[cite: 1]
    @Override
    public RegionCategory getCategory() { return RegionCategory.DOMESTIC; }
}

class InternationalDelivery implements DeliveryRegion {
    @Override
    public double calculateBaseCost(int miles) {
        return 500.0; // Fixed $500 surcharge, distance ignored[cite: 1]
    }
    @Override
    public String getStandardTime() { return "2-3 weeks"; } //[cite: 1]
    @Override
    public RegionCategory getCategory() { return RegionCategory.INTERNATIONAL; }
}

// ==========================================
// 2. BRIDGE PATTERN: ABSTRACTION HIERARCHY
// ==========================================
// This defines the delivery modes, which hold a reference to the region.

abstract class DeliveryMode {
    protected DeliveryRegion region;
    protected int miles;

    public DeliveryMode(DeliveryRegion region, int miles) {
        this.region = region;
        this.miles = miles;
    }

    public abstract double getTotalDeliveryCost();
    public abstract String getEstimatedTime();
}

class StandardDelivery extends DeliveryMode {
    public StandardDelivery(DeliveryRegion region, int miles) { super(region, miles); }
    
    @Override
    public double getTotalDeliveryCost() {
        return region.calculateBaseCost(miles); 
    }
    @Override
    public String getEstimatedTime() {
        return region.getStandardTime();
    }
}

class ExpressDelivery extends DeliveryMode {
    public ExpressDelivery(DeliveryRegion region, int miles) { super(region, miles); }
    
    @Override
    public double getTotalDeliveryCost() {
        return region.calculateBaseCost(miles) + 10.0; // Adds $10[cite: 1]
    }
    @Override
    public String getEstimatedTime() {
        if (region.getCategory() == RegionCategory.INTERNATIONAL) return "1 week"; //[cite: 1]
        return "2 days"; //[cite: 1]
    }
}

class PriorityDelivery extends DeliveryMode {
    public PriorityDelivery(DeliveryRegion region, int miles) { super(region, miles); }
    
    @Override
    public double getTotalDeliveryCost() {
        return region.calculateBaseCost(miles) + 25.0; // Adds $25[cite: 1]
    }
    @Override
    public String getEstimatedTime() {
        if (region.getCategory() == RegionCategory.INTERNATIONAL) return "5 days"; //[cite: 1]
        return "1 day"; //[cite: 1]
    }
}

// ==========================================
// 3. DECORATOR PATTERN: GIFT ITEMS 
// ==========================================

interface GiftItem {
    String getDescription();
    double getCost();
    String getDeliveryTime();
}

// Concrete Component
class BaseGiftItem implements GiftItem {
    private String name;
    private double basePrice;

    public BaseGiftItem(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    @Override
    public String getDescription() { return name; }
    @Override
    public double getCost() { return basePrice; }
    @Override
    public String getDeliveryTime() { return "N/A (No delivery requested)"; }
}

// Base Decorator
abstract class GiftDecorator implements GiftItem {
    protected GiftItem wrappedItem;

    public GiftDecorator(GiftItem item) {
        this.wrappedItem = item;
    }

    @Override
    public String getDescription() { return wrappedItem.getDescription(); }
    @Override
    public double getCost() { return wrappedItem.getCost(); }
    @Override
    public String getDeliveryTime() { return wrappedItem.getDeliveryTime(); }
}

// Concrete Decorator 1: Gift Wrapping
class WrappingDecorator extends GiftDecorator {
    public WrappingDecorator(GiftItem item) { super(item); }

    @Override
    public String getDescription() {
        return super.getDescription() + " (Gift Wrapped)";
    }

    @Override
    public double getCost() {
        return super.getCost() + 2.0; // Wrapping adds $2[cite: 1]
    }
}

// Concrete Decorator 2: Delivery 
class DeliveryDecorator extends GiftDecorator {
    private DeliveryMode deliveryMode;

    public DeliveryDecorator(GiftItem item, DeliveryMode deliveryMode) {
        super(item);
        this.deliveryMode = deliveryMode;
    }

    @Override
    public double getCost() {
        return super.getCost() + deliveryMode.getTotalDeliveryCost();
    }

    @Override
    public String getDeliveryTime() {
        return deliveryMode.getEstimatedTime();
    }
}

// ==========================================
// 4. CLIENT CODE (Testing the Cases)
// ==========================================

public class GiftShopSystem {
    public static void printReceipt(int caseNum, GiftItem item) {
        System.out.println("Case " + caseNum);
        System.out.println("Item: " + item.getDescription());
        System.out.println("Total Cost: $" + item.getCost());
        System.out.println("Estimated Delivery Time: " + item.getDeliveryTime());
        System.out.println("-------------------------------------------------");
    }

    public static void main(String[] args) {
        // CASE 1: Decorative vase ($40), 10 miles local, gift wrapping[cite: 1].
        GiftItem case1Item = new BaseGiftItem("Decorative vase", 40.0);
        case1Item = new WrappingDecorator(case1Item);
        
        DeliveryRegion localRegion = new LocalDelivery();
        DeliveryMode standardLocal = new StandardDelivery(localRegion, 10);
        case1Item = new DeliveryDecorator(case1Item, standardLocal);
        
        printReceipt(1, case1Item);

        // CASE 2: Wooden souvenir ($60), 50 miles national, gift wrapping, Express Delivery[cite: 1].
        GiftItem case2Item = new BaseGiftItem("Wooden souvenir", 60.0);
        case2Item = new WrappingDecorator(case2Item);
        
        DeliveryRegion nationalRegion = new NationalDelivery();
        DeliveryMode expressNational = new ExpressDelivery(nationalRegion, 50);
        case2Item = new DeliveryDecorator(case2Item, expressNational);
        
        printReceipt(2, case2Item);

        // CASE 3: Crystal showpiece ($150), international, Priority Delivery[cite: 1].
        GiftItem case3Item = new BaseGiftItem("Crystal showpiece", 150.0);
        
        DeliveryRegion intlRegion = new InternationalDelivery();
        DeliveryMode priorityIntl = new PriorityDelivery(intlRegion, 0); // distance is irrelevant here
        case3Item = new DeliveryDecorator(case3Item, priorityIntl);
        
        printReceipt(3, case3Item);
    }
}

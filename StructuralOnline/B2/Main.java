import java.util.ArrayList;
import java.util.List;

// ==========================================
// 1. COMPOSITE PATTERN: COMPONENT INTERFACE
// ==========================================
// This interface allows the Order class to treat individual items 
// and complex packages uniformly.

interface OrderItem {
    double getPrice();
    void print(String indent);
}

// ==========================================
// 2. LEAF NODES (Individual Items)
// ==========================================

class Food implements OrderItem {
    private String name;
    private double price;

    public Food(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void print(String indent) {
        System.out.printf("%sFood: %s (£%.2f)%n", indent, name, price);
    }
}

class Grocery implements OrderItem {
    private String name;
    private double price;

    public Grocery(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void print(String indent) {
        System.out.printf("%sGrocery: %s (£%.2f)%n", indent, name, price);
    }
}

// ==========================================
// 3. COMPOSITE NODES (Containers)
// ==========================================

class SetMenu implements OrderItem {
    private String name;
    // A set menu can only contain individual food items[cite: 3]
    private List<Food> foods = new ArrayList<>();

    public SetMenu(String name) {
        this.name = name;
    }

    public void addFood(Food item) {
        foods.add(item);
    }

    @Override
    public double getPrice() {
        double total = 0;
        for (Food food : foods) {
            total += food.getPrice();
        }
        // The price is 10 percent less than the total of individual items[cite: 3]
        return total * 0.90; 
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "Set Menu: " + name);
        for (Food food : foods) {
            food.print(indent + " "); // Increases indentation for nested items
        }
    }
}

class GroceryPackage implements OrderItem {
    private String name;
    // Packages can contain individual items or other packages[cite: 3]
    private List<OrderItem> items = new ArrayList<>();

    public GroceryPackage(String name) {
        this.name = name;
    }

    public void add(OrderItem item) {
        items.add(item);
    }

    @Override
    public double getPrice() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getPrice();
        }
        return total;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "Package: " + name);
        for (OrderItem item : items) {
            item.print(indent + " "); // Increases indentation for nested items
        }
    }
}

// ==========================================
// 4. PROVIDED CLASSES (Client & Execution)
// ==========================================

class Order {
    private List<OrderItem> items = new ArrayList<>();

    public void add(OrderItem item) {
        items.add(item);
    }

    public double getTotalPrice() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getPrice();
        }
        return total;
    }

    public void printReceipt() {
        System.out.println("========== RECEIPT ==========");
        for (OrderItem item : items) {
            item.print("");
        }
        System.out.println("-----------------------------");
        System.out.printf("Total Bill: £%.2f%n", getTotalPrice());
    }
}

public class Main {
    public static void main(String[] args) {
        // Foods
        Food burger = new Food("Burger", 8);
        Food pizza = new Food("Pizza", 10);
        Food fries = new Food("French Fries", 3);
        
        // Set Menu
        SetMenu lunch = new SetMenu("Lunch Combo"); 
        lunch.addFood(burger);
        lunch.addFood(fries);
        
        // Grocery Items
        Grocery rice = new Grocery("Rice", 20);
        Grocery oil = new Grocery("Cooking Oil", 12);
        Grocery eggs = new Grocery("Eggs", 6);
        Grocery sugar = new Grocery("Sugar", 5);
        
        // Small Package
        GroceryPackage breakfastPack = new GroceryPackage("Breakfast Pack");
        breakfastPack.add(eggs);
        breakfastPack.add(sugar);
        
        // Large Package (contains another package)
        GroceryPackage monthlyPack = new GroceryPackage("Monthly Essentials");
        monthlyPack.add(rice);
        monthlyPack.add(oil);
        monthlyPack.add(breakfastPack);
        
        // Customer Order
        Order order = new Order();
        order.add(pizza);
        order.add(lunch);
        order.add(rice);
        order.add(monthlyPack);
        
        order.printReceipt();
    }
}

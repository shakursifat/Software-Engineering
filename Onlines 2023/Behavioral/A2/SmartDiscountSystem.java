import java.util.ArrayList;
import java.util.List;

// --- ENUMS ---
enum CustomerCategory {
    REGULAR, PREMIUM
}

enum PaymentMethod {
    CARD, MFS, CASH
}

// --- PURCHASE MODEL ---
class Purchase {
    private double amount;
    private CustomerCategory customerCategory;
    private PaymentMethod paymentMethod;

    public Purchase(double amount, CustomerCategory customerCategory, PaymentMethod paymentMethod) {
        this.amount = amount;
        this.customerCategory = customerCategory;
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() { return amount; }
    public CustomerCategory getCustomerCategory() { return customerCategory; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
}

// --- STRATEGY INTERFACE ---
interface DiscountPolicy {
    double calculateDiscountPercentage(Purchase purchase);
}

// --- CONCRETE STRATEGIES ---

/**
 * 1. Purchase Amount Discount
 * For every complete 1,000 of purchase, an additional 5% discount is applied, 
 * up to a maximum of 25%.
 */
class PurchaseAmountDiscount implements DiscountPolicy {
    @Override
    public double calculateDiscountPercentage(Purchase purchase) {
        // Calculate the number of complete 1000s
        int thousands = (int) (purchase.getAmount() / 1000);
        double discount = thousands * 5.0;
        
        // Cap the maximum discount at 25%
        return Math.min(discount, 25.0);
    }
}

/**
 * 2. Customer Category Discount
 * REGULAR -> 5%, PREMIUM -> 15%[cite: 1].
 */
class CustomerCategoryDiscount implements DiscountPolicy {
    @Override
    public double calculateDiscountPercentage(Purchase purchase) {
        switch (purchase.getCustomerCategory()) {
            case PREMIUM: return 15.0; // Premium customer is eligible for a 15% discount regardless of the purchase amount[cite: 1]
            case REGULAR: return 5.0;
            default: return 0.0;
        }
    }
}

/**
 * 3. Payment Method Discount
 * CARD -> 2%, MFS -> 5%, CASH -> 8%[cite: 1].
 */
class PaymentMethodDiscount implements DiscountPolicy {
    @Override
    public double calculateDiscountPercentage(Purchase purchase) {
        switch (purchase.getPaymentMethod()) {
            case CASH: return 8.0;
            case MFS: return 5.0; // MFS includes mobile financial services such as bKash and Rocket[cite: 1]
            case CARD: return 2.0;
            default: return 0.0;
        }
    }
}

class SmartDiscountCalculator {
    private List<DiscountPolicy> policies;

    // Dependency Injection: The calculator receives its policies from the outside
    public SmartDiscountCalculator(List<DiscountPolicy> policies) {
        this.policies = policies;
    }

    public double getBestDiscountPercentage(Purchase purchase) {
        double maxDiscount = 0.0;
        
        for (DiscountPolicy policy : policies) {
            double currentDiscount = policy.calculateDiscountPercentage(purchase);
            if (currentDiscount > maxDiscount) {
                maxDiscount = currentDiscount;
            }
        }
        
        return maxDiscount;
    }

    public double calculateFinalPayableAmount(Purchase purchase) {
        double bestDiscount = getBestDiscountPercentage(purchase);
        return purchase.getAmount() * (1 - (bestDiscount / 100.0));
    }
}

// --- MAIN CLASS (TESTING) ---
public class SmartDiscountSystem {
    public static void main(String[] args) {
        // 1. Assemble the strategies in the client (Main class)
        List<DiscountPolicy> activePolicies = new ArrayList<>();
        activePolicies.add(new PurchaseAmountDiscount());
        activePolicies.add(new CustomerCategoryDiscount());
        activePolicies.add(new PaymentMethodDiscount());

        // 2. Inject the strategies into the Context
        SmartDiscountCalculator calculator = new SmartDiscountCalculator(activePolicies);
        System.out.println("--- Example 1 ---");
        // Purchase Amount: 3,500, Customer Type: PREMIUM, Payment Method: CASH[cite: 1]
        Purchase purchase1 = new Purchase(3500, CustomerCategory.PREMIUM, PaymentMethod.CASH);
        
        double appliedDiscount1 = calculator.getBestDiscountPercentage(purchase1);
        double finalAmount1 = calculator.calculateFinalPayableAmount(purchase1);
        
        System.out.println("Purchase Amount: " + purchase1.getAmount());
        System.out.println("Applied Discount: " + appliedDiscount1 + "%");
        System.out.println("Final Payable Amount: " + finalAmount1 + "\n");

        System.out.println("--- Example 2 ---");
        // Purchase Amount: 5,500, Customer Type: PREMIUM, Payment Method: CASH[cite: 1]
        Purchase purchase2 = new Purchase(5500, CustomerCategory.PREMIUM, PaymentMethod.CASH);
        
        double appliedDiscount2 = calculator.getBestDiscountPercentage(purchase2);
        double finalAmount2 = calculator.calculateFinalPayableAmount(purchase2);
        
        System.out.println("Purchase Amount: " + purchase2.getAmount());
        System.out.println("Applied Discount: " + appliedDiscount2 + "%");
        System.out.println("Final Payable Amount: " + finalAmount2);
    }
}

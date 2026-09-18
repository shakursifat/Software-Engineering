interface PaymentStrategy {
    void pay(double amount);
}


class CreditCardPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {
        System.out.println(
            "Paid " + amount + " using Credit Card"
        );
    }
}


class PayPalPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {
        System.out.println(
            "Paid " + amount + " using PayPal"
        );
    }
}


class BkashPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {
        System.out.println(
            "Paid " + amount + " using bKash"
        );
    }
}


class ShoppingCart {

    private PaymentStrategy paymentStrategy;

    public ShoppingCart(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void checkout(double amount) {
        paymentStrategy.pay(amount);
    }
}


public class Main {

    public static void main(String[] args) {

        PaymentStrategy strategy =
            new CreditCardPayment();

        ShoppingCart cart =
            new ShoppingCart(strategy);

        cart.checkout(5000);
    }
}

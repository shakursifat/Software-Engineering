package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class Order {
    public static final double DELIVERY_FEE  = 80.0;
    public static final double RUSH_FEE      = 120.0;
    public static final double GIFT_WRAP_FEE = 50.0;

    private final String orderId;
    private final String customerName;
    private final String phone;
    private final DeliveryType deliveryType;
    private final String deliveryAddress;
    private final PaymentMethod paymentMethod;
    private final LocalDateTime scheduledTime;
    private final String couponCode;
    private final boolean giftWrap;
    private final boolean cutleryRequired;
    private final int loyaltyPointsToRedeem;
    private final boolean rushOrder;
    private final List<OrderItem> items;
    private final String specialInstructions;

    /** Private — all construction goes through {Builder#build()}. */
    private Order(Builder b) {
        this.orderId               = b.orderId;
        this.customerName          = b.customerName;
        this.phone                 = b.phone;
        this.deliveryType          = b.deliveryType;
        this.deliveryAddress       = b.deliveryAddress;
        this.paymentMethod         = b.paymentMethod;
        this.scheduledTime         = b.scheduledTime;
        this.couponCode            = b.couponCode;
        this.giftWrap              = b.giftWrap;
        this.cutleryRequired       = b.cutleryRequired;
        this.loyaltyPointsToRedeem = b.loyaltyPointsToRedeem;
        this.rushOrder             = b.rushOrder;
        this.items                 = b.items;
        this.specialInstructions   = b.specialInstructions;
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static class Builder {

        // --- required ---
        private final String orderId;
        private final String customerName;
        private final String phone;
        private List<OrderItem> items;

        // --- optional with defaults ---
        private DeliveryType    deliveryType          = DeliveryType.PICKUP;
        private String          deliveryAddress       = "";
        private PaymentMethod   paymentMethod         = PaymentMethod.CASH;
        private LocalDateTime   scheduledTime         = null;
        private String          couponCode            = "";
        private boolean         giftWrap              = false;
        private boolean         cutleryRequired       = true;
        private int             loyaltyPointsToRedeem = 0;
        private boolean         rushOrder             = false;
        private String          specialInstructions   = "";

        
        public Builder(String orderId, String customerName, String phone, List<OrderItem> items) {
            this.orderId      = requireNonBlank(orderId,      "Order id");
            this.customerName = requireNonBlank(customerName, "Customer name");
            this.phone        = requireNonBlank(phone,        "Phone");
            this.items        = items;
        }

        public Builder deliveryType(DeliveryType deliveryType) {
            this.deliveryType = deliveryType != null ? deliveryType : DeliveryType.PICKUP;
            return this;
        }

        public Builder deliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress != null ? deliveryAddress.trim() : "";
            return this;
        }

        public Builder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.CASH;
            return this;
        }

        public Builder scheduledTime(LocalDateTime scheduledTime) {
            this.scheduledTime = scheduledTime;
            return this;
        }

        public Builder couponCode(String couponCode) {
            this.couponCode = couponCode != null ? couponCode.trim().toUpperCase() : "";
            return this;
        }

        public Builder giftWrap(boolean giftWrap) {
            this.giftWrap = giftWrap;
            return this;
        }

        public Builder cutleryRequired(boolean cutleryRequired) {
            this.cutleryRequired = cutleryRequired;
            return this;
        }

        public Builder loyaltyPoints(int loyaltyPointsToRedeem) {
            this.loyaltyPointsToRedeem = Math.max(0, loyaltyPointsToRedeem);
            return this;
        }

        public Builder rushOrder(boolean rushOrder) {
            this.rushOrder = rushOrder;
            return this;
        }

        public Builder specialInstructions(String specialInstructions) {
            this.specialInstructions = specialInstructions != null ? specialInstructions.trim() : "";
            return this;
        }

        
        public Order build() {
            // Validate items
            Objects.requireNonNull(items, "Items cannot be null");
            if (items.isEmpty()) {
                throw new IllegalArgumentException("Order must contain at least one item");
            }

            // Validate delivery address
            if (deliveryType == DeliveryType.DELIVERY) {
                requireNonBlank(deliveryAddress, "Delivery address");
            }

            // Freeze the item list
            this.items = Collections.unmodifiableList(new ArrayList<>(items));

            // Normalise coupon code
            this.couponCode = couponCode != null ? couponCode.trim().toUpperCase() : "";

            return new Order(this);
        }
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhone() {
        return phone;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public boolean isGiftWrap() {
        return giftWrap;
    }

    public boolean isCutleryRequired() {
        return cutleryRequired;
    }

    public int getLoyaltyPointsToRedeem() {
        return loyaltyPointsToRedeem;
    }

    public boolean isRushOrder() {
        return rushOrder;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    // -------------------------------------------------------------------------
    // Pricing
    // -------------------------------------------------------------------------

    public double getSubtotal() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    public double getDiscount() {
        double couponDiscount = 0.0;
        if ("WELCOME10".equals(couponCode)) {
            couponDiscount = getSubtotal() * 0.10;
        } else if ("FAMILY15".equals(couponCode) && getSubtotal() >= 1000.0) {
            couponDiscount = getSubtotal() * 0.15;
        }

        double loyaltyDiscount = Math.min(loyaltyPointsToRedeem, 100);
        return couponDiscount + loyaltyDiscount;
    }

    public double getServiceCharges() {
        double charges = 0.0;
        if (deliveryType == DeliveryType.DELIVERY) {
            charges += DELIVERY_FEE;
        }
        if (rushOrder) {
            charges += RUSH_FEE;
        }
        if (giftWrap) {
            charges += GIFT_WRAP_FEE;
        }
        return charges;
    }

    public double getTotal() {
        return Math.max(0.0, getSubtotal() + getServiceCharges() - getDiscount());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String requireNonBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return trimmed;
    }
}

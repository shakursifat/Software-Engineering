// 1. The common Product interface
interface Transport {
    void deliver();
}

// 2. Concrete Product A
class Truck implements Transport {
    @Override
    public void deliver() {
        System.out.println("Delivering cargo by land in a box on a Truck.");
    }
}

// 3. Concrete Product B
class Ship implements Transport {
    @Override
    public void deliver() {
        System.out.println("Delivering cargo by sea in a shipping container on a Ship.");
    }
}

// Future expansion example (You can add this later without breaking the client)
class Airplane implements Transport {
    @Override
    public void deliver() {
        System.out.println("Delivering cargo by air quickly in an Airplane.");
    }
}

// 4. The Factory Class
class TransportFactory {
    
    // The factory method determines which object to instantiate based on input
    public static Transport createTransport(String mode) {
        if (mode == null || mode.isEmpty()) {
            return null;
        }
        
        switch (mode.toLowerCase()) {
            case "road":
                return new Truck();
            case "sea":
                return new Ship();
            case "air": // Added easily for future expansion
                return new Airplane();
            default:
                throw new IllegalArgumentException("Unknown transport mode: " + mode);
        }
    }
}

// 5. The Client Code
public class LogisticsApplication {
    public static void main(String[] args) {
        
        // The client only knows about the Factory and the Transport interface.
        // It does not know how Trucks or Ships are created.
        
        System.out.println("--- Processing Road Delivery ---");
        Transport roadTransport = TransportFactory.createTransport("Road");
        roadTransport.deliver();
        
        System.out.println("\n--- Processing Sea Delivery ---");
        Transport seaTransport = TransportFactory.createTransport("Sea");
        seaTransport.deliver();

        System.out.println("\n--- Processing Air Delivery ---");
        Transport airTransport = TransportFactory.createTransport("Air");
        airTransport.deliver();
    }
}
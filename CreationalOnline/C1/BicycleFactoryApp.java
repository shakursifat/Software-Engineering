// ==========================================
// 1. The Product (The complex object)
// ==========================================
class Bicycle {
    private String frame;
    private String gearSystem;
    private String tireType;

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public void setGearSystem(String gearSystem) {
        this.gearSystem = gearSystem;
    }

    public void setTireType(String tireType) {
        this.tireType = tireType;
    }

    @Override
    public String toString() {
        return "Bicycle [" +
                "Frame: '" + frame + '\'' +
                ", Gears: '" + gearSystem + '\'' +
                ", Tires: '" + tireType + '\'' +
                ']';
    }
}

// ==========================================
// 2. The Builder Interface
// ==========================================
// Specifies the assembly steps required to build a Bicycle
interface BicycleBuilder {
    void buildFrame();
    void buildGearSystem();
    void buildTireType();
    
    // Method to return the finished product
    Bicycle getBicycle();
}

// ==========================================
// 3. Concrete Builders (The Representations)
// ==========================================

// Concrete Builder 1: The Commuter
class CommuterBuilder implements BicycleBuilder {
    private Bicycle bicycle;

    public CommuterBuilder() {
        this.bicycle = new Bicycle();
    }

    @Override
    public void buildFrame() {
        bicycle.setFrame("Aluminum Frame");
    }

    @Override
    public void buildGearSystem() {
        bicycle.setGearSystem("Single Speed Gear");
    }

    @Override
    public void buildTireType() {
        bicycle.setTireType("Road Tires");
    }

    @Override
    public Bicycle getBicycle() {
        return this.bicycle;
    }
}

// Concrete Builder 2: The Mountain Beast
class MountainBeastBuilder implements BicycleBuilder {
    private Bicycle bicycle;

    public MountainBeastBuilder() {
        this.bicycle = new Bicycle();
    }

    @Override
    public void buildFrame() {
        bicycle.setFrame("Carbon Fiber Frame");
    }

    @Override
    public void buildGearSystem() {
        bicycle.setGearSystem("12-Speed Gear");
    }

    @Override
    public void buildTireType() {
        bicycle.setTireType("Off-road Grip Tires");
    }

    @Override
    public Bicycle getBicycle() {
        return this.bicycle;
    }
}

// ==========================================
// 4. The Director (Manages the Construction)
// ==========================================
// The Director ensures the parts are assembled in the correct step-by-step order
class BicycleDirector {
    private BicycleBuilder builder;

    // The client passes the specific builder they want to the director
    public void setBicycleBuilder(BicycleBuilder builder) {
        this.builder = builder;
    }

    // The standardized step-by-step construction process
    public void constructBicycle() {
        builder.buildFrame();
        builder.buildGearSystem();
        builder.buildTireType();
    }
}

// ==========================================
// 5. The Client Code
// ==========================================
public class BicycleFactoryApp {
    public static void main(String[] args) {
        
        // 1. Create the Director (the factory manager)
        BicycleDirector director = new BicycleDirector();

        System.out.println("--- Ordering 'The Commuter' ---");
        // 2. Client chooses the Commuter model
        BicycleBuilder commuterBuilder = new CommuterBuilder();
        director.setBicycleBuilder(commuterBuilder);
        
        // 3. Director assembles it step-by-step
        director.constructBicycle();
        
        // 4. Retrieve the fully assembled bicycle
        Bicycle commuterBike = commuterBuilder.getBicycle();
        System.out.println(commuterBike);


        System.out.println("\n--- Ordering 'The Mountain Beast' ---");
        // 2. Client chooses the Mountain Beast model
        BicycleBuilder mountainBuilder = new MountainBeastBuilder();
        director.setBicycleBuilder(mountainBuilder);
        
        // 3. Director uses the EXACT SAME process to assemble a different bike
        director.constructBicycle();
        
        // 4. Retrieve the fully assembled bicycle
        Bicycle mountainBike = mountainBuilder.getBicycle();
        System.out.println(mountainBike);
    }
}

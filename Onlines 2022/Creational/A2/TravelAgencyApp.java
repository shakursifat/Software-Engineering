// ==========================================
// 1. The Product (The complex object we are building)
// ==========================================
class HolidayPackage {
    private String flight;
    private String hotel;
    private String activity;

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "Holiday Package [" +
                "Flight: '" + flight + '\'' +
                ", Hotel: '" + hotel + '\'' +
                ", Activity: '" + activity + '\'' +
                ']';
    }
}

// ==========================================
// 2. The Builder Interface
// ==========================================
// Specifies the steps required to build a HolidayPackage.
interface HolidayPackageBuilder {
    void buildFlight();
    void buildHotel();
    void buildActivity();
    HolidayPackage getPackage();
}

// ==========================================
// 3. Concrete Builders (The Representations)
// ==========================================

// Concrete Builder 1: Relaxation Package
class RelaxationPackageBuilder implements HolidayPackageBuilder {
    private HolidayPackage holidayPackage;

    public RelaxationPackageBuilder() {
        this.holidayPackage = new HolidayPackage();
    }

    @Override
    public void buildFlight() {
        holidayPackage.setFlight("Business Class Flight");
    }

    @Override
    public void buildHotel() {
        holidayPackage.setHotel("5-Star Resort");
    }

    @Override
    public void buildActivity() {
        holidayPackage.setActivity("Spa Treatment");
    }

    @Override
    public HolidayPackage getPackage() {
        return this.holidayPackage;
    }
}

// Concrete Builder 2: Adventure Package
class AdventurePackageBuilder implements HolidayPackageBuilder {
    private HolidayPackage holidayPackage;

    public AdventurePackageBuilder() {
        this.holidayPackage = new HolidayPackage();
    }

    @Override
    public void buildFlight() {
        holidayPackage.setFlight("Economy Flight");
    }

    @Override
    public void buildHotel() {
        holidayPackage.setHotel("Mountain Cabin");
    }

    @Override
    public void buildActivity() {
        holidayPackage.setActivity("Hiking Tour");
    }

    @Override
    public HolidayPackage getPackage() {
        return this.holidayPackage;
    }
}

// ==========================================
// 4. The Director (Manages the Construction Process)
// ==========================================
// The Director isolates the client from the step-by-step construction.
class TravelAgent {
    private HolidayPackageBuilder builder;

    public void setPackageBuilder(HolidayPackageBuilder builder) {
        this.builder = builder;
    }

    // The single construction process used for all packages
    public void constructHolidayPackage() {
        builder.buildFlight();
        builder.buildHotel();
        builder.buildActivity();
    }
}

// ==========================================
// 5. The Client Code
// ==========================================
public class TravelAgencyApp {
    public static void main(String[] args) {
        // Create the Director
        TravelAgent agent = new TravelAgent();

        System.out.println("--- Building Relaxation Package ---");
        // Create a specific builder and pass it to the director
        HolidayPackageBuilder relaxationBuilder = new RelaxationPackageBuilder();
        agent.setPackageBuilder(relaxationBuilder);
        
        // Director handles the construction process
        agent.constructHolidayPackage();
        
        // Retrieve the final constructed object
        HolidayPackage relaxationPackage = relaxationBuilder.getPackage();
        System.out.println(relaxationPackage);


        System.out.println("\n--- Building Adventure Package ---");
        // Swap out the builder to get a completely different representation
        HolidayPackageBuilder adventureBuilder = new AdventurePackageBuilder();
        agent.setPackageBuilder(adventureBuilder);
        
        // The same construction process is used
        agent.constructHolidayPackage();
        
        // Retrieve the new object
        HolidayPackage adventurePackage = adventureBuilder.getPackage();
        System.out.println(adventurePackage);
    }
}

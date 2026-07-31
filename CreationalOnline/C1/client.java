class Bicycle {
    private String frame;
    private String gear_system;
    private String tire_type;

    public void setFrame(String frame) {
        this.frame = frame;
    }
    public void setGear_system(String gear_system) {
        this.gear_system = gear_system;
    }
    public void setTire_type(String tire_type) {
        this.tire_type = tire_type;
    }

    @Override
    public String toString() {
        return "Bicycle: " + frame + " " + gear_system + " " + tire_type; 
    }
}

interface BicycleBuilder {
    void buildframe();
    void buildgearsystem();
    void buildtiretype();

    Bicycle getBicycle();
}

class Commuterbuilder implements BicycleBuilder {
    private Bicycle commuterBicycle;

    public Commuterbuilder() {
        this.commuterBicycle = new Bicycle();
    }

    @Override
    public void buildframe() {
        commuterBicycle.setFrame("Aluminium Frame");
    }

    @Override
    public void buildgearsystem() {
        commuterBicycle.setGear_system("Single Speed Gear");
    }

    @Override
    public void buildtiretype() {
        commuterBicycle.setTire_type("Road Tires");
    }

    @Override
    public Bicycle getBicycle() {
        return this.commuterBicycle;
    }
}

class MountainBeastbuilder implements BicycleBuilder {
    private Bicycle MountainBeastBicycle;

    public MountainBeastbuilder() {
        this.MountainBeastBicycle = new Bicycle();
    }

    @Override
    public void buildframe() {
        MountainBeastBicycle.setFrame("Carbon Fiber Frame");
    }

    @Override
    public void buildgearsystem() {
        MountainBeastBicycle.setGear_system("12 speed gears");
    }

    @Override
    public void buildtiretype() {
        MountainBeastBicycle.setTire_type("Off road grip tires");
    }

    @Override
    public Bicycle getBicycle() {
        return this.MountainBeastBicycle;
    }
}

class Assembler {
    private BicycleBuilder builder;

    public void setBuilder(BicycleBuilder builder) {
        this.builder = builder;
    }

    public void construct() {
        builder.buildframe();
        builder.buildgearsystem();
        builder.buildtiretype();
    }
}


public class client {
    public static void main(String[] args) {
        Assembler assembler = new Assembler();

        BicycleBuilder commuter = new Commuterbuilder();

        assembler.setBuilder(commuter);

        assembler.construct();

        Bicycle bicycle = commuter.getBicycle();

        System.out.println(bicycle);
    }
}

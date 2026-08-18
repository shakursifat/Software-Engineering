// ==========================================
// 1. Implementor (The Interface)
// ==========================================
// This interface declares the primitive operations that all devices must support.
interface Device {
    boolean isEnabled();
    void enable();
    void disable();
    int getVolume();
    void setVolume(int percent);
    int getChannel();
    void setChannel(int channel);
}

// ==========================================
// 2. Concrete Implementors
// ==========================================
// These classes implement the Device interface for specific electronics.

class Tv implements Device {
    private boolean on = false;
    private int volume = 30;
    private int channel = 1;

    @Override
    public boolean isEnabled() { return on; }

    @Override
    public void enable() { 
        on = true; 
        System.out.println("TV is turned ON.");
    }

    @Override
    public void disable() { 
        on = false; 
        System.out.println("TV is turned OFF.");
    }

    @Override
    public int getVolume() { return volume; }

    @Override
    public void setVolume(int volume) {
        // Keep volume within a valid range
        if (volume > 100) this.volume = 100;
        else if (volume < 0) this.volume = 0;
        else this.volume = volume;
        System.out.println("TV volume set to " + this.volume);
    }

    @Override
    public int getChannel() { return channel; }

    @Override
    public void setChannel(int channel) {
        this.channel = channel;
        System.out.println("TV channel set to " + this.channel);
    }
}

class Radio implements Device {
    private boolean on = false;
    private int volume = 50;
    private int channel = 88; // Default FM frequency

    @Override
    public boolean isEnabled() { return on; }

    @Override
    public void enable() { 
        on = true; 
        System.out.println("Radio is turned ON.");
    }

    @Override
    public void disable() { 
        on = false; 
        System.out.println("Radio is turned OFF.");
    }

    @Override
    public int getVolume() { return volume; }

    @Override
    public void setVolume(int volume) {
        if (volume > 100) this.volume = 100;
        else if (volume < 0) this.volume = 0;
        else this.volume = volume;
        System.out.println("Radio volume set to " + this.volume);
    }

    @Override
    public int getChannel() { return channel; }

    @Override
    public void setChannel(int channel) {
        this.channel = channel;
        System.out.println("Radio tuned to frequency " + this.channel);
    }
}

// ==========================================
// 3. Abstraction
// ==========================================
// The basic remote control. It holds a reference to a device and 
// delegates the actual execution to it.

class RemoteControl {
    protected Device device; // The "Bridge"

    public RemoteControl(Device device) {
        this.device = device;
    }

    public void togglePower() {
        System.out.println("\nRemote: Power toggle button pressed.");
        if (device.isEnabled()) {
            device.disable();
        } else {
            device.enable();
        }
    }

    public void volumeDown() {
        System.out.println("Remote: Volume down.");
        device.setVolume(device.getVolume() - 10);
    }

    public void volumeUp() {
        System.out.println("Remote: Volume up.");
        device.setVolume(device.getVolume() + 10);
    }

    public void channelDown() {
        System.out.println("Remote: Channel down.");
        device.setChannel(device.getChannel() - 1);
    }

    public void channelUp() {
        System.out.println("Remote: Channel up.");
        device.setChannel(device.getChannel() + 1);
    }
}

// ==========================================
// 4. Refined Abstraction
// ==========================================
// An advanced remote that adds new functionality (mute button) 
// without needing to change the base Device interface or the implementations.

class AdvancedRemote extends RemoteControl {
    
    public AdvancedRemote(Device device) {
        super(device);
    }

    public void mute() {
        System.out.println("Remote: Mute button pressed.");
        device.setVolume(0);
    }
}

// ==========================================
// 5. Client Code (Testing the Bridge)
// ==========================================
public class BridgePatternDemo {
    public static void main(String[] args) {
        
        System.out.println("--- Testing Basic Remote with TV ---");
        Device tv = new Tv();
        RemoteControl basicRemote = new RemoteControl(tv);
        
        basicRemote.togglePower();
        basicRemote.volumeUp();
        basicRemote.channelUp();


        System.out.println("\n--- Testing Advanced Remote with Radio ---");
        Device radio = new Radio();
        AdvancedRemote advancedRemote = new AdvancedRemote(radio);
        
        advancedRemote.togglePower();
        advancedRemote.mute(); // Feature specific to the Refined Abstraction
    }
}

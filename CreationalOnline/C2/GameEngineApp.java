// ==========================================
// 1. The Singleton Class
// ==========================================
class GameConfig {
    
    // 1. Private static variable to hold the single shared instance
    private static GameConfig instance;

    // Configuration settings
    private String resolution;
    private int audioVolume;
    private String difficultyLevel;

    // 2. Private constructor prevents instantiation from outside the class
    private GameConfig() {
        System.out.println("--- [SYSTEM] Booting up... Loading configuration from disk... ---");
        // Simulating expensive disk read operation
        this.resolution = "1920x1080";
        this.audioVolume = 80;
        this.difficultyLevel = "Normal";
    }

    // 3. Public static method for global access (Lazy Initialization)
    public static GameConfig getInstance() {
        // If the instance doesn't exist yet, create it.
        if (instance == null) {
            instance = new GameConfig();
        }
        // Otherwise, return the already existing instance.
        return instance;
    }

    // --- Getters and Setters for Game Settings ---
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public int getAudioVolume() { return audioVolume; }
    public void setAudioVolume(int audioVolume) { this.audioVolume = audioVolume; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
}

// ==========================================
// 2. The Client Code (Game Modules)
// ==========================================
public class GameEngineApp {
    public static void main(String[] args) {
        
        System.out.println("--- Graphics Engine Initializing ---");
        // Graphics engine requests the config (Triggers the disk load)
        GameConfig graphicsConfig = GameConfig.getInstance();
        System.out.println("Applying Resolution: " + graphicsConfig.getResolution());

        System.out.println("\n--- Audio Engine Initializing ---");
        // Audio engine requests the config (Does NOT trigger disk load, uses existing)
        GameConfig audioConfig = GameConfig.getInstance();
        System.out.println("Applying Volume: " + audioConfig.getAudioVolume());

        System.out.println("\n--- AI Engine Initializing ---");
        // AI engine requests the config
        GameConfig aiConfig = GameConfig.getInstance();
        System.out.println("Setting AI Difficulty: " + aiConfig.getDifficultyLevel());

        System.out.println("\n--- Player Changes Settings in Menu ---");
        // Player changes the difficulty using the graphics/menu module
        graphicsConfig.setDifficultyLevel("Hardcore");
        
        // Let's verify that the AI engine sees this change immediately
        System.out.println("AI Engine reads new difficulty: " + aiConfig.getDifficultyLevel());

        System.out.println("\n--- Verification ---");
        // Prove that all three modules share the exact same object in memory
        if (graphicsConfig == audioConfig && audioConfig == aiConfig) {
            System.out.println("SUCCESS: All modules are using the exact same GameConfig instance!");
        } else {
            System.out.println("ERROR: Multiple instances detected!");
        }
    }
}

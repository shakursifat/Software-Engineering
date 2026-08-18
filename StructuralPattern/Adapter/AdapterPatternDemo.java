// ---------------------------------------------------------
// 1. Target Interface
// This is the interface your modern application expects.
// ---------------------------------------------------------
interface ModernDatabaseInterface {
    String requestJSONData();
}

// ---------------------------------------------------------
// 2. Adaptee (The incompatible service)
// This is the legacy meteorological service you want to use.
// It returns XML instead of JSON and uses different methods.
// ---------------------------------------------------------
class LegacyWeatherAPI {
    public String fetchXMLWeatherReport() {
        return "<weather><temp>32.5</temp><humidity>80</humidity></weather>";
    }
}

// ---------------------------------------------------------
// 3. Adapter
// This class implements your expected Target interface but
// holds a reference to the Adaptee to do the actual work.
// ---------------------------------------------------------
class WeatherAPIAdapter implements ModernDatabaseInterface {
    private LegacyWeatherAPI legacyAPI;

    // The adapter takes the Adaptee in its constructor (Object Adapter approach)
    public WeatherAPIAdapter(LegacyWeatherAPI legacyAPI) {
        this.legacyAPI = legacyAPI;
    }

    // We implement the method the client expects
    @Override
    public String requestJSONData() {
        // 1. Get the incompatible data from the Adaptee
        String xmlData = legacyAPI.fetchXMLWeatherReport();
        
        // 2. Translate/Adapt the data (simulated XML to JSON conversion)
        System.out.println("[Adapter] Translating XML to JSON...");
        String jsonData = "{ \"temp\": 32.5, \"humidity\": 80 }";
        
        // 3. Return the data in the format the Client requires
        return jsonData;
    }
}

// ---------------------------------------------------------
// 4. Client Code
// The client only knows about ModernDatabaseInterface.
// ---------------------------------------------------------
public class AdapterPatternDemo {
    
    // The client method expects the target interface.
    public static void renderDashboard(ModernDatabaseInterface db) {
        // The client calls the modern method, oblivious to the legacy XML behind it
        System.out.println("Dashboard UI received: " + db.requestJSONData());
    }

    public static void main(String[] args) {
        // We have our legacy, incompatible API instance
        LegacyWeatherAPI legacyService = new LegacyWeatherAPI();

        // If we tried to pass legacyService directly to renderDashboard(), 
        // the Java compiler would throw an error. So, we wrap it in an Adapter.
        ModernDatabaseInterface adapter = new WeatherAPIAdapter(legacyService);

        // The client happily accepts the adapter
        renderDashboard(adapter);
    }
}
// ==========================================
// 1. Abstract Products (Component Interfaces)
// ==========================================
interface Button {
    void render();
}

interface TextField {
    void render();
}

interface Dialog {
    void render();
}

// ==========================================
// 2. Concrete Products (Theme-Specific Components)
// ==========================================

// --- Light Theme Family ---
class LightButton implements Button {
    @Override
    public void render() {
        System.out.println("Rendering a Light Button.");
    }
}

class LightTextField implements TextField {
    @Override
    public void render() {
        System.out.println("Rendering a Light TextField.");
    }
}

class LightDialog implements Dialog {
    @Override
    public void render() {
        System.out.println("Rendering a Light Dialog Box.");
    }
}

// --- Dark Theme Family ---
class DarkButton implements Button {
    @Override
    public void render() {
        System.out.println("Rendering a Dark Button.");
    }
}

class DarkTextField implements TextField {
    @Override
    public void render() {
        System.out.println("Rendering a Dark TextField.");
    }
}

class DarkDialog implements Dialog {
    @Override
    public void render() {
        System.out.println("Rendering a Dark Dialog Box.");
    }
}

// ==========================================
// 3. The Abstract Factory Interface
// ==========================================
// This ensures that any theme factory will provide all necessary components.
interface ThemeFactory {
    Button createButton();
    TextField createTextField();
    Dialog createDialog();
}

// ==========================================
// 4. Concrete Factories (Theme Implementations)
// ==========================================

// Creates exclusively Light Theme components
class LightThemeFactory implements ThemeFactory {
    @Override
    public Button createButton() {
        return new LightButton();
    }

    @Override
    public TextField createTextField() {
        return new LightTextField();
    }

    @Override
    public Dialog createDialog() {
        return new LightDialog();
    }
}

// Creates exclusively Dark Theme components
class DarkThemeFactory implements ThemeFactory {
    @Override
    public Button createButton() {
        return new DarkButton();
    }

    @Override
    public TextField createTextField() {
        return new DarkTextField();
    }

    @Override
    public Dialog createDialog() {
        return new DarkDialog();
    }
}

// ==========================================
// 5. Client Code (The Application)
// ==========================================
// The client only interacts with the Abstract Factory and Abstract Products.
// It does not care which theme is currently active.
class Application {
    private Button button;
    private TextField textField;
    private Dialog dialog;

    // The client accepts a factory, ensuring all components come from the same family
    public Application(ThemeFactory factory) {
        this.button = factory.createButton();
        this.textField = factory.createTextField();
        this.dialog = factory.createDialog();
    }

    public void renderUI() {
        button.render();
        textField.render();
        dialog.render();
    }
}

// ==========================================
// 6. Main Runner
// ==========================================
public class UILibraryApp {
    public static void main(String[] args) {
        
        System.out.println("--- Starting App with Light Theme ---");
        // We pass the specific factory to the application
        ThemeFactory lightFactory = new LightThemeFactory();
        Application lightApp = new Application(lightFactory);
        lightApp.renderUI();

        System.out.println("\n--- User switches to Dark Theme ---");
        // To change the whole theme, we just swap the factory
        ThemeFactory darkFactory = new DarkThemeFactory();
        Application darkApp = new Application(darkFactory);
        darkApp.renderUI();
    }
}

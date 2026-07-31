// ==========================================
// 1. The Product Interface
// ==========================================
interface Report {
    void open();
    void generate();
}

// ==========================================
// 2. Concrete Products
// ==========================================
class PDFReport implements Report {
    @Override
    public void open() {
        System.out.println("Opening PDF Report...");
    }

    @Override
    public void generate() {
        System.out.println("Generating PDF content (tables, charts, text)...");
    }
}

class WordReport implements Report {
    @Override
    public void open() {
        System.out.println("Opening Word Document...");
    }

    @Override
    public void generate() {
        System.out.println("Generating Word content (paragraphs, formatting)...");
    }
}

class HTMLReport implements Report {
    @Override
    public void open() {
        System.out.println("Opening HTML File...");
    }

    @Override
    public void generate() {
        System.out.println("Generating HTML content (tags, styling)...");
    }
}

// ==========================================
// 3. The Creator (Abstract Processor)
// ==========================================
abstract class ReportProcessor {
    
    // The Template/Core Logic: The general processing steps are defined ONCE.
    public void processReport() {
        // 1. Create the report object (Delegated to the factory method)
        Report report = createReport();
        
        // 2. Open the report
        report.open();
        
        // 3. Generate the report
        report.generate();
        
        // 4. Display a completion message
        System.out.println("SUCCESS: Report processing completed.\n");
    }

    // The Factory Method: Subclasses implement this to choose the concrete object.
    protected abstract Report createReport();
}

// ==========================================
// 4. Concrete Creators (Specialized Processors)
// ==========================================
class PDFProcessor extends ReportProcessor {
    @Override
    protected Report createReport() {
        return new PDFReport();
    }
}

class WordProcessor extends ReportProcessor {
    @Override
    protected Report createReport() {
        return new WordReport();
    }
}

class HTMLProcessor extends ReportProcessor {
    @Override
    protected Report createReport() {
        return new HTMLReport();
    }
}

// ==========================================
// 5. Client Code
// ==========================================
public class ReportApp {
    public static void main(String[] args) {
        
        System.out.println("--- Processing PDF ---");
        ReportProcessor pdfProcessor = new PDFProcessor();
        // Client calls the common logic; the processor handles the specific creation underneath.
        pdfProcessor.processReport();

        System.out.println("--- Processing Word ---");
        ReportProcessor wordProcessor = new WordProcessor();
        wordProcessor.processReport();

        System.out.println("--- Processing HTML ---");
        ReportProcessor htmlProcessor = new HTMLProcessor();
        htmlProcessor.processReport();
    }
}

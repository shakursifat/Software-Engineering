import java.util.ArrayList;
import java.util.List;

// STEP 1: The Component Interface
interface FileSystemNode {
    // We pass an indentation string to visualize the tree hierarchy
    void showDetails(String indentation);
}

// STEP 2: The Leaf
class File implements FileSystemNode {
    private String name;
    private int sizeInBytes;

    public File(String name, int sizeInBytes) {
        this.name = name;
        this.sizeInBytes = sizeInBytes;
    }

    @Override
    public void showDetails(String indentation) {
        System.out.println(indentation + "- File: " + name + " (" + sizeInBytes + " bytes)");
    }
}

// STEP 3: The Composite
class Directory implements FileSystemNode {
    private String name;
    // The Composite holds a list of the base Component interface type.
    // This allows it to store both Files (Leaves) and other Directories (Composites).
    private List<FileSystemNode> children = new ArrayList<>();

    public Directory(String name) {
        this.name = name;
    }

    // Child management operations are placed only in the Composite class
    // to enforce type safety (the "Safety" design variant).
    public void addComponent(FileSystemNode node) {
        children.add(node);
    }

    public void removeComponent(FileSystemNode node) {
        children.remove(node);
    }

    @Override
    public void showDetails(String indentation) {
        System.out.println(indentation + "+ Directory: " + name);
        
        // The Composite delegates the operation to all of its children.
        // It doesn't care if the child is a File or another Directory.
        for (FileSystemNode child : children) {
            child.showDetails(indentation + "    ");
        }
    }
}

// STEP 4: The Client Code
public class CompositePatternDemo {
    public static void main(String[] args) {
        // 1. Create simple leaf nodes (Files)
        File track1 = new File("Apocalypse.mp3", 4500000);
        File track2 = new File("Fade_Into_You.mp3", 5200000);
        File picture1 = new File("Kuakata_Beach.jpg", 2100000);
        File picture2 = new File("Street_Portrait.png", 3400000);
        File systemLog = new File("syslog.txt", 15000);

        // 2. Create composite nodes (Directories)
        Directory musicDir = new Directory("Music_Playlist");
        Directory photoDir = new Directory("Travel_Photos");
        Directory rootDir = new Directory("Root_Drive");

        // 3. Build the tree structure by assembling the parts
        musicDir.addComponent(track1);
        musicDir.addComponent(track2);

        photoDir.addComponent(picture1);
        photoDir.addComponent(picture2);

        // Add sub-directories and individual files to the root directory
        rootDir.addComponent(musicDir);
        rootDir.addComponent(photoDir);
        rootDir.addComponent(systemLog);

        // 4. Trigger the uniform operation
        // The client only needs to call showDetails() on the root node.
        // The pattern handles traversing the entire deeply nested structure.
        System.out.println("Displaying File System Structure:\n");
        rootDir.showDetails("");
    }
}
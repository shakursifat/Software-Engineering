// 1. Implementor
interface Color {
    String fill();
}

// 2. Concrete Implementors
class RedColor implements Color {
    @Override
    public String fill() {
        return "using Red pigment";
    }
}

class BlueColor implements Color {
    @Override
    public String fill() {
        return "using Blue pigment";
    }
}

// 3. Abstraction
abstract class Shape {
    protected Color color; // The "Bridge" reference holding the implementor

    // Constructor requires a Color implementor
    public Shape(Color color) {
        this.color = color;
    }

    abstract String draw();
}

// 4. Refined Abstractions
class Circle extends Shape {
    public Circle(Color color) {
        super(color);
    }

    @Override
    String draw() {
        // Delegates the color behavior to the bridged object
        return "Drawing a Circle " + color.fill();
    }
}

class Square extends Shape {
    public Square(Color color) {
        super(color);
    }

    @Override
    String draw() {
        // Delegates the color behavior to the bridged object
        return "Drawing a Square " + color.fill();
    }
}

// Client Code
public class BridgePatternShape{
    public static void main(String[] args) {
        // We can mix and match shapes and colors independently at runtime
        
        Color red = new RedColor();
        Shape redCircle = new Circle(red);
        System.out.println(redCircle.draw()); 
        // Output: Drawing a Circle using Red pigment
        
        Color blue = new BlueColor();
        Shape blueSquare = new Square(blue);
        System.out.println(blueSquare.draw()); 
        // Output: Drawing a Square using Blue pigment
    }
}

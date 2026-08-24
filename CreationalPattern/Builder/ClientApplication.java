/**
 * BUILDER DESIGN PATTERN TEMPLATE (Classic Director-Builder version)
 * 
 * Use this pattern when:
 * 1. An object requires multiple steps to be created (e.g., setting up multiple parts).
 * 2. You want the exact SAME construction process to be able to create DIFFERENT representations.
 * 3. The problem explicitly mentions separating "construction" from "representation" or using a "Director".
 */

// ==========================================
// STEP 1: The Product
// ==========================================
/**
 * The complex object being built.
 * Replace 'ComplexProduct' with what you are building (e.g., Meal, House, Computer).
 */


/*
[ ] Did I create the Product class containing the attributes to be built?

[ ] Did I create a Builder Interface with a method for each step and a getResult() method?

[ ] Do my Concrete Builders instantiate a new Product in their constructor and implement all step methods?

[ ] Did I create a Director class that takes a builder and dictates the order of execution?

[ ] In the Client/Main, did I correctly pass the Builder to the Director, call the construction method, and then extract the final object from the Builder?
 */
class ComplexProduct {
    // Replace these with the actual parts of your complex object
    private String part1;
    private String part2;
    private String part3;

    public void setPart1(String part1) { this.part1 = part1; }
    public void setPart2(String part2) { this.part2 = part2; }
    public void setPart3(String part3) { this.part3 = part3; }

    @Override
    public String toString() {
        return "Product [Part1: " + part1 + ", Part2: " + part2 + ", Part3: " + part3 + "]";
    }
}

// ==========================================
// STEP 2: The Builder Interface
// ==========================================
/**
 * Declares the product construction steps common to all types of builders.
 * Replace 'ProductBuilder' with your domain name (e.g., MealBuilder).
 */
interface ProductBuilder {
    // Replace these with the actual steps to build the object
    void buildPart1();
    void buildPart2();
    void buildPart3();
    
    // Method to return the final constructed object
    ComplexProduct getResult();
}

// ==========================================
// STEP 3: Concrete Builders
// ==========================================
/**
 * Implement the Builder interface to provide specific representations.
 * You will usually have more than one of these (e.g., VegMealBuilder, MeatMealBuilder).
 */
class ConcreteBuilderA implements ProductBuilder {
    private ComplexProduct product;

    public ConcreteBuilderA() {
        this.product = new ComplexProduct(); // Initialize the empty product
    }

    @Override
    public void buildPart1() {
        product.setPart1("Builder A - Specific Part 1");
    }

    @Override
    public void buildPart2() {
        product.setPart2("Builder A - Specific Part 2");
    }

    @Override
    public void buildPart3() {
        product.setPart3("Builder A - Specific Part 3");
    }

    @Override
    public ComplexProduct getResult() {
        return this.product;
    }
}

// ==========================================
// STEP 4: The Director
// ==========================================
/**
 * Defines the ORDER in which the building steps are called.
 * The Director doesn't know WHAT is being built, only HOW to sequence the steps.
 * Replace 'Director' with a manager/controller name (e.g., Waiter, Contractor, TravelAgent).
 */
class Director {
    private ProductBuilder builder;

    // The Director receives a specific builder from the client
    public void setBuilder(ProductBuilder builder) {
        this.builder = builder;
    }

    // The standardized sequence of steps for construction
    public void construct() {
        builder.buildPart1();
        builder.buildPart2();
        builder.buildPart3();
    }
}

// ==========================================
// STEP 5: Client Code
// ==========================================
/**
 * Wires the Director and the specific Builder together.
 */
public class ClientApplication {
    public static void main(String[] args) {
        // 1. Create the Director
        Director director = new Director();

        // 2. Create the specific Builder you want
        ProductBuilder builderA = new ConcreteBuilderA();

        // 3. Give the builder to the Director
        director.setBuilder(builderA);

        // 4. Tell the Director to execute the building steps
        director.construct();

        // 5. Ask the BUILDER for the final result
        ComplexProduct finalProduct = builderA.getResult();
        
        System.out.println(finalProduct);
    }
}

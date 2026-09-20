/*
 * =====================================================================
 *  Decorator Pattern                       Design Pattern | Medium  MUST-KNOW
 * =====================================================================
 *
 * PATTERN
 *   Decorator - Structural family. Also called Wrapper (like Adapter, but the
 *   wrapper here keeps the SAME interface instead of changing it).
 *
 * INTENT
 *   Add behaviour to one object at runtime by wrapping it, without touching
 *   its class and without creating a subclass per combination. A coffee gets
 *   milk, sugar, or milk+sugar+extra sugar by stacking wrappers.
 *
 * WHEN TO USE, WHEN NOT
 *   Use    - the combinations of optional features would explode into
 *            subclasses (MilkCoffee, MilkSugarCoffee, MilkDoubleSugar...).
 *   Use    - features must be added and removed per object, at runtime.
 *   Not    - the feature set is fixed and small: plain fields are simpler.
 *   Not    - you must change the interface -> Adapter.
 *   Not    - you want to control access rather than add behaviour -> Proxy.
 *   Cost   - many small objects, and debugging shows a deep wrapper chain.
 *
 * ROLES IN THIS CODE
 *   Coffee                 Component          - the shared interface.
 *   SimpleCoffee           ConcreteComponent  - the thing actually decorated.
 *   CoffeeDecorator        Decorator (base)   - implements Coffee AND holds a
 *                                               Coffee; the double identity.
 *   MilkDecorator          ConcreteDecorator  - adds ", Milk" and +1.5.
 *   SugarDecorator         ConcreteDecorator  - adds ", Sugar" and +0.5.
 *   DecoratorDesignPattern Client             - builds and reads the chain.
 *
 * KEY INSIGHT
 *   The wrapper implements the same interface as the wrappee. That single
 *   constraint is what lets wrappers stack: a decorator's constructor accepts
 *   Coffee, and a decorator IS a Coffee, so it can be fed into the next one.
 *   Each call recurses down the chain and each level adds its bit on the way
 *   back up. Proxy and Composite both assume this same recursion.
 *
 * COMPLEXITY
 *   Time  O(n) per getDescription()/getCost() call - one walk of n wrappers.
 *   Space O(n) for the n wrapper objects in the chain.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Decorator vs Proxy: identical structure, so what differs? (Intent, and
 *     who creates the wrappee: a proxy usually owns/creates it, a decorator
 *     is handed it.)
 *   - Decorator vs inheritance: why is "MilkSugarCoffee" the wrong answer?
 *   - Does order matter? Build milk-then-sugar and sugar-then-milk and say why
 *     the cost is equal here but the description string is not.
 *   - Where does the JDK use it? (java.io: BufferedReader(new FileReader(..)),
 *     Collections.unmodifiableList, HttpServletRequestWrapper in servlets.)
 *
 * RUN
 *   main() runs 5 cases: bare component, one decorator, two decorators, the
 *   same decorator applied twice, and a reversed order. Prints actual vs
 *   expected on the same line.
 */

interface Coffee {
    String getDescription();

    double getCost();
}

/** ConcreteComponent: the object at the bottom of every chain. */
class SimpleCoffee implements Coffee {

    @Override
    public String getDescription() {
        return "Simple Coffee";
    }

    @Override
    public double getCost() {
        return 5.0;
    }
}

/**
 * Base Decorator: IS a Coffee and HAS a Coffee. That double identity is the
 * whole pattern - it is what makes the wrappers stackable.
 */
abstract class CoffeeDecorator implements Coffee {

    protected final Coffee coffee; // the wrappee, one level down the chain

    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}

class MilkDecorator extends CoffeeDecorator {

    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Milk"; // delegate first, then add our part
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 1.5;
    }
}

class SugarDecorator extends CoffeeDecorator {

    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Sugar";
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 0.5;
    }
}

class DecoratorDesignPattern {

    public static void main(String[] args) {
        // Case 1: edge - no decorators at all. The chain is one link long.
        Coffee plain = new SimpleCoffee();
        print("case 1 bare        ", describe(plain), "Simple Coffee $5.0");

        // Case 2: typical - one wrapper.
        Coffee withMilk = new MilkDecorator(plain);
        print("case 2 milk        ", describe(withMilk), "Simple Coffee, Milk $6.5");

        // Case 3: typical - stack a second wrapper on top of the first.
        Coffee withMilkSugar = new SugarDecorator(withMilk);
        print("case 3 milk+sugar  ", describe(withMilkSugar), "Simple Coffee, Milk, Sugar $7.0");

        // Case 4: tricky - the SAME decorator twice. Inheritance cannot do this;
        // wrapping can, because a decorator is just another Coffee.
        Coffee doubleSugar = new SugarDecorator(new SugarDecorator(new MilkDecorator(new SimpleCoffee())));
        print("case 4 milk+2 sugar", describe(doubleSugar), "Simple Coffee, Milk, Sugar, Sugar $7.5");

        // Case 5: tricky - reversed order. Same cost, different description,
        // because addition commutes but string concatenation does not.
        Coffee sugarThenMilk = new MilkDecorator(new SugarDecorator(new SimpleCoffee()));
        print("case 5 sugar+milk  ", describe(sugarThenMilk), "Simple Coffee, Sugar, Milk $7.0");
    }

    private static String describe(Coffee coffee) {
        return coffee.getDescription() + " $" + coffee.getCost();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " : " + actual + "   expected " + expected);
    }
}

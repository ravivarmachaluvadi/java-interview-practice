/**
 * Demonstrates the Decorator design pattern by dynamically adding
 * features (Milk, Sugar) to a base Coffee object.
 *
 * The program starts with a SimpleCoffee and wraps it successively
 * in MilkDecorator and SugarDecorator, each augmenting the description
 * and cost. The final output shows the cumulative description and price.
 *
 * Approach: Each decorator holds a reference to a Coffee instance,
 * delegating calls while adding its own behavior. This allows flexible
 * composition without subclassing every combination.
 *
 * Time Complexity: O(n) where n is the number of decorators applied,
 * since each getDescription/getCost traverses the chain once.
 * Space Complexity: O(n) for the decorator objects created in the chain.
 * 
 */

class DecoratorDesignPattern {
    public static void main(String[] args) {
        Coffee coffee = new SimpleCoffee();
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());

        coffee = new MilkDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());

        coffee = new SugarDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());
    }
}

interface Coffee {
    String getDescription();

    double getCost();
}

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

abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
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
        return coffee.getDescription() + " decorated with Milk";
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


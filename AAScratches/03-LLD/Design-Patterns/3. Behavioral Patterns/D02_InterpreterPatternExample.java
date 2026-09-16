// Step 1: Define the Expression interface
interface Expression {
    int interpret();
}

// Step 2: Terminal expression — represents numbers
class NumberExpression implements Expression {
    private int number;

    public NumberExpression(int number) {
        this.number = number;
    }

    @Override
    public int interpret() {
        return number;
    }
}

// Step 3: Non-terminal expressions — Add and Subtract
class AddExpression implements Expression {
    private Expression left;
    private Expression right;

    public AddExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret() {
        return left.interpret() + right.interpret();
    }
}

class SubtractExpression implements Expression {
    private Expression left;
    private Expression right;

    public SubtractExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret() {
        return left.interpret() - right.interpret();
    }
}

// Step 4: Client code
class InterpreterPatternExample {
    public static void main(String[] args) {
        // Interpret the expression: 5 + 10 - 3

        Expression five = new NumberExpression(5);
        Expression ten = new NumberExpression(10);
        Expression three = new NumberExpression(3);

        // (5 + 10)
        Expression add = new AddExpression(five, ten);

        // (5 + 10) - 3
        Expression subtract = new SubtractExpression(add, three);

        System.out.println("(5 + 10) - 3 = " + subtract.interpret());
    }
}

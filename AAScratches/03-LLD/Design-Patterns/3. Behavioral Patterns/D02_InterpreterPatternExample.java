/*
 * =====================================================================
 *  Interpreter Pattern - a tiny arithmetic evaluator   Behavioral | Hard
 * =====================================================================
 *
 * PATTERN
 *   Interpreter (Behavioral family). Close cousin of Composite: the object
 *   tree IS the parsed sentence, and each node knows how to evaluate itself.
 *
 * INTENT
 *   Represent each rule of a small, stable grammar as a class, then evaluate a
 *   sentence by walking the tree those classes form. The grammar here is:
 *       expr := number | expr '+' expr | expr '-' expr
 *
 * WHEN TO USE, WHEN NOT
 *   Use when the grammar is small, changes rarely, and rules must be composed
 *   at runtime: rule engines, feature-flag predicates, spreadsheet formulas.
 *   Do NOT use for a real language - one class per rule explodes fast, and a
 *   parser generator or recursive-descent parser over a plain AST wins.
 *
 * ROLES IN THIS CODE
 *   Expression                - AbstractExpression: declares interpret()
 *   NumberExpression          - TerminalExpression: leaf holding a literal
 *   AddExpression             - NonTerminal: combines two sub-expressions
 *   SubtractExpression        - NonTerminal: combines two sub-expressions
 *   InterpreterPatternExample - Client: builds the tree, then interprets it
 *   (no Context class: nothing is looked up by name here; variables need one)
 *
 * KEY INSIGHT
 *   interpret() is recursive by construction - a non-terminal asks its children
 *   for their values and never inspects their type. Building the tree and
 *   evaluating it are separate steps, so one tree can be evaluated many times,
 *   printed, or optimised.
 *
 * INTERVIEW FOLLOW-UPS
 *   - vs Composite: same tree shape; Composite's intent is uniform part/whole
 *     treatment, Interpreter's is evaluating a grammar.
 *   - vs Visitor: new operation without touching node classes -> Visitor;
 *     new node type without touching existing code -> Interpreter.
 *   - Add variables? Pass a Context (Map<String,Integer>) into interpret() and
 *     add a VariableExpression terminal that looks itself up.
 *   - Who builds the tree? A parser - parsePostfix() below is the smallest one.
 *
 * RUN
 *   main() runs 5 cases (typical, single leaf, negative, left-nesting, parsed).
 */

import java.util.ArrayDeque;
import java.util.Deque;

/** AbstractExpression: every node in the tree can evaluate itself. */
interface Expression {
    int interpret();
}

/** TerminalExpression: a literal number. Recursion bottoms out here. */
class NumberExpression implements Expression {
    private final int number;

    public NumberExpression(int number) {
        this.number = number;
    }

    @Override
    public int interpret() {
        return number;
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }
}

/** NonTerminalExpression: left + right, whatever those sub-trees turn out to be. */
class AddExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public AddExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret() {
        // The whole pattern in one line: ask the children, do not inspect them.
        return left.interpret() + right.interpret();
    }

    @Override
    public String toString() {
        return "(" + left + " + " + right + ")";
    }
}

/** NonTerminalExpression: left - right. Order matters, so the tree shape matters. */
class SubtractExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public SubtractExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret() {
        return left.interpret() - right.interpret();
    }

    @Override
    public String toString() {
        return "(" + left + " - " + right + ")";
    }
}

/** Client: builds expression trees by hand and, in the last case, by parsing. */
class InterpreterPatternExample {

    /**
     * Smallest useful parser: reverse Polish notation to an Expression tree.
     * Postfix is used because it needs no precedence rules - one token, one
     * decision - which keeps the grammar-to-class mapping the visible part.
     * "5 10 + 3 -"  ->  ((5 + 10) - 3)
     */
    static Expression parsePostfix(String tokens) {
        Deque<Expression> stack = new ArrayDeque<>();
        for (String token : tokens.trim().split("\\s+")) {
            if (token.equals("+") || token.equals("-")) {
                // Operands were pushed left-then-right, so pop right first.
                Expression right = stack.pop();
                Expression left = stack.pop();
                stack.push(token.equals("+")
                        ? new AddExpression(left, right)
                        : new SubtractExpression(left, right));
            } else {
                stack.push(new NumberExpression(Integer.parseInt(token)));
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Malformed postfix expression: " + tokens);
        }
        return stack.pop();
    }

    private static void print(String label, Expression expression, int expected) {
        System.out.println(label + ": " + expression + " = " + expression.interpret()
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        Expression five = new NumberExpression(5);
        Expression ten = new NumberExpression(10);
        Expression three = new NumberExpression(3);

        // Case 1 - typical: (5 + 10) - 3
        Expression subtract = new SubtractExpression(new AddExpression(five, ten), three);
        print("case 1 typical      ", subtract, 12);

        // Case 2 - edge: a lone terminal is already a valid expression tree.
        print("case 2 single leaf  ", new NumberExpression(7), 7);

        // Case 3 - tricky: 2 - (3 + 4) is negative, and proves the tree shape
        // (not the token order) decides the answer.
        Expression negative = new SubtractExpression(
                new NumberExpression(2),
                new AddExpression(new NumberExpression(3), new NumberExpression(4)));
        print("case 3 negative     ", negative, -5);

        // Case 4 - tricky: left-nested subtraction, ((20 - 5) - 5), shows that
        // subtraction is not associative so nesting direction is load-bearing.
        Expression leftNested = new SubtractExpression(
                new SubtractExpression(new NumberExpression(20), new NumberExpression(5)),
                new NumberExpression(5));
        print("case 4 left nesting ", leftNested, 10);

        // Case 5 - the same tree as case 1, built by a parser instead of by hand.
        print("case 5 parsed       ", parsePostfix("5 10 + 3 -"), 12);
    }
}

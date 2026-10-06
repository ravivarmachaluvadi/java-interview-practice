/*
 * =====================================================================
 *  Interpreter - coupon eligibility rules            Behavioral | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Marketing writes coupon rules in an admin panel, for example
 *     cartTotal >= 1000 AND category = ELECTRONICS OR tier = GOLD
 *   and expects them live without a code deploy. Checkout must decide, per
 *   cart, whether the rule matches.
 *
 * KEY INSIGHT
 *   Turn the rule text into a tree of small objects, one class per grammar
 *   element: Compare is a leaf, And / Or hold two sub-rules. matches() on a
 *   node asks its children and combines the answers, so evaluation is plain
 *   recursion. Splitting on OR first and AND second makes AND bind tighter,
 *   as in SQL.
 *
 * ROLES IN THIS CODE
 *   Rule                  AbstractExpression
 *   Compare               TerminalExpression (field op value)
 *   And, Or               NonterminalExpression
 *   Map<String, String>   Context (the cart's facts)
 *   RuleParser            builds the tree (not part of the GoF roles)
 *
 * INTERVIEW FOLLOW-UPS
 *   - When it stops scaling: parentheses, NOT, precedence levels -> use a
 *     real parser (ANTLR) or an engine (Drools, Spring SpEL).
 *   - Seen in: Spring SpEL, regex engines, SQL WHERE clauses, feature-flag
 *     targeting rules.
 *
 * RUN
 *   6 cases: the parsed tree, eligible by total + category, eligible by
 *   GOLD only, not eligible, missing field, malformed rule.
 */

import java.util.Map;

interface Rule {
    boolean matches(Map<String, String> cart);
}

record Compare(String field, String op, String value) implements Rule {
    public boolean matches(Map<String, String> cart) {
        String actual = cart.get(field);
        if (actual == null) {
            return false; // unknown fact never matches
        }
        if (op.equals("=")) {
            return actual.equals(value);
        }
        int left = Integer.parseInt(actual);
        int right = Integer.parseInt(value);
        return switch (op) {
            case ">=" -> left >= right;
            case "<=" -> left <= right;
            case ">" -> left > right;
            case "<" -> left < right;
            default -> throw new IllegalArgumentException("unknown operator " + op);
        };
    }

    @Override
    public String toString() {
        return field + " " + op + " " + value;
    }
}

record And(Rule left, Rule right) implements Rule {
    public boolean matches(Map<String, String> cart) {
        return left.matches(cart) && right.matches(cart);
    }

    @Override
    public String toString() {
        return "(" + left + " AND " + right + ")";
    }
}

record Or(Rule left, Rule right) implements Rule {
    public boolean matches(Map<String, String> cart) {
        return left.matches(cart) || right.matches(cart);
    }

    @Override
    public String toString() {
        return "(" + left + " OR " + right + ")";
    }
}

class RuleParser {
    /** OR is split first, so AND groups bind tighter. */
    static Rule parse(String text) {
        Rule rule = null;
        for (String orPart : text.split(" OR ")) {
            Rule andRule = null;
            for (String condition : orPart.split(" AND ")) {
                String[] t = condition.trim().split("\\s+");
                if (t.length != 3) {
                    throw new IllegalArgumentException("bad condition '" + condition.trim() + "'");
                }
                Rule compare = new Compare(t[0], t[1], t[2]);
                andRule = andRule == null ? compare : new And(andRule, compare);
            }
            rule = rule == null ? andRule : new Or(rule, andRule);
        }
        return rule;
    }
}

class InterpreterPatternExample {

    public static void main(String[] args) {
        Rule rule = RuleParser.parse("cartTotal >= 1000 AND category = ELECTRONICS OR tier = GOLD");
        print("case 1 tree        ", rule,
                "((cartTotal >= 1000 AND category = ELECTRONICS) OR tier = GOLD)");

        // Case 2-4: one parsed rule, evaluated against three different carts.
        print("case 2 big gadget  ", rule.matches(Map.of(
                "cartTotal", "1500", "category", "ELECTRONICS", "tier", "SILVER")), true);
        print("case 3 gold member ", rule.matches(Map.of(
                "cartTotal", "400", "category", "BOOKS", "tier", "GOLD")), true);
        print("case 4 big books   ", rule.matches(Map.of(
                "cartTotal", "1500", "category", "BOOKS", "tier", "SILVER")), false);

        // Case 5: edge. A guest has no tier at all; the missing fact is just false.
        print("case 5 guest       ", rule.matches(Map.of(
                "cartTotal", "900", "category", "ELECTRONICS")), false);

        // Case 6: a typo in the admin panel is caught when the rule is saved.
        String outcome;
        try {
            RuleParser.parse("cartTotal >= AND tier = GOLD");
            outcome = "parsed";
        } catch (IllegalArgumentException e) {
            outcome = e.getMessage();
        }
        print("case 6 bad rule    ", outcome, "bad condition 'cartTotal >='");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

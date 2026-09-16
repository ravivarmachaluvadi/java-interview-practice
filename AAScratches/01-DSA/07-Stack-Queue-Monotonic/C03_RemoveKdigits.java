import java.util.Stack;

// 1432219 , k=3, smallest after 3 removals 1219
class RemoveKdigits {
    public static String removeKdigits(String num, int k) {
        //edge case
        if (num.length() == k) {
            return "0";
        }
        // use a stack to keep track of the digits in the new number
        Stack<Character> decreasingStack = new Stack<>();

        for (int i = 0; i < num.length(); i++) {
            while (!decreasingStack.isEmpty() && decreasingStack.peek() > num.charAt(i) && k > 0) {
                decreasingStack.pop();
                //decrement k
                k--;
            }
            decreasingStack.push(num.charAt(i));
        }
        while (k > 0) {
            //pop off the top of the stack
            decreasingStack.pop();
            //decrement k
            k--;
        }

        StringBuilder newNum = new StringBuilder();
        while (!decreasingStack.isEmpty()) {
            newNum.append(decreasingStack.pop());
        }
        //reverse the new number
        newNum.reverse();

        //remove any leading 0's
        while (newNum.length() > 1 && newNum.charAt(0) == '0')
            newNum.deleteCharAt(0);
        return newNum.toString();
    }

    public static void main(String[] args) {
        System.out.println(removeKdigits("1432219", 3));
    }
}

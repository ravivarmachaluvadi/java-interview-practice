import java.util.Stack;

class StackTraversal {
    public static void main(String[] args) {
        //1
        //2
        //3
        //4
        //----------------------------------------------------------
        //4
        //3
        //2
        //1
        Stack<String> stack = new Stack<>();
        stack.add("1");
        stack.add("2");
        stack.add("3");
        stack.add("4");

        for (String string : stack) {
            System.out.println(string);
        }
        System.out.println("----------------------------------------------------------");
        while (!stack.isEmpty()) {
            System.out.println(stack.pop());
        }

    }
}

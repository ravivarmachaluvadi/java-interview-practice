public class Tricky7 {
    public static void main(String[] args) {
        int x = 9;

        if (x == 9) {
            // Compilation Error
            // Variable 'x' is already defined in the scope
            int x = 10;
            System.out.println(x);
        }
    }
}


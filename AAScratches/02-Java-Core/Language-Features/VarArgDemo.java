import java.util.ArrayList;
import java.util.List;

class VarArgDemo {
    public static void main(String... args) {
        List<Integer> list = new ArrayList<>();
        String[] list1 = new String[10];

        varArgDemo(list);
        varArgDemo(list1);
        varArgDemo(list1);
        varArgDemo("");

    }

    private static void varArgDemo(Integer... list) {
    }

    private static void varArgDemo(String... list) {
    }
}

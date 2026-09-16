import java.util.ArrayList;
import java.util.List;
class PascalTrianleTwoLoops {
    public static void main(String[] args) {
// 1   1
//1  2  1
        List<List<Integer>> list = new ArrayList<>();
        list.add(List.of(1));

        List<Integer> prevRow = List.of(1, 1);
        list.add(prevRow);

        for (int i = 3; i < 6; i++) {
            List<Integer> currentRow = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                if (j == 0 || j == i - 1)
                    currentRow.add(1);
                else
                    currentRow.add(prevRow.get(j) + prevRow.get(j - 1));
            }
            list.add(currentRow);
            prevRow = currentRow;
        }

        for (List<Integer> integers : list)
            System.out.println(integers);
    }
}
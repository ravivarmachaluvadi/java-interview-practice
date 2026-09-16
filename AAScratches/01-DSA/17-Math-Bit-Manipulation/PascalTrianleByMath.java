import java.util.ArrayList;
import java.util.List;

class PascalTrianleByMath {
    public static void main(String[] args) {

//    5*4*3*2*1
//    1*2*3*4*5 <=

//     5th row [1, 4, 6, 4, 1]
//      (row-col)/col-->5-1/1--> 2nd Element
        List<List<Integer>> pascalTriangle = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            List<Integer> generatedRow = generateRow(i);
            pascalTriangle.add(generatedRow);
        }

        for (List<Integer> integerList : pascalTriangle) {
            System.out.println(integerList);
        }
    }

    private static List<Integer> generateRow(int rowNumber) {
        List<Integer> pascalRow = new ArrayList<>();
        pascalRow.add(1);
        int ans = 1;
        for (int col = 1; col < rowNumber; col++) {
            ans *= (rowNumber - col);
            ans /= col;
            pascalRow.add(ans);
        }
        return pascalRow;
    }
}

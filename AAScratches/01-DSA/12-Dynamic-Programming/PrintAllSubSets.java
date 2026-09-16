import java.util.ArrayList;
import java.util.List;

class PrintAllSubSets {
    public static void main(String[] args) {
        int[] arr = new int[]{1, 2, 3};
        ArrayList<Integer> list = new ArrayList<>();
        printAllSubSets(0, arr, list);
    }

    private static void printAllSubSets(int index,
                                        int[] arr,
                                        List<Integer> ansList) {
        if (index == arr.length) {
            System.out.println(ansList);
            return;
        }
        // pick Element and go for next element to pick
        ansList.add(arr[index]);
        printAllSubSets(index + 1, arr, ansList);// Include
        // not-pick prev Element and go for next element to pick
        ansList.remove(ansList.size() - 1);
        printAllSubSets(index + 1, arr, ansList);// Exclude
    }
}
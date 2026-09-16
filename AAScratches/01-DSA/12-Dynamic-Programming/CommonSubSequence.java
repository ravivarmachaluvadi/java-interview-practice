import java.util.ArrayList;
import java.util.List;

class CommonSubSequence {

    static List<Character> list = new ArrayList<>();
    static int maxLength = -1;
    static int lcsCount = 0;

    public static void main(String[] args) {
        String s1 = "abcdef";
        String s2 = "ace";

        ArrayList<Character> arrayList = new ArrayList<>();
        commonSubSequence(s1, s2, 0, 0, arrayList, 0);

        System.out.println(list);
        System.out.println(lcsCount);

    }


    private static void commonSubSequence(String s1, String s2, int i, int j,
                                          ArrayList<Character> arrayList, int count) {

        if (i == s1.length() || s2.length() == j) {
            if (maxLength < arrayList.size()) {
                maxLength = arrayList.size();
                list.clear();
                list.addAll(arrayList);
            }
            if (count > lcsCount) {
                lcsCount = count;
            }
            // remember
            return;
        }

        if (s1.charAt(i) == s2.charAt(j)) {
            arrayList.add(s1.charAt(i));
            commonSubSequence(s1, s2, i + 1, j + 1, arrayList, 1 + count);
            arrayList.remove(arrayList.size() - 1);
        }
        commonSubSequence(s1, s2, i + 1, j, arrayList, count);
        commonSubSequence(s1, s2, i, j + 1, arrayList, count);
    }
}

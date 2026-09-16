import java.util.*;

class RemoveInsertGetO1 {

    List<Integer> list;
    Map<Integer, Integer> map;
    Random rand;

    public RemoveInsertGetO1() {
        list = new ArrayList<>(); // 0
        map = new HashMap<>(); //
        rand = new Random();
    }

    public boolean insert(int val) {
        if (map.containsKey(val)) {
            return false;
        }
        list.add(val); // 1
        map.put(val, list.size() - 1); //1,1
        return true;
    }

    public boolean remove(int val) {
        if (!map.containsKey(val)) return false;

        int lastVal = list.get(list.size() - 1); // 1
        int valIdx = map.get(val);// 0
        list.set(valIdx, lastVal);
        map.put(lastVal, valIdx);
        list.remove(list.size() - 1);
        map.remove(val);

        return true;
    }
}


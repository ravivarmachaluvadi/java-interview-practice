import java.util.*;

// https://leetcode.com/problems/snapshot-array/description/
// 1146. Snapshot Array
public class SnapshotArray {
    private static class Version {
        int snapId;
        int val;
        Version(int s, int v) { snapId = s; val = v; }
    }

    private List<Version>[] history;
    private int currentSnapId;

    public SnapshotArray(int length) {
        history = (List<Version>[]) new List[length];
        for (int i = 0; i < length; i++) {
            history[i] = new ArrayList<>();
            // initialize with value 0 at snapId = 0
            history[i].add(new Version(0, 0));
        }
        currentSnapId = 0;
    }

    public void set(int index, int val) {
        List<Version> list = history[index];
        Version last = list.get(list.size() - 1);
        if (last.snapId == currentSnapId) {
            // same snapshot, just update value
            last.val = val;
        } else {
            // new snapshot id, append new version
            list.add(new Version(currentSnapId, val));
        }
    }

    public int snap() {
        int ret = currentSnapId;
        currentSnapId++;
        return ret;
    }

    public int get(int index, int snapId) {
        List<Version> list = history[index];
        // binary search for largest version.snapId <= snapId
        int lo = 0, hi = list.size() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (list.get(mid).snapId <= snapId) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        // hi now points to the correct version
        return list.get(hi).val;
    }

    // --- main method with example ---
    public static void main(String[] args) {
        // Example from description
        SnapshotArray snapshotArr = new SnapshotArray(3);  // length = 3
        snapshotArr.set(0, 5);
        int snap0 = snapshotArr.snap();  // snap0 = 0
        snapshotArr.set(0, 6);
        int val0 = snapshotArr.get(0, snap0);  // should return 5

        System.out.println("snap0 = " + snap0);
        System.out.println("value at index 0 at snap0 = " + val0);
        // expected output: 
        // snap0 = 0
        // value at index 0 at snap0 = 5
    }
}

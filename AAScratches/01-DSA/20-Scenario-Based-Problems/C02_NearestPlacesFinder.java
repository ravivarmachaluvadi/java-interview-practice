/*
 * =====================================================================
 *  K Nearest Places to a Coordinate        Scenario / "find me nearby" | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a user's latitude/longitude, a list of places (name, lat, lon) and an
 *   integer k, return the k places closest to the user, nearest first. Distance is
 *   great-circle distance on the Earth's surface, not straight-line on a flat map.
 *   k may exceed the number of places (return all) or be zero (return nothing), and
 *   the input list must not be reordered as a side effect.
 *
 * EXAMPLE
 *   user = Connaught Place, Delhi (28.6139, 77.2090), k = 3
 *   places = Taj Mahal, India Gate, Red Fort, Qutub Minar, Lotus Temple
 *     -> [India Gate (2.0 km), Red Fort (5.7 km), Lotus Temple (8.3 km)]
 *   k = 0   -> []                                    edge case main() runs
 *   k = 99  -> all 5, ordered India Gate .. Taj Mahal (Agra, ~180 km away)
 *
 * APPROACH  (Haversine distance, then top-k by sort or by bounded heap)
 *   1. Haversine: convert the latitude and longitude deltas to radians, apply
 *      a = sin^2(dLat/2) + cos(lat1) * cos(lat2) * sin^2(dLon/2),
 *      c = 2 * atan2(sqrt(a), sqrt(1-a)), distance = R * c with R = 6371 km.
 *   2. nearestBySort: compute each place's distance ONCE into a (place, distance)
 *      record, sort those records ascending, take the first k. O(n log n).
 *   3. nearestByHeap: keep a size-k MAX-heap keyed on distance. Offer every place and,
 *      whenever the heap exceeds k, evict its head - the farthest place held. At the
 *      end the heap holds the k nearest; drain and reverse. O(n log k).
 *   4. Both use the same tie-break (equal distance -> name ascending) so their answers
 *      are identical and main() can assert that.
 *
 * KEY INSIGHT
 *   The distance key is EXPENSIVE - Haversine costs several trig calls - and a
 *   comparator is invoked O(n log n) times, so computing the distance inside the
 *   comparator evaluates it roughly n log n times instead of n. Compute the key once
 *   per element, sort the decorated records, then strip the decoration. That is the
 *   decorate-sort-undecorate (Schwartzian transform) pattern, and it is the answer
 *   interviewers are fishing for here. The second half is the same top-k lesson as any
 *   heap problem, with the polarity flipped: to keep the k SMALLEST distances you hold
 *   a MAX-heap, because the thing you must cheaply discard is the current worst.
 *   Fixed: the original sorted the caller's list in place (a visible side effect, and
 *   an exception on an immutable list), recomputed Haversine inside the comparator,
 *   returned a subList view aliased to that list, and had no tie-break, so places at
 *   equal distance could come back in either order.
 *
 * COMPLEXITY
 *   Time  O(n log n) sort form, O(n log k) heap form; n Haversine calls either way
 *   Space O(n) for the decorated copy in the sort form, O(k) for the heap form
 *
 * INTERVIEW FOLLOW-UPS
 *   - k much smaller than n: why the bounded heap wins, and when Quickselect (O(n)
 *     average) beats both.
 *   - Millions of places and many queries: precompute a spatial index - geohash,
 *     quadtree, k-d tree or PostGIS/S2 cells - so you never scan every place.
 *   - Can you skip Haversine? For ranking only, squared equirectangular distance
 *     (dLat, dLon * cos(lat)) preserves order locally and costs one cosine.
 *   - Filter by a radius as well as k, or weight the ranking by rating or open hours.
 *
 * RUN
 *   main() runs 4 cases (typical k=3, k larger than the list, k=0, a deliberate
 *   distance tie) through both implementations and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Place {
    final String name;
    final double latitude;
    final double longitude;

    public Place(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return name;
    }
}

class NearestPlacesFinder {

    private static final double EARTH_RADIUS_KM = 6371;

    /** A place with its distance already computed - the "decorate" half of the pattern. */
    private static final class Scored {
        final Place place;
        final double distanceKm;

        Scored(Place place, double distanceKm) {
            this.place = place;
            this.distanceKm = distanceKm;
        }
    }

    /** Nearest first; equal distances broken by name so the answer is deterministic. */
    private static final Comparator<Scored> NEAREST_FIRST =
            Comparator.<Scored>comparingDouble(s -> s.distanceKm)
                      .thenComparing(s -> s.place.name);

    // ---------- The distance kernel ----------

    /** Great-circle distance in kilometres between two lat/lon points (Haversine). */
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /** Decorate: one Haversine call per place, never one per comparison. */
    private static List<Scored> score(double userLat, double userLon, List<Place> places) {
        List<Scored> scored = new ArrayList<>(places.size());
        for (Place p : places) {
            scored.add(new Scored(p, distance(userLat, userLon, p.latitude, p.longitude)));
        }
        return scored;
    }

    private static List<Place> undecorate(List<Scored> scored) {
        List<Place> out = new ArrayList<>(scored.size());
        for (Scored s : scored) out.add(s.place);
        return out;
    }

    // ---------- Approach 1: decorate, sort, undecorate ----------

    public static List<Place> nearestBySort(double userLat, double userLon,
                                            List<Place> places, int k) {
        if (k <= 0) return new ArrayList<>();

        List<Scored> scored = score(userLat, userLon, places); // a COPY: caller's list untouched
        scored.sort(NEAREST_FIRST);
        return undecorate(scored.subList(0, Math.min(k, scored.size())));
    }

    // ---------- Approach 2: bounded max-heap, O(n log k) ----------

    public static List<Place> nearestByHeap(double userLat, double userLon,
                                            List<Place> places, int k) {
        if (k <= 0) return new ArrayList<>();

        // Farthest-first heap: its head is the worst candidate currently held.
        PriorityQueue<Scored> farthestFirst = new PriorityQueue<>(NEAREST_FIRST.reversed());
        for (Place p : places) {
            farthestFirst.offer(new Scored(p, distance(userLat, userLon, p.latitude, p.longitude)));
            if (farthestFirst.size() > k) {
                farthestFirst.poll(); // drop the farthest, never the nearest
            }
        }

        // Draining a farthest-first heap gives descending order, so reverse it.
        List<Place> out = new ArrayList<>();
        while (!farthestFirst.isEmpty()) out.add(farthestFirst.poll().place);
        Collections.reverse(out);
        return out;
    }

    // ---------- Test harness ----------

    private static void check(String label, double lat, double lon,
                              List<Place> places, int k, String expected) {
        List<Place> bySort = nearestBySort(lat, lon, places, k);
        List<Place> byHeap = nearestByHeap(lat, lon, places, k);
        System.out.println(label + " sort: " + bySort + "   expected " + expected);
        System.out.println(label + " heap: " + byHeap + "   expected " + expected
                + "   (agrees with sort: " + bySort.equals(byHeap) + ", expected true)");
    }

    public static void main(String[] args) {
        List<Place> places = Arrays.asList(
                new Place("Taj Mahal", 27.1751, 78.0421),
                new Place("India Gate", 28.6129, 77.2295),
                new Place("Red Fort", 28.6562, 77.2410),
                new Place("Qutub Minar", 28.5244, 77.1855),
                new Place("Lotus Temple", 28.5535, 77.2588));

        double userLat = 28.6139; // Connaught Place, Delhi
        double userLon = 77.2090;

        // case 1 - typical: the 3 nearest landmarks
        check("case 1", userLat, userLon, places, 3,
                "[India Gate, Red Fort, Lotus Temple]");

        // case 2 - edge: k larger than the list, so the full ranking comes back
        check("case 2", userLat, userLon, places, 99,
                "[India Gate, Red Fort, Lotus Temple, Qutub Minar, Taj Mahal]");

        // case 3 - edge: k = 0 asks for nothing
        check("case 3", userLat, userLon, places, 0, "[]");

        // case 4 - tricky: two places at an identical distance (same point, mirrored
        // offsets), so only the name tie-break makes the answer reproducible.
        List<Place> tied = Arrays.asList(
                new Place("West", 28.6139, 77.1090),
                new Place("East", 28.6139, 77.3090),
                new Place("Far", 28.6139, 78.2090));
        check("case 4", userLat, userLon, tied, 2, "[East, West]");

        // The input list must survive the calls unreordered.
        System.out.println("case 5: input order " + places
                + "   expected [Taj Mahal, India Gate, Red Fort, Qutub Minar, Lotus Temple]");

        // The distances behind case 1, for a reader who wants the numbers.
        for (Place p : nearestBySort(userLat, userLon, places, 3)) {
            System.out.printf("  %-14s %.2f km%n", p.name,
                    distance(userLat, userLon, p.latitude, p.longitude));
        }
    }
}

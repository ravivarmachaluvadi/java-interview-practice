/**
 * Finds the k closest places to a given latitude/longitude using the Haversine formula.
 *
 * The algorithm computes the great‑circle distance from the user location to each
 * place, sorts all places by that distance, and returns the first k entries.
 *
 * Time Complexity: O(n log n) due to sorting (n = number of places).
 * Space Complexity: O(1) auxiliary space beyond the input list; the sort is in‑place.
 */
import java.util.*;

class Place {
    String name;
    double latitude;
    double longitude;

    public Place(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}


class NearestPlacesFinder {

    // Haversine Formula to calculate distance between two lat/lng
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth radius in km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // distance in KM
    }

    public static List<Place> findNearest(double userLat, double userLon,
                                          List<Place> places, int k) {
        // Sort by distance
        places.sort(Comparator.comparingDouble(
                p -> distance(userLat, userLon, p.latitude, p.longitude)
        ));
        return places.subList(0, Math.min(k, places.size()));
    }

    public static void main(String[] args) {

        // Example list of places
        List<Place> places = Arrays.asList(
                new Place("Taj Mahal", 27.1751, 78.0421),
                new Place("India Gate", 28.6129, 77.2295),
                new Place("Red Fort", 28.6562, 77.2410),
                new Place("Qutub Minar", 28.5244, 77.1855),
                new Place("Lotus Temple", 28.5535, 77.2588)
        );

        // Given latitude & longitude
        double userLat = 28.6139; // Connaught Place, Delhi
        double userLon = 77.2090;

        // Find 3 nearest places
        List<Place> nearest = findNearest(userLat, userLon, places, 3);

        System.out.println("Nearest Places:");
        for (Place p : nearest) {
            double dist = distance(userLat, userLon, p.latitude, p.longitude);
            System.out.printf("%s -> %.2f KM\n", p.name, dist);
        }
    }
}

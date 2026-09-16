/**
 * Implements a simple parking lot system that assigns vehicles to appropriate spots,
 * generates tickets, and calculates fees upon exit.
 *
 * The system supports multiple vehicle types (CAR, BIKE, TRUCK) mapped to spot sizes
 * (SMALL, MEDIUM, LARGE). Each floor holds a list of spots; the lot searches floors in order
 * for an available spot matching the vehicle's required size. When parked, a ticket with
 * a unique ID and entry timestamp is created. On exit, the ticket records the exit time,
 * computes the fee (minimum ₹20 or ₹10 per hour), and frees the spot.
 *
 * Approach:
 * - Map VehicleType to ParkingSpotType via a switch expression.
 * - Iterate floors to find first available matching spot using streams.
 * - Use UUID for unique ticket IDs; LocalDateTime and Duration for timing.
 * - Synchronize parkVehicle/exitVehicle to ensure thread safety in a singleton lot.
 *
 * Time Complexity:
 * O(F + S) per parking operation, where F = number of floors and S = spots per floor
 * (worst‑case scans all spots). Exit is O(1).
 *
 * Space Complexity:
 * O(N) for storing the list of floors and their spots; each ticket uses constant space.
 */
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

enum VehicleType {
    CAR, BIKE, TRUCK
}

enum ParkingSpotType {
    SMALL, MEDIUM, LARGE
}

enum ParkingStatus {
    AVAILABLE, OCCUPIED
}

abstract class Vehicle {
    private final String plateNo;
    private final VehicleType type;

    public Vehicle(String plateNo, VehicleType type) {
        this.plateNo = plateNo;
        this.type = type;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public VehicleType getType() {
        return type;
    }
}

class Car extends Vehicle {

    public Car(String plateNo, VehicleType type) {
        super(plateNo, type);
    }
}

class ParkingSpot {
    private final String id;
    private final ParkingSpotType spotType;
    private ParkingStatus status;
    private Vehicle vehicle;

    public ParkingSpot(String id, ParkingSpotType spotType) {
        this.id = id;
        this.spotType = spotType;
    }

    boolean isAvailable() {
        return status == ParkingStatus.AVAILABLE;
    }

    public void parkVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        status = ParkingStatus.OCCUPIED;
    }

    public void removeVehicle() {
        this.vehicle = null;
        status = ParkingStatus.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public ParkingSpotType getSpotType() {
        return spotType;
    }
}

class CarSpot extends ParkingSpot {

    public CarSpot(String id) {
        super(id, ParkingSpotType.MEDIUM);
    }
}

class Ticket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double amount;

    public Ticket(String ticketId, Vehicle vehicle, ParkingSpot spot) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
    }

    public void cancelTicket() {
        this.exitTime = LocalDateTime.now();
        this.amount = calculateAmount();
    }

    private double calculateAmount() {
        long hours = Duration.between(exitTime, entryTime).toHours();
        return Math.max(20, hours * 10);
    }

    public double getAmount() {
        return amount;
    }

    public ParkingSpot getSpot() {
        return spot;
    }
}


class ParkingFloor {
    private final String floorId;
    private final List<ParkingSpot> spots;

    public ParkingFloor(String floorId, List<ParkingSpot> spots) {
        this.floorId = floorId;
        this.spots = spots;
    }

    public Optional<ParkingSpot> getAvailableSpot(ParkingSpotType type) {
        return spots.stream().filter(parkingSpot ->
                        parkingSpot.isAvailable() && parkingSpot.getSpotType() == type)
                .findFirst();
    }
}

class ParkingLot {
    private static ParkingLot instance;
    private final List<ParkingFloor> floors;

    private ParkingLot(List<ParkingFloor> floors) {
        this.floors = floors;

    }

    public static synchronized ParkingLot getInstance(List<ParkingFloor> floors) {
        if (instance == null) {
            instance = new ParkingLot(floors);
        }
        return instance;
    }

    private ParkingSpotType mapType(VehicleType type) {
        return switch (type) {
            case BIKE -> ParkingSpotType.SMALL;
            case CAR -> ParkingSpotType.MEDIUM;
            case TRUCK -> ParkingSpotType.LARGE;
        };
    }

    public synchronized Ticket parkVehicle(Vehicle vehicle) {
        for (ParkingFloor floor : floors) {
            Optional<ParkingSpot> spotOpt = floor.getAvailableSpot(mapType(vehicle.getType()));
            if (spotOpt.isPresent()) {
                ParkingSpot spot = spotOpt.get();
                spot.parkVehicle(vehicle);
                String ticketId = UUID.randomUUID().toString();
                return new Ticket(ticketId, vehicle, spot);
            }
        }
        throw new RuntimeException("No available parking spots!");
    }

    public synchronized void exitVehicle(Ticket ticket) {
        ticket.cancelTicket();
        ticket.getSpot().removeVehicle();
    }
}

class ParkingLotExample {
    public static void main(String[] args) throws InterruptedException {
        List<ParkingSpot> floor1Spots = List.of(
                new CarSpot("C1"), new CarSpot("C2")
        );
        ParkingFloor floor1 = new ParkingFloor("F1", floor1Spots);

        ParkingLot parkingLot = ParkingLot.getInstance(List.of(floor1));

        Vehicle car = new Car("TS09AB1234", VehicleType.CAR);
        Ticket ticket = parkingLot.parkVehicle(car);
        System.out.println("Car parked, ticket: " + ticket);

        Thread.sleep(2000); // simulate time
        parkingLot.exitVehicle(ticket);
        System.out.println("Vehicle exited. Amount: ₹" + ticket.getAmount());
    }
}


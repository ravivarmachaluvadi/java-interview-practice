/*
 * =====================================================================
 *  Design a Parking Lot                              LLD | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Design a multi-floor parking lot. A vehicle arrives, is given the first
 *   free spot whose size fits its type, and gets a ticket recording the spot
 *   and the entry time. On exit the ticket is closed, a fee is charged and the
 *   spot is freed for the next vehicle. If nothing fits, entry is refused.
 *   Fee rule here: 10 per completed hour, with a minimum charge of 20.
 *
 * EXAMPLE
 *   park(Car "TS09AB1234")   -> ticket on spot F1-C1
 *   park(Bike "TS09XY0007")  -> ticket on spot F2-B1   (BIKE maps to SMALL)
 *   exit(car ticket, 3h later)     -> 30.0   (3 * 10)
 *   exit(bike ticket, 10min later) -> 20.0   (minimum charge wins)
 *   park a third car when both car spots are taken -> NoSpotAvailableException
 *
 * DESIGN  (composed entities: Lot -> Floor -> Spot, plus a ticket lifecycle)
 *   Vehicle (abstract) + Car, Bike   what arrives; carries plate and VehicleType.
 *   ParkingSpot + CarSpot, BikeSpot  one parkable slot; owns its own AVAILABLE /
 *                                    OCCUPIED status and the vehicle on it.
 *   ParkingFloor                     a list of spots; can hand out the first free
 *                                    spot of a requested size.
 *   ParkingLot (singleton)           the only public API: park() and exit(). Walks
 *                                    floors in order, maps VehicleType to
 *                                    ParkingSpotType, issues and closes tickets.
 *   Ticket                           the lifecycle record: id, vehicle, spot,
 *                                    entry time, and after close() exit time + fee.
 *   FeeCalculator                    the pricing rule, kept separate so it can be
 *                                    swapped without touching allocation.
 *
 * KEY DECISIONS
 *   1. Status lives on the spot, not in a set held by the lot - one owner of the
 *      truth, so a spot can never be "free" and "occupied" at the same time.
 *      Fixed: status was never initialised, so every spot read as null and the
 *      original main() always threw "No available parking spots!".
 *   2. Enum-to-enum mapping (VehicleType -> ParkingSpotType) in one switch, so
 *      adding an EV type touches exactly one method.
 *   3. Pricing is its own type. Allocation does not know the tariff, and a
 *      weekend or EV tariff is a new FeeCalculator, not an if inside Ticket.
 *      Fixed: the fee used Duration.between(exitTime, entryTime) - the arguments
 *      were swapped, so every stay produced negative hours and the minimum
 *      charge silently hid the bug.
 *   4. park() and exit() are synchronized on the singleton lot: find-then-claim
 *      must be atomic or two cars race onto the same spot. This is the coarse,
 *      correct version; the follow-ups cover making it finer-grained.
 *   5. exit() takes the exit time as a parameter (with a now() overload) so the
 *      fee is testable without sleeping.
 *
 * COMPLEXITY
 *   Time  park O(F * S)   worst case scans every spot on every floor.
 *         exit O(1)       the ticket already points at the spot.
 *   Space O(F * S)        floors and spots; each ticket is constant size.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Make park() O(1): keep a per-size queue of free spots instead of scanning.
 *   - Should a CAR be allowed to take a LARGE spot when MEDIUM is full? That is
 *     a fit policy - make it a strategy, not a hard-coded switch.
 *   - The fee truncates: 90 minutes bills as 1 hour. Round up, or bill per slab?
 *   - The singleton ignores the floors passed on later getInstance() calls, and
 *     is untestable. Prefer dependency injection or a builder in real code.
 *   - Distribute it: several entry gates, one shared spot inventory. Where does
 *     the lock live once the lot no longer fits in one JVM?
 *
 * RUN
 *   main() runs 4 cases: a typical car park, a bike routed to a SMALL spot on
 *   another floor, two exits (3 hours vs 10 minutes, to show the minimum fee),
 *   and a full lot refusing a third car.
 */

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

/** Thrown when no spot of the required size is free anywhere in the lot. */
class NoSpotAvailableException extends RuntimeException {
    public NoSpotAvailableException(String message) {
        super(message);
    }
}

abstract class Vehicle {
    private final String plateNo;
    private final VehicleType type;

    protected Vehicle(String plateNo, VehicleType type) {
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
    public Car(String plateNo) {
        super(plateNo, VehicleType.CAR); // the subclass, not the caller, fixes the type
    }
}

class Bike extends Vehicle {
    public Bike(String plateNo) {
        super(plateNo, VehicleType.BIKE);
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
        this.status = ParkingStatus.AVAILABLE; // a brand new spot is free
    }

    public boolean isAvailable() {
        return status == ParkingStatus.AVAILABLE;
    }

    public void parkVehicle(Vehicle vehicle) {
        if (!isAvailable()) {
            throw new IllegalStateException("spot " + id + " is already occupied");
        }
        this.vehicle = vehicle;
        this.status = ParkingStatus.OCCUPIED;
    }

    public void removeVehicle() {
        this.vehicle = null;
        this.status = ParkingStatus.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public ParkingSpotType getSpotType() {
        return spotType;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}

class CarSpot extends ParkingSpot {
    public CarSpot(String id) {
        super(id, ParkingSpotType.MEDIUM);
    }
}

class BikeSpot extends ParkingSpot {
    public BikeSpot(String id) {
        super(id, ParkingSpotType.SMALL);
    }
}

/** The pricing rule, kept out of Ticket so it can be swapped independently. */
class FeeCalculator {
    private static final double RATE_PER_HOUR = 10.0;
    private static final double MINIMUM_FEE = 20.0;

    public double feeFor(LocalDateTime entryTime, LocalDateTime exitTime) {
        long hours = Duration.between(entryTime, exitTime).toHours(); // entry first, then exit
        return Math.max(MINIMUM_FEE, hours * RATE_PER_HOUR);
    }
}

class Ticket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double amount;

    public Ticket(String ticketId, Vehicle vehicle, ParkingSpot spot, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = entryTime;
    }

    /** Closes the ticket at the given time and freezes the amount owed. */
    public void close(LocalDateTime exitTime, FeeCalculator feeCalculator) {
        if (isClosed()) {
            throw new IllegalStateException("ticket " + ticketId + " is already closed");
        }
        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("exit time is before entry time");
        }
        this.exitTime = exitTime;
        this.amount = feeCalculator.feeFor(entryTime, exitTime);
    }

    public boolean isClosed() {
        return exitTime != null;
    }

    public double getAmount() {
        return amount;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    @Override
    public String toString() {
        // The ticket id is a random UUID, so show only a short prefix.
        return "Ticket{" + ticketId.substring(0, 8)
                + ", plate=" + vehicle.getPlateNo()
                + ", spot=" + spot.getId()
                + ", in=" + entryTime
                + (isClosed() ? ", out=" + exitTime + ", fee=" + amount : ", open")
                + "}";
    }
}

class ParkingFloor {
    private final String floorId;
    private final List<ParkingSpot> spots;

    public ParkingFloor(String floorId, List<ParkingSpot> spots) {
        this.floorId = floorId;
        this.spots = new ArrayList<>(spots);
    }

    public Optional<ParkingSpot> getAvailableSpot(ParkingSpotType type) {
        return spots.stream()
                .filter(spot -> spot.isAvailable() && spot.getSpotType() == type)
                .findFirst();
    }

    public String getFloorId() {
        return floorId;
    }
}

class ParkingLot {
    private static ParkingLot instance;

    private final List<ParkingFloor> floors;
    private final FeeCalculator feeCalculator = new FeeCalculator();

    private ParkingLot(List<ParkingFloor> floors) {
        this.floors = List.copyOf(floors);
    }

    /**
     * Note the classic singleton trap: the floors argument is only honoured on
     * the very first call. Later callers silently get the original lot.
     */
    public static synchronized ParkingLot getInstance(List<ParkingFloor> floors) {
        if (instance == null) {
            instance = new ParkingLot(floors);
        }
        return instance;
    }

    private ParkingSpotType spotTypeFor(VehicleType type) {
        return switch (type) {
            case BIKE -> ParkingSpotType.SMALL;
            case CAR -> ParkingSpotType.MEDIUM;
            case TRUCK -> ParkingSpotType.LARGE;
        };
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        return parkVehicle(vehicle, LocalDateTime.now());
    }

    /** Find-and-claim must be one atomic step, or two arrivals can race onto one spot. */
    public synchronized Ticket parkVehicle(Vehicle vehicle, LocalDateTime entryTime) {
        ParkingSpotType required = spotTypeFor(vehicle.getType());
        for (ParkingFloor floor : floors) {
            Optional<ParkingSpot> freeSpot = floor.getAvailableSpot(required);
            if (freeSpot.isPresent()) {
                ParkingSpot spot = freeSpot.get();
                spot.parkVehicle(vehicle);
                return new Ticket(UUID.randomUUID().toString(), vehicle, spot, entryTime);
            }
        }
        throw new NoSpotAvailableException(
                "no free " + required + " spot for " + vehicle.getPlateNo());
    }

    public double exitVehicle(Ticket ticket) {
        return exitVehicle(ticket, LocalDateTime.now());
    }

    /** Closes the ticket, then frees the spot so the next vehicle can use it. */
    public synchronized double exitVehicle(Ticket ticket, LocalDateTime exitTime) {
        ticket.close(exitTime, feeCalculator);
        ticket.getSpot().removeVehicle();
        return ticket.getAmount();
    }
}

class ParkingLotExample {

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Two floors: floor 1 has two car spots, floor 2 has one bike spot.
        ParkingFloor floor1 = new ParkingFloor("F1",
                List.of(new CarSpot("F1-C1"), new CarSpot("F1-C2")));
        ParkingFloor floor2 = new ParkingFloor("F2",
                List.of(new BikeSpot("F2-B1")));
        ParkingLot lot = ParkingLot.getInstance(List.of(floor1, floor2));

        // Fixed clock, so the printed fees are deterministic.
        LocalDateTime entry = LocalDateTime.of(2024, 1, 1, 10, 0);

        // Case 1: typical - a car takes the first free MEDIUM spot on floor 1.
        Ticket carTicket = lot.parkVehicle(new Car("TS09AB1234"), entry);
        print("case 1  car spot", carTicket.getSpot().getId(), "F1-C1");

        // Case 2: a bike needs SMALL, so the lot skips floor 1 entirely.
        Ticket bikeTicket = lot.parkVehicle(new Bike("TS09XY0007"), entry);
        print("case 2  bike spot", bikeTicket.getSpot().getId(), "F2-B1");

        // Case 3: fees. Three hours bills 3 * 10; ten minutes hits the minimum.
        print("case 3a car fee 3h", lot.exitVehicle(carTicket, entry.plusHours(3)), 30.0);
        print("case 3b bike fee 10m", lot.exitVehicle(bikeTicket, entry.plusMinutes(10)), 20.0);
        print("case 3c F1-C1 freed", carTicket.getSpot().isAvailable(), true);

        // Case 4: edge - fill both car spots, then a third car must be refused.
        lot.parkVehicle(new Car("CAR-A"), entry);
        lot.parkVehicle(new Car("CAR-B"), entry);
        String outcome;
        try {
            lot.parkVehicle(new Car("CAR-C"), entry);
            outcome = "parked (wrong)";
        } catch (NoSpotAvailableException e) {
            outcome = "refused: " + e.getMessage();
        }
        print("case 4  lot full", outcome, "refused: no free MEDIUM spot for CAR-C");
    }
}

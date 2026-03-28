import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleRentalAppTest {
    @Test
    public void testLicensePlate() {
        Vehicle vehicle1 = new Car("Toyota", "Corolla", 2019, 4);
        Vehicle vehicle2 = new Minibus("Honda", "Civic", 2021, true);
        Vehicle vehicle3 = new PickupTruck("Ford", "Focus", 2024, 10, true);
        Vehicle vehicle4 = new Car("Toyota", "Corolla", 2019, 4);

        assertDoesNotThrow(() -> {
            vehicle1.setLicensePlate("AAA100");
        });
        assertDoesNotThrow(() -> {
            vehicle2.setLicensePlate("ABC567");
        });
        assertDoesNotThrow(() -> {
            vehicle3.setLicensePlate("ZZZ999");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            vehicle1.setLicensePlate("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            vehicle2.setLicensePlate(null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            vehicle3.setLicensePlate("AAA1000");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            vehicle4.setLicensePlate("ZZZ99");
        });
    }
}
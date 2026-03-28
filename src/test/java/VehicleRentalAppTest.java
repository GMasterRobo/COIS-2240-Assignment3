import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

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
    
    @Test
    public void testRentAndReturnVehicle() {
		// Initializes the single RentalSystem object, as well as the vehicle and customer objects.
		RentalSystem testSystem = RentalSystem.getInstance();
		Vehicle testVehicle = new Car("Porsche", "911", 2023, 4);
		Customer testCustomer = new Customer(2342532, "Joey");
		// Tests whether the objects initialize with expected properties, and get added to the system correctly
		assertEquals(Vehicle.VehicleStatus.Available, testVehicle.getStatus());
		assertTrue(testSystem.addVehicle(testVehicle));
		assertTrue(testSystem.addCustomer(testCustomer));
		LocalDate testDate = LocalDate.of(2025, 11, 29);
		boolean rentResult = testSystem.rentVehicle(testVehicle, testCustomer, testDate, 1000);
		assertTrue(rentResult);
		assertEquals(Vehicle.VehicleStatus.Rented, testVehicle.getStatus());
		rentResult = testSystem.rentVehicle(testVehicle, testCustomer, testDate, 1000);
		assertFalse(rentResult);
		boolean returnResult = testSystem.returnVehicle(testVehicle, testCustomer, testDate, 500);
		assertTrue(returnResult);
		assertEquals(Vehicle.VehicleStatus.Available, testVehicle.getStatus());
		returnResult = testSystem.returnVehicle(testVehicle, testCustomer, testDate, 500);
		assertFalse(returnResult);
	}
}
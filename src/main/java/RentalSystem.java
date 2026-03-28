import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

public class RentalSystem {
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    private static RentalSystem system;

    private RentalSystem() {

    }

    public static RentalSystem getInstance() {
        if (system == null) {
            system = new RentalSystem();
        }
        return system;
    }

    public boolean addVehicle(Vehicle vehicle) {
    	// Add check to ensure that a car with this license plate doesn't already exist in the system
    	if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
    		System.out.print("A vehicle with this license plate already exists!");
    		return false;
    	}
    	else {
	        saveVehicle(vehicle, true);
	        vehicles.add(vehicle);
	        return true;
    	}
    }

    public boolean addCustomer(Customer customer) {
    	// Add check to ensure that a customer with this customer id doesn't already exist in the system
    	if (findCustomerById(customer.getCustomerId()) != null) {
    		System.out.print("A customer with this ID number already exists!");
    		return false;
    	}
    	else {
	        saveCustomer(customer);
	        customers.add(customer);
	        return true;
    	}
    }
    public void loadData(){
    	try (BufferedReader reader = new BufferedReader(new FileReader("vehicles.txt"))) {      
			// Reads each line from the vehicles.txt file, and uses csv handling to convert it to vehicle objects.
    		String line;
			while ((line = reader.readLine()) != null) {
	    	String [] parts = line.split(",");
	    	// Sets the status of the vehicle by convert the text value into an enum
	    	String licensePlate = parts[1].trim();
	    	String make = parts[2].trim();
	    	String model = parts[3].trim();
	    	int year = Integer.parseInt(parts[4].trim());
	    	Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(parts[5].trim());
	    	// Setting the vehicle initially as null for compiler reasons.
	    	Vehicle vehicle = null;
	    	// This switch statements differentiates between the different types of vehicles, and creates a specific object of that class.
	    	switch(parts[0].trim()) {
	    	case "SportCar":{
	    		int numSeats = Integer.parseInt(parts[6].trim());
	    		int horsePower = Integer.parseInt(parts[7].trim());
	    		Boolean hasTurbo = Boolean.parseBoolean(parts[8].trim());
	    		vehicle = new SportCar(make, model, year, numSeats, horsePower, hasTurbo);
	    		break;
	    	}
	    	case "Car":{
	    		int numSeats = Integer.parseInt(parts[6].trim());
	    		vehicle = new Car(make, model, year, numSeats);
	    		break;
	    	}
	    	case "Minibus":{
	    		Boolean isAccessible = Boolean.parseBoolean(parts[6].trim());
	    		vehicle = new Minibus(make, model, year, isAccessible);
	    		break;
	    	}
	    	case "PickupTruck":{
	    		double cargoSize = Double.parseDouble(parts[6].trim());
	    		Boolean hasTrailer = Boolean.parseBoolean(parts[7].trim());
	    		vehicle = new PickupTruck(make, model, year, cargoSize, hasTrailer);
	    		break;
	    	}
	    }
		    	if (vehicle != null) {
			    	vehicle.setLicensePlate(licensePlate);
			    	vehicle.setStatus(status);
		    		vehicles.add(vehicle);
		    	}
			}
		} catch (IOException e) {
			System.out.print("Error reading file");
		}
    	try (BufferedReader reader = new BufferedReader(new FileReader("customers.txt"))) {      
			// Reads each line from the customers.txt file, and uses csv handling to convert it to customer objects, and then add them to the customers list.
    		String line;
			while ((line = reader.readLine()) != null) {
	    	String [] parts = line.split(",");
	    	// Sets the status of the vehicle by convert the text value into an enum
	        Customer customer = new Customer(Integer.parseInt(parts[0].trim()), parts[1].trim()); 
    		customers.add(customer);
			}
		} catch (IOException e) {
			System.out.print("Error reading file");
    	}
    	try (BufferedReader reader = new BufferedReader(new FileReader("rental_records.txt"))) {      
			// Reads each line from the customers.txt file, and uses csv handling to convert it to customer objects, and then add them to the customers list.
			String line;
			while ((line = reader.readLine()) != null) {
	    	String [] parts = line.split(",");
	    	// Finds the car and the customer associated with a specific record through the saved identification information
	    	Vehicle vehicle = findVehicleByPlate(parts[0].trim());
	    	Customer customer = findCustomerById(Integer.parseInt(parts[1].trim()));
	    	LocalDate date = LocalDate.parse(parts[2].trim());
	    	double totalAmount = Double.parseDouble(parts[3].trim());
	    	String recordType = parts[4].trim();
	    	if (vehicle != null && customer != null) {
		        RentalRecord rentalRecord = new RentalRecord(vehicle, customer, date, totalAmount, recordType); 
	    		rentalHistory.addRecord(rentalRecord);
				}
	    	else {
	    		System.out.print("Error: Couldn't find Vehicle or Customer Information.");
	    		}
			}
		} catch (IOException e) {
			System.out.print("Error reading file");
		}
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        boolean first = true;

        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());

            saveRecord(record);

            for(Vehicle v : vehicles) {
                if (first) {
                    saveVehicle(v, false);
                    first = false;
                } else {
                    saveVehicle(v, true);
                }
            }


        }
        else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        boolean first = true;

        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());

            saveRecord(record);

            for(Vehicle v : vehicles) {
                if (first) {
                    saveVehicle(v, false);
                    first = false;
                } else {
                    saveVehicle(v, true);
                }
            }
        }
        else {
            System.out.println("Vehicle is not rented.");
        }
    }    

    public void displayVehicles(Vehicle.VehicleStatus status) {
        // Display appropriate title based on status
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        // Header with proper column widths
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            // Header with proper column widths
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }

    public void saveVehicle(Vehicle vehicle, boolean append) {
        try {
            FileWriter writer = new FileWriter("vehicles.txt", append);
            if (vehicle instanceof SportCar) {
                SportCar v = (SportCar) vehicle;
                writer.write("SportCar," + v.getLicensePlate() + "," + v.getMake() + "," +
                        v.getModel() + "," + v.getYear() + "," +v.getStatus() +
                        "," + v.getNumSeats() + "," + v.getHorsepower() + "," + v.hasTurbo() + "\n");

            } else if (vehicle instanceof Car) {
                Car v = (Car) vehicle;
                writer.write("Car," + v.getLicensePlate() + "," + v.getMake() + "," +
                        v.getModel() + "," + v.getYear() + "," +v.getStatus() +
                        "," + v.getNumSeats() + "\n");

            } else if (vehicle instanceof Minibus) {
                Minibus v = (Minibus) vehicle;
                writer.write("Minibus," + v.getLicensePlate() + "," + v.getMake() + "," +
                        v.getModel() + "," + v.getYear() + "," +v.getStatus() +
                        "," + v.isAccessible() + "\n");

            } else if (vehicle instanceof PickupTruck) {
                PickupTruck v = (PickupTruck) vehicle;
                writer.write("PickupTruck," + v.getLicensePlate() + "," + v.getMake() + "," +
                        v.getModel() + "," + v.getYear() + "," +v.getStatus() +
                        "," + v.getCargoSize() + "," + v.hasTrailer() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCustomer(Customer customer) {
        try {
            FileWriter writer = new FileWriter("customers.txt", true);
            writer.write(customer.getCustomerId() + "," + customer.getCustomerName() + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveRecord(RentalRecord record) {
        try {
            FileWriter writer = new FileWriter("rental_records.txt", true);
            writer.write( record.getVehicle().getLicensePlate() + ","
                    + record.getCustomer().getCustomerId() + ","
                    + record.getRecordDate() + "," + record.getTotalAmount() + ","
                    + record.getRecordType() + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
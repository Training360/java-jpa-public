package jpa;

import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.Assert.assertEquals;

public class ParkingPlaceDaoTest {

    private ParkingPlaceDao parkingPlaceDao;

    private EmployeeDao employeeDao;

    @Before
    public void init() {
        EntityManagerFactory factory = Persistence
                .createEntityManagerFactory("pu");
        parkingPlaceDao = new ParkingPlaceDao(factory);
        employeeDao = new EmployeeDao(factory);
    }

    @Test
    public void testSave() {
        parkingPlaceDao.saveParkingPlace(new ParkingPlace(100));
        ParkingPlace parkingPlace = parkingPlaceDao.findParkingPlaceNumber(100);
        assertEquals(100, parkingPlace.getNumber());
    }

    @Test
    public void testSaveEmployeeWithParkingPlace() {
        ParkingPlace parkingPlace = new ParkingPlace(100);
        parkingPlaceDao.saveParkingPlace(parkingPlace);

        Employee employee = new Employee("John Doe");
        employee.setParkingPlace(parkingPlace);
        employeeDao.saveEmployee(employee);

        Employee anotherEmployee = employeeDao.findEmployeeById(employee.getId());
        assertEquals(100, anotherEmployee.getParkingPlace().getNumber());
    }
}

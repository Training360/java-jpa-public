package employees;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeDaoTest {

    EmployeeDao employeeDao;

    ParkingPlaceDao parkingPlaceDao;

    @BeforeEach
    void init() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pu");
        employeeDao = new EmployeeDao(entityManagerFactory);
        parkingPlaceDao = new ParkingPlaceDao(entityManagerFactory);
    }

    @Test
    void testSaveThenFindById() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);

        long id = employee.getId();
        Employee another = employeeDao.findEmployeeById(id);
//        Employee another = employeeDao.findEmployeeById(employee.getDepName(), id);

        assertEquals("John Doe", another.getName());
    }

    @Test
    void testSaveThenListAll() {
        employeeDao.saveEmployee(new Employee("John Doe"));
        employeeDao.saveEmployee(new Employee("Jane Doe"));
//        employeeDao.save(new Employee("x", 1L, "John Doe"));
//        employeeDao.save(new Employee("x", 2L, "Jane Doe"));

        List<Employee> employees = employeeDao.listEmployees();
        assertEquals(List.of("Jane Doe", "John Doe"),
                employees.stream().map(Employee::getName).collect(Collectors.toList()));
    }

    @Test
    void testChangeName() {
        Employee employee = new Employee("John Doe");
//        Employee employee = new Employee("x", 1L, "John Doe");
        employeeDao.saveEmployee(employee);

        long id = employee.getId();

        employeeDao.updateEmployeeName(id, "Jack Doe");
//        employeeDao.updateEmployeeName("x", 1L, "Jack Doe");
        Employee another = employeeDao.findEmployeeById(id);
//        Employee modifiedEmployee = employeeDao.findEmployeeById("x", 1L);

        assertEquals("Jack Doe", another.getName());
//        assertEquals("Jack Doe", modifiedEmployee.getName());
    }

    @Test
    void testDelete() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);
        long id = employee.getId();
        employeeDao.deleteEmployee(id);

        List<Employee> employees = employeeDao.listEmployees();
        assertTrue(employees.isEmpty());
    }

    @Test
    void testIllegalId() {
        Employee employee = employeeDao.findEmployeeById(12L);
//        Employee employee = employeeDao.findEmployeeById("x", 12L);

        assertEquals(null, employee);
    }

    @Test
    void testEmployeeWithAttributes() {
//        employeeDao.save(new Employee("John Doe", Employee.EmployeeType.HALF_TIME,
//                LocalDate.of(2000, 1, 1)));
        for (int i = 0; i < 10; i++) {
            employeeDao.saveEmployee(new Employee("John Doe", Employee.EmployeeType.HALF_TIME,
                    LocalDate.of(2000, 1, 1)));
        }

        Employee employee = employeeDao.listEmployees().get(0);
        assertEquals(LocalDate.of(2000, 1, 1), employee.getDateOfBirth());
    }

    @Test
    void testSaveEmployeeChangeState() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);

        employee.setName("Jack Doe");
        Employee modifiedEmployee = employeeDao.findEmployeeById(employee.getId());

        assertEquals("John Doe", modifiedEmployee.getName());
        assertFalse(employee == modifiedEmployee);
    }

    @Test
    void testMerge() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);
        employee.setName("Jack Doe");
        employeeDao.updateEmployee(employee);

        Employee modifiedEmployee = employeeDao.findEmployeeById(employee.getId());
        assertEquals("Jack Doe", modifiedEmployee.getName());
    }

    @Test
    public void testFlush() {
        for (int i =0; i < 10; i++) {
            employeeDao.saveEmployee(new Employee("John Doe" + i));
        }
        employeeDao.updateEmployeeNames();
    }

    @Test
    public void testNicknames() {
        Employee employee = new Employee("John Doe");
        employee.setNicknames(Set.of("Johnny", "J"));
        employeeDao.saveEmployee(employee);

//        Employee anotherEmployee = employeeDao.findById(employee.getId());
//        System.out.println(anotherEmployee.getNicknames());

        Employee anotherEmployee = employeeDao.findEmployeeByIdWithNicknames(employee.getId());
        assertEquals(Set.of("J", "Johnny"), anotherEmployee.getNicknames());
    }

    @Test
    void testVacations() {
        Employee employee = new Employee("John Doe");
        employee.setVacationBookings(Set.of(new VacationEntry(LocalDate.of(2018, 1, 1), 4),
                new VacationEntry(LocalDate.of(2018, 2, 15), 2)));
        employeeDao.saveEmployee(employee);

        Employee anotherEmployee = employeeDao.findEmployeeByIdWithVacations(employee.getId());
        assertEquals(2, anotherEmployee.getVacationBookings().size());
    }

//    @Test
//    void testPhoneNumbers() {
//        Employee employee = new Employee("John Doe");
//        employee.setPhoneNumbers(Map.of("home", "1234", "work", "4321"));
//
//        employeeDao.saveEmployee(employee);
//        Employee anotherEmployee = employeeDao.findEmployeeByIdWithPhoneNumbers(employee.getId());
//
//        assertEquals("1234", anotherEmployee.getPhoneNumbers().get("home"));
//        assertEquals("4321", anotherEmployee.getPhoneNumbers().get("work"));
//    }

    @Test
    void testPhoneNumbers() {
        PhoneNumber phoneNumberHome = new PhoneNumber("home", "1234");
        PhoneNumber phoneNumberWork = new PhoneNumber("work", "4321");

        Employee employee = new Employee("John Doe");
//        employee.setPhoneNumbers(Set.of(phoneNumberHome, phoneNumberWork));
        employee.addPhoneNumber(phoneNumberWork);
        employee.addPhoneNumber(phoneNumberHome);
        employeeDao.saveEmployee(employee);

        Employee anotherEmployee = employeeDao.findEmployeeByIdWithPhoneNumbers(employee.getId());
        assertEquals(2, anotherEmployee.getPhoneNumbers().size());
//        assertEquals("home", anotherEmployee.getPhoneNumbers().get(0).getType());
        assertEquals("work", anotherEmployee.getPhoneNumbers().get(0).getType());
    }

    @Test
    void testAddPhoneNumber() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);

        employeeDao.addPhoneNumber(employee.getId(), new PhoneNumber("home", "1111"));
        Employee anotherEmployee = employeeDao.findEmployeeByIdWithPhoneNumbers(employee.getId());

        assertEquals(1, anotherEmployee.getPhoneNumbers().size());
    }

    @Test
    void testRemove() {
        Employee employee = new Employee("John Doe");
        employee.addPhoneNumber(new PhoneNumber("home", "1111"));
        employee.addPhoneNumber(new PhoneNumber("work", "2222"));
        employeeDao.saveEmployee(employee);

        employeeDao.deleteEmployee(employee.getId());
    }

//    @Test
//    void testEmployeeWithAddress() {
//        Employee employee = new Employee("John Doe");
//        Address address = new Address("H-1301", "Budapest", "Fő utca");
//        employee.setAddress(address);
//        employeeDao.saveEmployee(employee);
//
//        Employee anotherEmployee = employeeDao.findEmployeeById(employee.getId());
//        assertEquals("H-1301", anotherEmployee.getAddress().getZip());
//    }

    @Test
    void testEmployeeWithAddressAttributes() {
        Employee employee = new Employee("John Doe");
        employee.setZip("H-1301");
        employee.setCity("Budapest");
        employee.setLine1("Fő utca");
        employeeDao.saveEmployee(employee);

        Employee anotherEmployee = employeeDao.findEmployeeById(employee.getId());
        assertEquals("H-1301", anotherEmployee.getZip());
    }

    @Test
    void testFindEmloyeeByName() {
        employeeDao.saveEmployee(new Employee("John Doe"));

        Employee employee = employeeDao.findEmployeeByName("John Doe");
        assertEquals("John Doe", employee.getName());
    }

    @Test
    void testPaging() {
        for (int i = 100; i < 300; i++) {
            Employee employee = new Employee("John Doe " + i);
            employeeDao.saveEmployee(employee);
        }

        List<Employee> employees = employeeDao.listEmployees(50, 20);
        assertEquals("John Doe 150", employees.get(0).getName());
        assertEquals(20, employees.size());
    }

    @Test
    void testFindNumber() {
        Employee employee = new Employee("John Doe");
        ParkingPlace parkingPlace = new ParkingPlace(101);
        parkingPlaceDao.saveParkingPlace(parkingPlace);
        employee.setParkingPlace(parkingPlace);
        employeeDao.saveEmployee(employee);
        int number = employeeDao.findParkinPlaceNumberByEmployeeName("John Doe");
        assertEquals(101, number);
    }

    @Test
    void testBaseData() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);

        List<Object[]> data = employeeDao.listEmployeeBaseData();
        assertEquals(1, data.size());
        assertEquals("John Doe", data.get(0)[1]);
    }

    @Test
    void testDto() {
        employeeDao.saveEmployee(new Employee("John Doe"));
        employeeDao.saveEmployee(new Employee("Jane Doe"));

        List<EmpBaseDataDto> data = employeeDao.listEmployeesDto();

        assertEquals(2, data.size());
        assertEquals("Jane Doe", data.get(0).getName());
    }

    @Test
    void testUpdateToType() {
        employeeDao.saveEmployee(new Employee("John Doe", Employee.EmployeeType.FULL_TIME, LocalDate.of(1980, 1, 1)));
        employeeDao.saveEmployee(new Employee("Jack Doe", Employee.EmployeeType.FULL_TIME, LocalDate.of(1990, 1, 1)));
        employeeDao.saveEmployee(new Employee("Jane Doe", Employee.EmployeeType.FULL_TIME, LocalDate.of(2000, 1, 1)));

        employeeDao.updateToType(LocalDate.of(1985, 1, 1), Employee.EmployeeType.HALF_TIME);

        List<Employee> employees = employeeDao.listEmployees();

        assertEquals(Employee.EmployeeType.HALF_TIME, employees.get(0).getEmployeeType());
        assertEquals(Employee.EmployeeType.FULL_TIME, employees.get(2).getEmployeeType());
    }

    @Test
    void testDeleteWithoutType() {
        employeeDao.saveEmployee(new Employee("John Doe", Employee.EmployeeType.FULL_TIME, LocalDate.of(1980, 1, 1)));
        employeeDao.saveEmployee(new Employee("Jack Doe", null, LocalDate.of(1990, 1, 1)));
        employeeDao.saveEmployee(new Employee("Jane Doe", Employee.EmployeeType.FULL_TIME, LocalDate.of(2000, 1, 1)));

        employeeDao.deleteWithoutType();

        List<Employee> employees = employeeDao.listEmployees();

        assertEquals(2, employees.size());
    }
}
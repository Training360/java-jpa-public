package jpa;

import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EmployeeDaoTest {

    private EmployeeDao employeeDao;

    @Before
    public void init() {
        EntityManagerFactory factory = Persistence
                .createEntityManagerFactory("pu");
        employeeDao = new EmployeeDao(factory);
    }

    @Test
    public void testCreateAndList() {
        employeeDao.saveEmployee(new Employee("John Doe"));
        employeeDao.saveEmployee(new Employee("Jane Doe"));

        List<Employee> employees = employeeDao.listEmployees();
        List<String> names = employees.stream().map(Employee::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("Jane Doe", "John Doe"), names);
    }

    @Test
    public void testUpdateName() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);

        employeeDao.updateEmployeeName(employee.getId(), "Jack Doe");

        Employee modifiedEmployee = employeeDao.findEmployeeById(employee.getId());

        assertEquals("Jack Doe", modifiedEmployee.getName());
    }

    @Test
    public void testDelete() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);
        employeeDao.saveEmployee(new Employee("Jane Doe"));

        employeeDao.deleteEmployee(employee.getId());

        List<Employee> employees = employeeDao.listEmployees();
        List<String> names = employees.stream().map(Employee::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("Jane Doe"), names);
    }

    @Test
    public void testIllegalId() {
        Employee employee = employeeDao.findEmployeeById(12L);
        assertEquals(null, employee);
    }

    @Test
    public void testEmployeeWithAttributes() {
        for (int i = 0; i < 10; i++) {
            employeeDao.saveEmployee(new Employee("John Doe", Employee.EmployeeType.HALF_TIME,
                    LocalDate.of(2000, 1, 1)));
        }
        Employee employee = employeeDao.listEmployees().get(0);
        assertEquals(LocalDate.of(2000, 1, 1), employee.getDateOfBirth());
    }

    @Test
    public void testSaveEmployeeChangeState() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);

        employee.setName("Jack Doe");
        Employee modifiedEmployee = employeeDao.findEmployeeById(employee.getId());

        assertEquals("John Doe", modifiedEmployee.getName());
        assertFalse(employee == modifiedEmployee);
    }

    @Test
    public void testMerge() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);

        employee.setName("Jack Doe");
        employeeDao.updateEmployee(employee);

        Employee modifiedEmployee = employeeDao.findEmployeeById(employee.getId());
        assertEquals("Jack Doe", modifiedEmployee.getName());
    }

    @Test
    public void testFlush() {
        for (int i = 0; i < 10; i++) {
            employeeDao.saveEmployee(new Employee("John Doe" + i));
        }

        employeeDao.updateEmployeeNames();
    }

    @Test
    public void testNicknames() {
        Employee employee = new Employee("John Doe");
        employee.setNicknames(Set.of("Johnny", "J"));
        employeeDao.saveEmployee(employee);

        Employee anotherEmployee = employeeDao.findEmployeeByIdWithNicknames(employee.getId());
        assertEquals(Set.of("J", "Johnny"), anotherEmployee.getNicknames());
    }

    @Test
    public void testVacations() {
        Employee employee = new Employee("John Doe");
        employee.setVacationBookings(Set.of(new VacationEntry(LocalDate.of(2018, 1, 1), 4),
                new VacationEntry(LocalDate.of(2018, 2, 15), 2)));
        employeeDao.saveEmployee(employee);

        Employee anotherEmployee = employeeDao.findEmployeeByIdWithVacations(employee.getId());
        assertEquals(2, anotherEmployee.getVacationBookings().size());
    }


    @Test
    public void testPhoneNumber() {
        PhoneNumber phoneNumberHome = new PhoneNumber("home", "1234");
        PhoneNumber phoneNumberWork = new PhoneNumber("work", "4321");

        Employee employee = new Employee("John Doe");
        employee.addPhoneNumber(phoneNumberWork);
        employee.addPhoneNumber(phoneNumberHome);
        employeeDao.saveEmployee(employee);

        Employee anotherEmployee = employeeDao.findEmployeeByIdWithPhoneNumbers(employee.getId());
        assertEquals(2, anotherEmployee.getPhoneNumbers().size());
        assertEquals("work", anotherEmployee.getPhoneNumbers().get(0).getType());
    }

    @Test
    public void testAddPhoneNumber() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);

        employeeDao.addPhoneNumber(employee.getId(), new PhoneNumber("home", "1111"));

        Employee anotherEmployee = employeeDao.findEmployeeByIdWithPhoneNumbers(employee.getId());
        assertEquals(1, anotherEmployee.getPhoneNumbers().size());
    }

    @Test
    public void testRemove() {
        Employee employee = new Employee("John Doe");
        employee.addPhoneNumber(new PhoneNumber("home", "1111"));
        employee.addPhoneNumber(new PhoneNumber("work", "2222"));
        employeeDao.saveEmployee(employee);

        employeeDao.deleteEmployee(employee.getId());
    }

    //    @Test
//    public void testEmployeeWithAddress() {
//        Employee employee = new Employee("John Doe");
//        Address address = new Address("H-1301", "Budapest", "Fo utca");
//        employee.setAddress(address);
//        employeeDao.saveEmployee(employee);
//
//        Employee anotherEmployee = employeeDao.findEmployeeById(employee.getId());
//        assertEquals("H-1301", anotherEmployee.getAddress().getZip());
//    }

    @Test
    public void testEmployeeWithAddressAttributes() {
        Employee employee = new Employee("John Doe");
        employee.setZip("H-1301");
        employee.setCity("Budapest");
        employee.setLine1("Fo utca");
        employeeDao.saveEmployee(employee);

        Employee anotherEmployee = employeeDao.findEmployeeById(employee.getId());
        assertEquals("H-1301", anotherEmployee.getZip());
    }

}

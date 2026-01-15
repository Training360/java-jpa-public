package jpa;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeDaoTest {

    EmployeeDao employeeDao;

    @BeforeEach
    void init() {
        EntityManagerFactory factory = Persistence
                .createEntityManagerFactory("pu");
        employeeDao = new EmployeeDao(factory);
    }

    @Test
    void testCreateAndList() {
        employeeDao.saveEmployee(new Employee("x", 1L, "John Doe"));
        employeeDao.saveEmployee(new Employee("x", 2L, "Jane Doe"));

        List<Employee> employees = employeeDao.listEmployees();
        List<String> names = employees.stream().map(Employee::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("Jane Doe", "John Doe"), names);
    }

    @Test
    void testUpdateName() {
        Employee employee = new Employee("x", 1L,"John Doe");
        employeeDao.saveEmployee(employee);

        employeeDao.updateEmployeeName(employee.getId(), "Jack Doe");

        Employee modifiedEmployee = employeeDao.findEmployeeById(employee.getId());

        assertEquals("Jack Doe", modifiedEmployee.getName());
    }

    @Test
    void testDelete() {
        Employee employee = new Employee("x", 1L, "John Doe");
        employeeDao.saveEmployee(employee);
        employeeDao.saveEmployee(new Employee("x", 2L, "Jane Doe"));

        employeeDao.deleteEmployee(employee.getId());

        List<Employee> employees = employeeDao.listEmployees();
        List<String> names = employees.stream().map(Employee::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("Jane Doe"), names);
    }

    @Test
    void testIllegalId() {
        Employee employee = employeeDao.findEmployeeById(new EmployeeId("x", 12L));
        assertEquals(null, employee);
    }

    @Test
    void testEmployeeWithAttributes() {
        for (int i = 0; i < 10; i++) {
            employeeDao.saveEmployee(new Employee("x", (long) i, "John Doe"));
        }
        Employee employee = employeeDao.listEmployees().get(0);
        assertEquals(LocalDate.of(2000, 1, 1), employee.getDateOfBirth());
    }

}

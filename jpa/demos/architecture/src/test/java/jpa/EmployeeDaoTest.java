package jpa;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class EmployeeDaoTest {

    private EmployeeDao employeeDao;

    @Before
    public void init() {
        EntityManagerFactory factory = Persistence
                .createEntityManagerFactory("pu");
        employeeDao = new EmployeeDao(factory);

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://localhost/employees");
        dataSource.setUser("employees");
        dataSource.setPassword("employees");

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void testCreateAndList() {
        employeeDao.save(new Employee("John Doe"));
        employeeDao.save(new Employee("Jane Doe"));

        List<Employee> employees = employeeDao.listAll();
        List<String> names = employees.stream().map(Employee::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("Jane Doe", "John Doe"), names);
    }

    @Test
    public void testUpdateName() {
        Employee employee = new Employee("John Doe");
        employeeDao.save(employee);

        employeeDao.changeName(employee.getId(), "Jack Doe");

        Employee modifiedEmployee = employeeDao.findById(employee.getId());

        assertEquals("Jack Doe", modifiedEmployee.getName());
    }

    @Test
    public void testDelete() {
        Employee employee = new Employee("John Doe");
        employeeDao.save(employee);
        employeeDao.save(new Employee("Jane Doe"));

        employeeDao.delete(employee.getId());

        List<Employee> employees = employeeDao.listAll();
        List<String> names = employees.stream().map(Employee::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("Jane Doe"), names);
    }

    @Test
    public void testIllegalId() {
        Employee employee = employeeDao.findById(12L);
        assertEquals(null, employee);
    }

}

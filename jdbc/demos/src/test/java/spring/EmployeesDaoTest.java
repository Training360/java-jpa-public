package spring;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeesDaoTest {

    EmployeesDao employeesDao;

    @BeforeEach
    void init() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/employees?useUnicode=true");
        dataSource.setUser("employees");
        dataSource.setPassword("employees");

        Flyway flyway = Flyway.configure().dataSource(dataSource).load();

        flyway.clean();
        flyway.migrate();

        employeesDao = new EmployeesDao(dataSource);
    }

    @Test
    void testCreateThanList() {
        employeesDao.createEmployee("John Doe");
        List<String> employees = employeesDao.listEmployeeNames();

        assertEquals(Arrays.asList("John Doe"), employees);
    }

    @Test
    void testThanFind() {
        long id = employeesDao.createEmployee("John Doe");
        System.out.println(id);
        String name = employeesDao.findEmployeeNameById(id);
        assertEquals("John Doe", name);
    }
}

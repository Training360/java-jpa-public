package advancedrs;

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
    void testOddNames() {
        employeesDao.createEmployees(Arrays.asList("Jack Doe", "Jane Doe", "Joe Doe"));
        List<String> names = employeesDao.listOddEmployeeNames();

        assertEquals(Arrays.asList("Jack Doe", "Joe Doe"), names);
    }

    @Test
    void testUpdateNames() {
        employeesDao.createEmployees(Arrays.asList("Jack Doe", "Jane Doe", "Joe Doe"));
        employeesDao.updateNames();
        List<String> names = employeesDao.listEmployeeNames();
        assertEquals(Arrays.asList("Mr. Jack Doe", "Jane Doe", "Mr. Joe Doe"),
                names);
    }
}

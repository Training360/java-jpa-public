package generatedid;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void testById() {
        long id = employeesDao.createEmployee("Jack Doe");
        System.out.println(id);
        id = employeesDao.createEmployee("Jane Doe");
        System.out.println(id);
        String name = employeesDao.findEmployeeNameById(id);
        assertEquals("Jane Doe", name);
    }

}

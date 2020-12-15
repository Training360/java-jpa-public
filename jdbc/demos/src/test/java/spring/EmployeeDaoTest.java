package spring;

import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class EmployeeDaoTest {

    @Autowired
    private Flyway flyway;

    @Autowired
    private EmployeeDao employeeDao;

    @Before
    public void init() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void testCreateThanList() {
        employeeDao.createEmployee("John Doe");
        List<String> employees = employeeDao.listEmployeeNames();

        assertEquals(Arrays.asList("John Doe"), employees);
    }

    @Test
    public void testThanFind() {
        long id = employeeDao.createEmployee("John Doe");
        System.out.println(id);
        String name = employeeDao.findEmployeeNameById(id);
        assertEquals("John Doe", name);
    }
}

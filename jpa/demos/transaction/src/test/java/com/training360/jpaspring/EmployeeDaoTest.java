package com.training360.jpaspring;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(statements = {"delete from employees", "delete from log_entry"})
public class EmployeeDaoTest {

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private LogEntryDao logEntryDao;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testSaveThenFind() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);

        long id = employee.getId();

        assertEquals("John Doe", employeeDao.findEmployeeById(id).getName());
    }

    @Test
    public void testSaveWithEmptyName() {
        expectedException.expect(IllegalArgumentException.class);
        Employee employee = new Employee("");
        employeeDao.saveEmployee(employee);
    }

    @Test
    public void testSaveLog() {
        LogEntry logEntry = new LogEntry("Begin");
        logEntryDao.save(logEntry);
        assertEquals(1, logEntryDao.list().size());
    }
}

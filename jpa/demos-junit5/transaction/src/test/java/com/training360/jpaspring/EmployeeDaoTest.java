package com.training360.jpaspring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Sql(statements = {"delete from employees", "delete from log_entry"})
class EmployeeDaoTest {

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    LogEntryDao logEntryDao;

    @Test
    void testSaveThenFind() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);

        long id = employee.getId();

        assertEquals("John Doe", employeeDao.findEmployeeById(id).getName());
    }

    @Test
    void testSaveWithEmptyName() {
        Employee employee = new Employee("");
        assertThrows(IllegalArgumentException.class, () ->employeeDao.saveEmployee(employee));
    }

    @Test
    void testSaveLog() {
        LogEntry logEntry = new LogEntry("Begin");
        logEntryDao.save(logEntry);
        assertEquals(1, logEntryDao.list().size());
    }
}

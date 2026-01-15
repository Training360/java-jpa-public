package com.training360.jpaspring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Sql(statements = "delete from employees")
public class EmployeeDaoTest {

    @Autowired
    EmployeeDao employeeDao;

    @Test
    void saveAndFind() {
        Employee employee = new Employee("John Doe");
        employeeDao.saveEmployee(employee);

        Employee anotherEmployee = employeeDao
                .findEmployeeByName("John Doe");
        assertEquals("John Doe", anotherEmployee.getName());
    }
}

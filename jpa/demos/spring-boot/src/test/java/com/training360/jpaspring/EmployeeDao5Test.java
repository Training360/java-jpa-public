package com.training360.jpaspring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;

@SpringBootTest
public class EmployeeDao5Test {

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

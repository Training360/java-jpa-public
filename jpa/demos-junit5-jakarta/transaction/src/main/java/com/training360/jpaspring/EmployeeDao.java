package com.training360.jpaspring;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class EmployeeDao {

    @PersistenceContext
    private EntityManager entityManager;

    private LogEntryDao logEntryDao;

    public EmployeeDao(LogEntryDao logEntryDao) {
        this.logEntryDao = logEntryDao;
    }

    @Transactional
    public void saveEmployee(Employee employee) throws IllegalArgumentException {
        logEntryDao.save(new LogEntry("Create employee: " + employee.getName()));

        entityManager.persist(employee);

        if (employee.getName().length() < 3) {
            throw new IllegalArgumentException("Name must be longer then 3 characters");
        }
    }

    public Employee findEmployeeById(long id) {
        return entityManager.find(Employee.class, id);
    }
}

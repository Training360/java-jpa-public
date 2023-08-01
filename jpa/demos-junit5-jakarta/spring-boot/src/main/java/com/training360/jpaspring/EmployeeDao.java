package com.training360.jpaspring;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class EmployeeDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveEmployee(Employee employee) {
        entityManager.persist(employee);
    }

    public Employee findEmployeeByName(String name) {
        return entityManager.createQuery("select e from Employee e where e.name = :name",
                Employee.class)
                .setParameter("name", name)
                .getSingleResult();
    }
}

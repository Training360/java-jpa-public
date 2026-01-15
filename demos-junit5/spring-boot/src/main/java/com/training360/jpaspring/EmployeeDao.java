package com.training360.jpaspring;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

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

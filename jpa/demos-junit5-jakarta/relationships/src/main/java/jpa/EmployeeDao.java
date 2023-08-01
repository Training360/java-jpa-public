package jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class EmployeeDao {

    private EntityManagerFactory entityManagerFactory;

    public EmployeeDao(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void saveEmployee(Employee employee) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(employee);
        entityManager.getTransaction().commit();
        entityManager.close();
    }


    public List<Employee> listEmployees() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<Employee> employees = entityManager
                .createQuery("select e from Employee e order by e.name",
                        Employee.class)
                .getResultList();
        entityManager.close();
        return employees;
    }

    public Employee findEmployeeById(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Employee employee = entityManager.find(Employee.class, id);
        entityManager.close();
        return employee;
    }


    public void updateEmployeeName(long id, String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Employee employee = entityManager.find(Employee.class, id);
        employee.setName(name);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void deleteEmployee(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Employee employee = entityManager.find(Employee.class, id);
        entityManager.remove(employee);
        entityManager.getTransaction().commit();
    }

    public void updateEmployee(Employee employee) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(employee); // Visszatérési értéke a merged entitás
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void updateEmployeeNames() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<Employee> employees = entityManager
                .createQuery("select e from Employee e order by e.name",
                        Employee.class)
                .getResultList();

        entityManager.getTransaction().begin();

        for (Employee employee: employees) {
            employee.setName(employee.getName() + " ***");
            System.out.println("Modositva");
            entityManager.flush();
        }

        entityManager.getTransaction().commit();

        entityManager.close();
    }

    public Employee findEmployeeByIdWithNicknames(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Employee employee = entityManager
                .createQuery("select e from Employee e join fetch e.nicknames where e.id = :id",
                        Employee.class)
                .setParameter("id", id)
                .getSingleResult();
        entityManager.close();
        return employee;
    }

    public Employee findEmployeeByIdWithVacations(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Employee employee = entityManager
                .createQuery("select e from Employee e join fetch e.vacationBookings where id = :id",
                        Employee.class)
                .setParameter("id", id)
                .getSingleResult();
        entityManager.close();
        return employee;
    }

    public void addPhoneNumber(long id, PhoneNumber phoneNumber) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        // Employee employee = entityManager.find(Employee.class, id);
        Employee employee = entityManager.getReference(Employee.class, id);
        phoneNumber.setEmployee(employee);
        entityManager.persist(phoneNumber);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public Employee findEmployeeByIdWithPhoneNumbers(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Employee employee = entityManager.createQuery("select e from Employee e join fetch e.phoneNumbers where e.id = :id"
                ,Employee.class)
                .setParameter("id", id)
                .getSingleResult();
        entityManager.close();
        return employee;
    }



}

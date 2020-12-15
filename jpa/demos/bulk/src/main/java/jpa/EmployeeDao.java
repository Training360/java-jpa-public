package jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
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
                .createQuery("select e from Employee e join fetch e.nicknames where id = :id",
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

    public Employee findEmployeeByIdWithProjects(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Employee employee = entityManager.createQuery("select e from Employee e join fetch e.projects where e.id = :id"
                ,Employee.class)
                .setParameter("id", id)
                .getSingleResult();
        entityManager.close();
        return employee;
    }

    public Employee findEmployeeByName(String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> c = cb.createQuery(Employee.class);
        Root<Employee> emp = c.from(Employee.class);
        c.select(emp).where(cb.equal(emp.get("name"), name));
        Employee employee = entityManager.createQuery(c).getSingleResult();
        entityManager.close();
        return employee;
    }

    public List<Employee> listEmployees(int start, int maxResult) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<Employee> employees = entityManager
                .createNamedQuery("listEmployees", Employee.class)
                .setFirstResult(start)
                .setMaxResults(maxResult)
                .getResultList();
        entityManager.close();
        return employees;
    }

    public int findParkingPlaceNumberByEmployeeName(String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        int i =
                entityManager
                        .createQuery("select p.number from Employee e join e.parkingPlace p where e.name = :name",
                                Integer.class)
                        .setParameter("name", name)
                        .getSingleResult();
        entityManager.close();
        return i;
    }

    public List<Object[]> listEmployeeBaseData() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<Object[]> empDatas = entityManager
                .createQuery("select e.id, e.name from Employee e")
                .getResultList();
        entityManager.close();
        return empDatas;
    }

    public List<EmpBaseDataDto> listEmployeeDto() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<EmpBaseDataDto> data =
                entityManager
                        .createQuery("select new jpa.EmpBaseDataDto(e.id, e.name) from Employee e order by e.name")
                        .getResultList();
        entityManager.close();
        return data;
    }

    public void updateToType(LocalDate start, Employee.EmployeeType type) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.
                createQuery("update Employee e set e.employeeType = :type where e.dateOfBirth >= :start")
                .setParameter("type", type)
                .setParameter("start", start)
                .executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void deleteWithoutType() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.
                createQuery("delete Employee e where e.employeeType is null")
                .executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.close();
    }

}

package inheritance;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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

    public Employee findEmployeeByName(String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Employee employee = entityManager.createQuery("select e from Employee e where e.name = :name", Employee.class)
                .setParameter("name", name).getSingleResult();
        entityManager.close();
        return employee;
    }

}

package jpa;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.flywaydb.core.Flyway;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class EmployeeMain {

    public static void main(String[] args) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://localhost/employees");
        dataSource.setUser("employees");
        dataSource.setPassword("employees");

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.clean();
        flyway.migrate();

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("pu");
        EntityManager em = factory.createEntityManager();

        em.getTransaction().begin();
        for (int i = 0; i < 10; i++) {
            Employee employee = new Employee();
            employee.setName("John Doe " + i);
            em.persist(employee);
        }
        em.getTransaction().commit();

        List<Employee> employees = em.createQuery("select e from Employee e order by e.name",
                Employee.class).getResultList();

        System.out.println(employees);

        em.getTransaction().begin();
        Employee employeeToModify = em.find(Employee.class, employees.get(4).getId());
        System.out.println(employeeToModify.getName());

        employeeToModify.setName("Jack Doe");
        em.getTransaction().commit();

        em.getTransaction().begin();
        Employee employeeToDelete = em.find(Employee.class, employees.get(3).getId());
        em.remove(employeeToDelete);
        em.getTransaction().commit();

        List<Employee> newEmployees = em.createQuery("select e from Employee e", Employee.class)
                .getResultList();
        System.out.println(newEmployees);

        em.close();
        factory.close();
    }
}

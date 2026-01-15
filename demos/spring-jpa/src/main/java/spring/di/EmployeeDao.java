package spring.di;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EmployeeDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveEmployee(String name) {
        entityManager.persist(new Employee(name));
    }

    public List<String> listEmployees() {
        return entityManager.createQuery("select e from Employee e order by e.name", Employee.class)
                .getResultList().stream().map(Employee::getName).collect(Collectors.toList());
    }
}

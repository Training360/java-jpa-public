package jpa;

import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ProjectDaoTest {

    private ProjectDao projectDao;

    private EmployeeDao employeeDao;

    @Before
    public void init() {
        EntityManagerFactory factory = Persistence
                .createEntityManagerFactory("pu");
        projectDao = new ProjectDao(factory);
        employeeDao = new EmployeeDao(factory);
    }

    @Test
    public void testSaveProject() {
        Employee john = new Employee("John Doe");
        Employee jane = new Employee("Jane Doe");
        Employee jack = new Employee("Jack Doe");

        employeeDao.saveEmployee(john);
        employeeDao.saveEmployee(jane);
        employeeDao.saveEmployee(jack);

        Project java = new Project("Java");
        Project dotNet = new Project("dotNet");
        Project python = new Project("Python");

        java.addEmployee(john);
        java.addEmployee(jane);

        python.addEmployee(john);
        python.addEmployee(jack);

        dotNet.addEmployee(jack);

        projectDao.saveProject(java);
        projectDao.saveProject(dotNet);
        projectDao.saveProject(python);

        Project project = projectDao.findProjectByName("Java");

        assertEquals(Set.of("John Doe", "Jane Doe"),
                project.getEmployees().stream().map(Employee::getName).collect(Collectors.toSet()));

    }
}

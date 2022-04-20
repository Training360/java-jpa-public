package employees;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDaoTest {

    ProjectDao projectDao;

    EmployeeDao employeeDao;

    @BeforeEach
    void init() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("pu");
        projectDao = new ProjectDao(factory);
        employeeDao = new EmployeeDao(factory);
    }

//    @Test
//    void testSaveProject() {
//        Employee john = new Employee("John Doe");
//        Employee jane = new Employee("Jane Doe");
//        Employee jack = new Employee("Jack Doe");
//
//        employeeDao.saveEmployee(john);
//        employeeDao.saveEmployee(jane);
//        employeeDao.saveEmployee(jack);
//
//        Project java = new Project("Java");
//        Project dotNet = new Project("dotNet");
//        Project python = new Project("Python");
//
//        java.addEmployee(john);
//        java.addEmployee(jane);
//
//        python.addEmployee(john);
//        python.addEmployee(jack);
//
//        dotNet.addEmployee(jack);
//
//        projectDao.saveProject(java);
//        projectDao.saveProject(dotNet);
//        projectDao.saveProject(python);
//
//        Project project = projectDao.findProjectByName("Java");
//
//        assertEquals(Set.of("John Doe", "Jane Doe"),
//                project.getEmployees().stream().map(Employee::getName).collect(Collectors.toSet()));
//    }

    @Test
    public void testSaveThenFind() {
        Project project = new Project("Java");
//        project.getEmployees().put("c123", new Employee("c123", "John Doe"));
//        project.getEmployees().put("c456", new Employee("c456", "Jane Doe"));
        project.getEmployees().put("java_1", new Employee("c123", "John Doe"));
        project.getEmployees().put("java_2", new Employee("c456", "Jane Doe"));

        projectDao.saveProject(project);
        long id = project.getId();

        Project another = projectDao.findById(id);
//        assertEquals("John Doe", another.getEmployees().get("c123").getName());
        assertEquals("Jane Doe", another.getEmployees().get("java_2").getName());
    }
}
package map;

import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.Assert.assertEquals;

public class ProjectDaoTest {

    private ProjectDao projectDao;

    @Before
    public void init() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pu");
        projectDao = new ProjectDao(entityManagerFactory);
    }

    @Test
    public void testSaveThenFind() {
        Project project = new Project("Java");
        project.getEmployees().put("java_1", new Employee("c123", "John Doe"));
        project.getEmployees().put("java_2", new Employee("c456", "Jane Doe"));

        projectDao.saveProject(project);
        long id = project.getId();

        Project another = projectDao.findById(id);
        assertEquals("Jane Doe", another.getEmployees().get("java_2").getName());
    }

}

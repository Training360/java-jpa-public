package map;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectDaoTest {

    ProjectDao projectDao;

    @BeforeEach
    void init() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pu");
        projectDao = new ProjectDao(entityManagerFactory);
    }

    @Test
    void testSaveThenFind() {
        Project project = new Project("Java");
        project.getEmployees().put("java_1", new Employee("c123", "John Doe"));
        project.getEmployees().put("java_2", new Employee("c456", "Jane Doe"));

        projectDao.saveProject(project);
        long id = project.getId();

        Project another = projectDao.findById(id);
        assertEquals("Jane Doe", another.getEmployees().get("java_2").getName());
    }

}

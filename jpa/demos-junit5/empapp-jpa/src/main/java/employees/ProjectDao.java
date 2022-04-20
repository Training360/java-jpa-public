package employees;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ProjectDao {

    EntityManagerFactory entityManagerFactory;

    public ProjectDao(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void saveProject(Project project) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(project);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public Project findProjectById(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Project project = entityManager.find(Project.class, id);
        entityManager.close();
        return project;
    }

    public Project findProjectByName(String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Project project = entityManager.createQuery("select p from Project p join fetch p.employees where name = :name",
                Project.class)
                .setParameter("name", name)
                .getSingleResult();
        entityManager.close();
        return project;
    }

    public Project findById(Long id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        Project project = em.createQuery("select p from Project p join fetch p. employees where p.id = :id",
                Project.class)
                .setParameter("id", id)
                .getSingleResult();
        em.close();
        return project;
    }
}

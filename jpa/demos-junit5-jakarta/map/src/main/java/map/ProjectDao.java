package map;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class ProjectDao {

    private EntityManagerFactory entityManagerFactory;

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

    public Project findById(Long id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        Project project = em.createQuery("select p from Project p join fetch p.employees where p.id = :id",
                Project.class)
                .setParameter("id", id)
                .getSingleResult();
        em.close();
        return project;
    }

}

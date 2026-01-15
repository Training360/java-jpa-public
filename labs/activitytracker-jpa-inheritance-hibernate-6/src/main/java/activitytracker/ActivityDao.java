package activitytracker;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class ActivityDao {

    private EntityManagerFactory entityManagerFactory;

    public ActivityDao(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void saveActivity(Activity activity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(activity);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    public Activity findActivityByDescription(String description) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Activity activity = entityManager.createQuery(
                    "select a from Activity a where a.description = :description", Activity.class)
                    .setParameter("description", description)
                    .getSingleResult();
            return activity;
        } finally {
            entityManager.close();
        }
    }
}

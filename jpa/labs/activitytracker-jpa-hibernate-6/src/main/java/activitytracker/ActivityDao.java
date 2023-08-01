package activitytracker;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;

public class ActivityDao {

    private EntityManagerFactory factory;

    public ActivityDao(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public void saveActivity(Activity activity) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            manager.persist(activity);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public List<Activity> listActivities() {
        EntityManager manager = factory.createEntityManager();
        try {
            List<Activity> activities = manager.createQuery("select a from Activity a", Activity.class)
                    .getResultList();
            return activities;
        } finally {
            manager.close();
        }
    }

    public Activity findActivityById(long id) {
        EntityManager manager = factory.createEntityManager();
        try {
            Activity activity = manager.find(Activity.class, id);
            return activity;
        } finally {
            manager.close();
        }
    }

    public void deleteActivity(long id) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            Activity activity = manager.getReference(Activity.class, id);
            manager.remove(activity);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public void updateActivity(long id, String description) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            Activity activity = manager.find(Activity.class, id);
            activity.setDescription(description);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public Activity findActivityByIdWithLabels(long id) {
        EntityManager manager = factory.createEntityManager();
        try {
            Activity activity = manager
                    .createQuery("select a from Activity a join fetch a.labels where a.id = :id", Activity.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return activity;
        } finally {
            manager.close();
        }
    }

    public Activity findActivityByIdWithTrackPoints(long id) {
        EntityManager manager = factory.createEntityManager();
        try {
            Activity activity = manager
                    .createQuery("select a from Activity a join fetch a.trackPoints where a.id = :id", Activity.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return activity;
        } finally {
            manager.close();
        }
    }

    public List<Coordinate> findTrackPointCoordinatesByDate(LocalDateTime afterThis, int start, int max) {
        EntityManager manager = factory.createEntityManager();
        try {
            List<Coordinate> coordinates = manager
                    .createNamedQuery("findTrackPointCoordinatesByDate")
                    .setParameter("time", afterThis)
                    .setFirstResult(start)
                    .setMaxResults(max)
                    .getResultList();
            return coordinates;
        } finally {
            manager.close();
        }
    }

    public List<Object[]> findTrackPointCountByActivity() {
        EntityManager manager = factory.createEntityManager();
        try {
            List<Object[]> trackPointsCount = manager
                    .createQuery("select description, size(a.trackPoints) from Activity a order by a.description",
                            Object[].class)
                    .getResultList();
            return trackPointsCount;
        } finally {
            manager.close();
        }
    }

    public void removeActivitiesByDateAndType(LocalDateTime afterThis, ActivityType type) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            manager.createQuery("delete Activity a where a.startTime >= :time and a.type = :type")
                    .setParameter("time", afterThis)
                    .setParameter("type", type)
                    .executeUpdate();
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }
}

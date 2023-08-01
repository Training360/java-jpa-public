package activitytracker;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityTrackerMain {

    public static void main(String[] args) {
        ActivityTrackerMain activityTrackerMain = new ActivityTrackerMain();

        Activity firstActivity = new Activity(LocalDateTime.of(2022, 4, 11, 6, 0), "morning running", ActivityType.RUNNING);
        Activity secondActivity = new Activity(LocalDateTime.of(2022, 4, 9, 10, 15), "hiking near Budapest", ActivityType.HIKING);
        Activity thirdActivity = new Activity(LocalDateTime.of(2022, 3, 14, 17, 30), "evening running", ActivityType.RUNNING);

        List<Activity> activities = new ArrayList<>();
        activities.add(firstActivity);
        activities.add(secondActivity);
        activities.add(thirdActivity);

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("pu");
        EntityManager manager = factory.createEntityManager();

        try {
            manager.getTransaction().begin();
            activityTrackerMain.insertActivities(activities, manager);
            manager.getTransaction().commit();

            manager.getTransaction().begin();
            List<Activity> expected = activityTrackerMain.listActivities(manager);
            System.out.println(expected);
            manager.getTransaction().commit();

            manager.getTransaction().begin();
            Activity fourthActivity = new Activity(LocalDateTime.of(2022, 2, 1, 18, 45), "championship in the school", ActivityType.BASKETBALL);
            manager.persist(fourthActivity);

            long id = fourthActivity.getId();
            Activity result = activityTrackerMain.findActivity(id, manager);
            System.out.println(result);
            manager.getTransaction().commit();

            manager.getTransaction().begin();
            result.setDescription("playing in the garden");
            manager.getTransaction().commit();
            expected = activityTrackerMain.listActivities(manager);
            System.out.println(expected);
            manager.getTransaction().begin();
            result = activityTrackerMain.findActivity(id, manager);
            System.out.println(result);
            manager.getTransaction().commit();

            manager.getTransaction().begin();
            manager.remove(result);
            manager.getTransaction().commit();

            manager.getTransaction().begin();
            expected = activityTrackerMain.listActivities(manager);
            System.out.println(expected);
            manager.getTransaction().commit();

        } finally {
            manager.close();
            factory.close();
        }
    }

    private void insertActivities(List<Activity> activities, EntityManager manager) {
        for (Activity activity : activities) {
            manager.persist(activity);
        }
    }

    private List<Activity> listActivities(EntityManager manager) {
        List<Activity> result = manager.createQuery("select a from Activity a", Activity.class)
                .getResultList();
        return result;
    }

    private Activity findActivity(long id, EntityManager manager) {
        Activity expected = manager.find(Activity.class, id);
        return expected;
    }
}

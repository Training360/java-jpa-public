package activitytracker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ActivityDaoTest {

    EntityManagerFactory factory;

    ActivityDao activityDao;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("pu");
        activityDao = new ActivityDao(factory);
    }

    @AfterEach
    void close() {
        factory.close();
    }

    @Test
    void testSaveActivity() {
        Activity activity = new Activity(LocalDateTime.of(2022, 4, 11, 6, 0), "morning running", ActivityType.RUNNING);
        activityDao.saveActivity(activity);

        long id = activity.getId();

        Activity expected = activityDao.findActivityById(id);

        assertEquals("morning running", expected.getDescription());
        assertEquals(ActivityType.RUNNING, expected.getType());
    }

    @Test
    void testListActivities() {
        Activity firstActivity = new Activity(LocalDateTime.of(2022, 4, 11, 6, 0), "morning running", ActivityType.RUNNING);
        Activity secondActivity = new Activity(LocalDateTime.of(2022, 4, 9, 10, 15), "hiking near Budapest", ActivityType.HIKING);
        Activity thirdActivity = new Activity(LocalDateTime.of(2022, 3, 14, 17, 30), "evening running", ActivityType.RUNNING);
        activityDao.saveActivity(firstActivity);
        activityDao.saveActivity(secondActivity);
        activityDao.saveActivity(thirdActivity);

        List<Activity> expected = activityDao.listActivities();

        assertEquals(3, expected.size());
        assertThat(expected)
                .extracting(Activity::getDescription)
                .contains("morning running", "hiking near Budapest", "evening running");
    }

    @Test
    void testDeleteActivity() {
        Activity activity = new Activity(LocalDateTime.of(2022, 4, 11, 6, 0), "morning running", ActivityType.RUNNING);
        activityDao.saveActivity(activity);

        long id = activity.getId();
        activityDao.deleteActivity(id);
        List<Activity> expected = activityDao.listActivities();

        assertEquals(0, expected.size());
    }

    @Test
    void testUpdateActivity() {
        Activity activity = new Activity(LocalDateTime.of(2022, 4, 11, 6, 0), "morning running", ActivityType.RUNNING);
        activityDao.saveActivity(activity);
        assertTrue(activity.getCreatedAt() != null);

        long id = activity.getId();
        activityDao.updateActivity(id, "running at 6 o'clock");
        assertTrue(activity.getUpdatedAt() != null);

        Activity expected = activityDao.findActivityById(id);

        assertEquals("running at 6 o'clock", expected.getDescription());
        assertEquals(ActivityType.RUNNING, expected.getType());
    }

    @Test
    void testFindActivityByIdWithLabels() {
        Activity activity = new Activity(LocalDateTime.of(2022, 4, 11, 6, 0), "morning running", ActivityType.RUNNING);
        activity.setLabels(Arrays.asList("morning", "running"));
        activityDao.saveActivity(activity);
        Activity expected = activityDao.findActivityByIdWithLabels(activity.getId());

        assertNotNull(expected.getLabels());
        assertEquals(2, expected.getLabels().size());
        assertEquals(List.of("morning", "running"), expected.getLabels());
    }

    @Test
    void testFindActivityByIdWithTrackPoints() {
        Activity activity = new Activity(LocalDateTime.of(2022, 4, 11, 6, 0), "morning running", ActivityType.RUNNING);
        activity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 2, 3), 47.497912, 19.040235));
        activity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 4, 5), -33.88223, 151.33140));
        activity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 3, 4), 48.87376, 2.25120));
        activityDao.saveActivity(activity);

        Activity expected = activityDao.findActivityByIdWithTrackPoints(activity.getId());

        assertEquals(3, expected.getTrackPoints().size());
        assertEquals(-33.88223, expected.getTrackPoints().get(2).getLatitude());
        assertEquals(2.25120, expected.getTrackPoints().get(1).getLongitude());
    }

    @Test
    void testActivityWithDetails() {
        Activity activity = new Activity(LocalDateTime.of(2022, 4, 11, 6, 0), "morning running", ActivityType.RUNNING);
        activity.setDistance(12.7);
        activity.setDuration(10800);
        activityDao.saveActivity(activity);

        Activity expected = activityDao.findActivityById(activity.getId());

        assertEquals(12.7, expected.getDistance());
        assertEquals(10800, expected.getDuration());
    }

    @Test
    void testFindTrackPointCoordinatesByDate() {
        Activity wrongActivity = new Activity(LocalDateTime.of(2017, 4, 11, 16, 0), "evening running", ActivityType.RUNNING);
        wrongActivity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 2, 3), 1, 1));
        wrongActivity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 4, 5), 2, 2));
        wrongActivity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 3, 4), 3, 3));
        wrongActivity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 3, 4), 4, 4));
        activityDao.saveActivity(wrongActivity);
        Activity rightActivity = new Activity(LocalDateTime.of(2022, 4, 11, 6, 0), "morning running", ActivityType.RUNNING);
        rightActivity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 2, 3), 47.497912, 19.040235));
        rightActivity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 4, 5), 33.88223, 151.33140));
        rightActivity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 3, 4), 48.87376, 2.25120));
        rightActivity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 3, 5), 23.87376, 4.25120));
        activityDao.saveActivity(rightActivity);

        List<Coordinate> expected = activityDao.findTrackPointCoordinatesByDate(LocalDateTime.of(2018, 1, 1, 0, 0), 1, 2);

        assertEquals(2, expected.size());
        assertEquals(48.87376, expected.get(0).getLatitude());
        assertEquals(4.2512, expected.get(1).getLongitude());
    }

    @Test
    void testFindTrackPointCountByActivity() {
        Activity activity = new Activity(LocalDateTime.of(2022, 4, 11, 6, 0), "morning running", ActivityType.RUNNING);
        activity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 2, 3), 47.497912, 19.040235));
        activity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 4, 5), -33.88223, 151.33140));
        activity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 3, 4), 48.87376, 2.25120));
        activityDao.saveActivity(activity);

        Activity secondActivity = new Activity(LocalDateTime.of(2022, 3, 14, 17, 30), "evening running", ActivityType.RUNNING);
        secondActivity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 2, 3), 47.497912, 19.040235));
        secondActivity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 4, 5), -33.88223, 151.33140));
        activityDao.saveActivity(secondActivity);

        Activity thirdActivity = new Activity(LocalDateTime.of(2022, 4, 9, 10, 15), "hiking near Budapest", ActivityType.HIKING);
        thirdActivity.addTrackPoint(new TrackPoint(LocalDate.of(2021, 2, 3), 47.497912, 19.040235));
        activityDao.saveActivity(thirdActivity);

        List<Object[]> expected = activityDao.findTrackPointCountByActivity();

        Object[] firstActivityData = new Object[]{"morning running", 3};
        Object[] secondActivityData = new Object[]{"evening running", 2};
        Object[] thirdActivityData = new Object[]{"hiking near Budapest", 1};

        assertEquals(3, expected.size());
        assertArrayEquals(firstActivityData, expected.get(2));
        assertArrayEquals(secondActivityData, expected.get(0));
        assertArrayEquals(thirdActivityData, expected.get(1));
    }

    @Test
    void testRemoveActivitiesByDateAndType() {
        Activity firstActivity = new Activity(LocalDateTime.of(2022, 4, 11, 6, 0), "morning running", ActivityType.RUNNING);
        Activity secondActivity = new Activity(LocalDateTime.of(2022, 4, 9, 10, 15), "hiking near Budapest", ActivityType.HIKING);
        Activity thirdActivity = new Activity(LocalDateTime.of(2022, 3, 14, 17, 30), "evening running", ActivityType.RUNNING);
        Activity fourthActivity = new Activity(LocalDateTime.of(2022, 4, 14, 17, 30), "running", ActivityType.RUNNING);
        Activity fifthActivity = new Activity(LocalDateTime.of(2022, 3, 15, 17, 30), "always running", ActivityType.RUNNING);
        activityDao.saveActivity(firstActivity);
        activityDao.saveActivity(secondActivity);
        activityDao.saveActivity(thirdActivity);
        activityDao.saveActivity(fourthActivity);
        activityDao.saveActivity(fifthActivity);

        activityDao.removeActivitiesByDateAndType(LocalDateTime.of(2022, 4, 1, 0, 0), ActivityType.RUNNING);
        List<Activity> expected = activityDao.listActivities();

        assertEquals(3, expected.size());
        assertThat(expected)
                .extracting(Activity::getDescription)
                .contains("hiking near Budapest", "evening running", "always running")
                .doesNotContain("morning running", "running");
    }
}